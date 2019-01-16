package suncor.com.android.ui.home.stationlocator;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final ArrayList<StationItem> stations = new ArrayList<>();
    private final StationsFragment fragment;
    private final BottomSheetBehavior bottomSheetBehavior;
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

    public StationAdapter(StationsFragment fragment, BottomSheetBehavior bottomSheetBehavior) {
        this.fragment = fragment;
        this.bottomSheetBehavior = bottomSheetBehavior;
        swipeUpDetector = new GestureDetectorCompat(fragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
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
                    .observe(fragment, result -> { //TODO choose right lifecycle owner
                        holder.binding.brKmCard.setVisibility(result.status == Resource.Status.LOADING ? View.VISIBLE : View.GONE);
                        if (result.status == Resource.Status.SUCCESS) {
                            stationItem.distanceDuration.set(result.data);
                        }
                        //TODO handle error
                    });
        }


        holder.binding.btnCardDirections.setOnClickListener(v -> {
            if (fragment.getContext() != null) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                NavigationAppsHelper.openNavigationApps(fragment.getContext(), station);
            }
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
        dialog.setTargetFragment(fragment, StationsFragment.STATION_DETAILS_REQUEST_CODE);
        dialog.show(fragment.getFragmentManager(), StationDetailsDialog.TAG);
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
