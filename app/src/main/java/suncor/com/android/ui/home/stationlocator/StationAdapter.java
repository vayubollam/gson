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
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final ArrayList<StationItem> stations = new ArrayList<>();
    private final StationsFragment fragment;
    private final BottomSheetBehavior bottomSheetBehavior;
    private LatLng userLocation;

    public ArrayList<StationItem> getStations() {
        return stations;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    public StationAdapter(StationsFragment fragment, BottomSheetBehavior bottomSheetBehavior) {
        this.fragment = fragment;
        this.bottomSheetBehavior = bottomSheetBehavior;
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
        final Station station = stationItem.getStation();
        holder.binding.setVm(stationItem);
        holder.binding.stationTitleText.setText(station.getAddress().getAddressLine());

        if (userLocation != null && stationItem.getDistanceDuration() == null) {
            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
            DirectionsApi.getInstance().enqueuJob(userLocation, dest)
                    .observe(fragment, result -> {
                        if (result.status == Resource.Status.SUCCESS) {
                            stationItem.setDistanceDuration(result.data);
                        } else if (result.status == Resource.Status.ERROR) {
                            stationItem.setDistanceDuration(DirectionsResult.INVALID);
                        }
                    });
        }

        holder.binding.directionsButton.setOnClickListener(v -> {
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
        holder.itemView.setOnTouchListener(new CardTouchListener(stationItem, holder.itemView));

        holder.binding.executePendingBindings();
    }

    private void showStationDetails(StationItem stationItem, View itemView) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            return;
        }
        StationDetailsDialog dialog = new StationDetailsDialog();
        dialog.setInitialHeight(itemView.getHeight());
        int[] position = new int[2];
        itemView.getLocationOnScreen(position);
        dialog.setInitialPosition(position[1]);
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

    private class CardTouchListener implements View.OnTouchListener {

        StationItem item;
        View view;

        GestureDetectorCompat swipeUpDetector = new GestureDetectorCompat(fragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
            boolean isSwipeUpDetected;

            @Override
            public boolean onDown(MotionEvent e) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }
                isSwipeUpDetected = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (!isSwipeUpDetected) {
                    isSwipeUpDetected = true;
                    if (velocityY > 0) {
                        showStationDetails(item, view);
                    } else if (velocityY < 0) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (!isSwipeUpDetected) {
                    isSwipeUpDetected = true;
                    if (distanceY > 0) {
                        showStationDetails(item, view);
                    } else if (distanceY < 0) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    return true;
                }
                return false;
            }
        });

        CardTouchListener(StationItem item, View view) {
            this.item = item;
            this.view = view;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return swipeUpDetector.onTouchEvent(event);
        }
    }

}
