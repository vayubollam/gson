package suncor.com.android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import suncor.com.android.constants.GeneralConstants;
import suncor.com.android.dataObjects.Resource;
import suncor.com.android.dataObjects.Station;
import suncor.com.android.dataObjects.StationMatrix;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.dialogs.OpenWithDialog;
import suncor.com.android.dialogs.StationDetailsDialog;
import suncor.com.android.fragments.StationViewModel;
import suncor.com.android.utilities.NavigationAppsHelper;
import suncor.com.android.workers.DirectionsWorker;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final ArrayList<StationViewModel> stations = new ArrayList<>();
    private final FragmentActivity activity;
    private final BottomSheetBehavior bottomSheetBehavior;
    private final SharedPreferences prefs;
    public static final String ORIGIN_LAT = "origin_lat";
    public static final String ORIGIN_LNG = "origin_lng";
    public static final String DEST_LAT = "dest_lat";
    public static final String DEST_LNG = "dest_lng";
    private LatLng userLocation;
    private GestureDetectorCompat swipeUpDetector;

    public ArrayList<StationViewModel> getStations() {
        return stations;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    public StationAdapter(FragmentActivity activity, BottomSheetBehavior bottomSheetBehavior) {
        this.activity = activity;
        this.bottomSheetBehavior = bottomSheetBehavior;
        prefs = activity.getSharedPreferences(GeneralConstants.USER_PREFS_NAME, Context.MODE_PRIVATE);
        swipeUpDetector = new GestureDetectorCompat(activity, new GestureDetector.SimpleOnGestureListener() {
            private boolean isSwipeUpDetected = false;

            @Override
            public boolean onDown(MotionEvent e) {
                isSwipeUpDetected = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!isSwipeUpDetected && velocityY > 0) {
                    isSwipeUpDetected = true;
                    return true;
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!isSwipeUpDetected && distanceY > 0) {
                    isSwipeUpDetected = true;
                    return true;
                }
                return false;
            }
        });
    }

    private ArrayList<StationViewModel> convertToViewModel(ArrayList<Station> stations) {
        ArrayList<StationViewModel> models = new ArrayList<>();
        for (Station station : stations) {
            models.add(new StationViewModel(station));
        }
        return models;
    }

    @NonNull
    @Override
    public StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardStationItemBinding binding = CardStationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.detailsLayout.setNestedScrollingEnabled(false);
        return new StationViewHolder(binding);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull StationViewHolder holder, int position) {
        final StationViewModel stationViewModel = stations.get(position);
        final Station station = stationViewModel.station.get();
        holder.binding.setVm(stationViewModel);
        holder.binding.txtStationTitle.setText(station.getAddress().getAddressLine());

        if (stationViewModel.distanceDuration.get() == null) {
            Data locationData = new Data.Builder()
                    .putDouble(DEST_LAT, station.getAddress().getLatitude())
                    .putDouble(DEST_LNG, station.getAddress().getLongitude())
                    .putDouble(ORIGIN_LAT, userLocation.latitude)
                    .putDouble(ORIGIN_LNG, userLocation.longitude)
                    .build();
            Constraints myConstraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            OneTimeWorkRequest getDirectionsWork = new OneTimeWorkRequest
                    .Builder(DirectionsWorker.class)
                    .setConstraints(myConstraints)
                    .setInputData(locationData)
                    .build();
            WorkManager.getInstance().enqueue(getDirectionsWork);
            WorkManager.getInstance().getWorkInfoByIdLiveData(getDirectionsWork.getId())
                    .observe(activity, workInfo -> {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                            String distance = workInfo.getOutputData().getString("distance");
                            String duration = workInfo.getOutputData().getString("duration");
                            StationMatrix distanceDuration = new StationMatrix(distance, duration);
                            stationViewModel.distanceDuration.set(distanceDuration);
                        }
                    });
        }


        holder.binding.btnCardDirections.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            NavigationAppsHelper helper = new NavigationAppsHelper(activity);
            helper.openNavigationApps(station);
        });

        holder.binding.imgBottomSheet.setOnClickListener((v) -> {
            showStationDetails(stationViewModel, holder.itemView);
        });
        holder.binding.getRoot().setOnTouchListener((view, event) -> {
            boolean eventHandled = swipeUpDetector.onTouchEvent(event);
            boolean isSwipeUp = eventHandled && event.getAction() != MotionEvent.ACTION_DOWN;
            if (isSwipeUp) {
                showStationDetails(stationViewModel, holder.itemView);
            }
            return eventHandled;
        });

        holder.binding.executePendingBindings();
    }

    private void showStationDetails(StationViewModel stationViewModel, View itemView) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }
        StationDetailsDialog dialog = new StationDetailsDialog();
        dialog.setIntialHeight(itemView.getHeight());
        int[] position = new int[2];
        itemView.getLocationInWindow(position);
        dialog.setIntialPosition(position[1]);
        dialog.setStationViewModel(stationViewModel);
        dialog.show(activity.getSupportFragmentManager(), StationDetailsDialog.TAG);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }


    class StationViewHolder extends RecyclerView.ViewHolder {

        final CardStationItemBinding binding;
        final int screenWidth = getScreenWidth();


        StationViewHolder(CardStationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private int getScreenWidth() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            return displayMetrics.widthPixels;
        }
    }

}
