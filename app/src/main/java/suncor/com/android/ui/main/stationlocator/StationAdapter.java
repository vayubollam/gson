package suncor.com.android.ui.main.stationlocator;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import suncor.com.android.data.DistanceApi;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.utilities.NavigationAppsHelper;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.StationViewHolder> {

    private final ArrayList<StationItem> stations = new ArrayList<>();
    private final StationsFragment fragment;
    private final BottomSheetBehavior bottomSheetBehavior;
    private DistanceApi distanceApi;
    private LatLng userLocation;

    public StationAdapter(StationsFragment fragment, BottomSheetBehavior bottomSheetBehavior, DistanceApi distanceApi) {
        this.fragment = fragment;
        this.bottomSheetBehavior = bottomSheetBehavior;
        this.distanceApi = distanceApi;
    }

    public ArrayList<StationItem> getStations() {
        return stations;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        for (StationItem stationItem : stations) {
            stationItem.setDistanceDuration(null);
        }
        notifyDataSetChanged();
    }

    public void setStationItems(ArrayList<StationItem> stations) {
        this.stations.clear();
        this.stations.addAll(stations);
        notifyDataSetChanged();
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


        if (userLocation == null) {
            stationItem.setDistanceDuration(DirectionsResult.INVALID);
        } else if (stationItem.getDistanceDuration() == null) {
            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
            distanceApi.enqueuJob(userLocation, dest)
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
                StationDetailsDialog.showCard(fragment, stationItem, holder.itemView, true);

            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        });
        holder.binding.executePendingBindings();
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
