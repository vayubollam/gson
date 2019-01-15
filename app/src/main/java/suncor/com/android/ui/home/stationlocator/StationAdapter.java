package suncor.com.android.ui.home.stationlocator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import suncor.com.android.GeneralConstants;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final ArrayList<StationItem> stations = new ArrayList<>();
    private final FragmentActivity activity;
    private final BottomSheetBehavior bottomSheetBehavior;
    private final SharedPreferences prefs;
    public static final String ORIGIN_LAT = "origin_lat";
    public static final String ORIGIN_LNG = "origin_lng";
    public static final String DEST_LAT = "dest_lat";
    public static final String DEST_LNG = "dest_lng";
    private LatLng userLocation;
    private GestureDetectorCompat swipeUpDetector;


    public ArrayList<StationItem> getStations() {
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

    private ArrayList<StationItem> convertToViewModel(ArrayList<Station> stations) {
        ArrayList<StationItem> models = new ArrayList<>();
        for (Station station : stations) {
            models.add(new StationItem(station));
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
        final StationItem stationItem = stations.get(position);
        final Station station = stationItem.station.get();
        holder.binding.setVm(stationItem);
        holder.binding.txtStationTitle.setText(station.getAddress().getAddressLine());

        if (stationItem.distanceDuration.get() == null) {

            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
            DirectionsApi.getInstance().enqueuJob(userLocation, dest)
                    .observe(activity, result -> { //TODO choose right lifecycle owner
                        holder.binding.brKmCard.setVisibility(result.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                        if (result.status == Resource.Status.SUCCESS) {
                            stationItem.distanceDuration.set(result.data);
                        }
                        //TODO handle error
                    });
        }


        holder.binding.btnCardDirections.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            NavigationAppsHelper.openNavigationApps(activity, station);
        });

        holder.binding.imgBottomSheet.setOnClickListener((v) -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                showStationDetails(stationItem, holder.itemView);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        holder.binding.getRoot().setOnTouchListener((view, event) -> {
            boolean eventHandled = swipeUpDetector.onTouchEvent(event);
            boolean isSwipeUp = eventHandled && event.getAction() != MotionEvent.ACTION_DOWN;
            if (isSwipeUp) {
                showStationDetails(stationItem, holder.itemView);
            }
            return eventHandled;
        });

        holder.binding.executePendingBindings();
    }

    private void showStationDetails(StationItem stationItem, View itemView) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }
        StationDetailsDialog dialog = new StationDetailsDialog();
        dialog.setIntialHeight(itemView.getHeight());
        int[] position = new int[2];
        itemView.getLocationInWindow(position);
        dialog.setIntialPosition(position[1]);
        dialog.setStationItem(stationItem);
        dialog.show(activity.getSupportFragmentManager(), StationDetailsDialog.TAG);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }


    class StationViewHolder extends RecyclerView.ViewHolder {

        final CardStationItemBinding binding;


        StationViewHolder(CardStationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}
