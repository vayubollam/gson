package suncor.com.android.ui.main.stationlocator.favourites;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.data.DistanceApi;
import suncor.com.android.databinding.SwipeableStationItemBinding;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.stationlocator.StationItem;
import suncor.com.android.uicomponents.swipe.Attributes;
import suncor.com.android.uicomponents.swipe.RecyclerSwipeAdapter;
import suncor.com.android.utilities.NavigationAppsHelper;

public class FavouritesAdapter extends RecyclerSwipeAdapter<FavouritesAdapter.FavoriteHolder> {
    private ArrayList<StationItem> stationItems = new ArrayList<>();
    private LatLng userLocation = null;
    private FavouritesFragment fragment;
    private DistanceApi distanceApi;

    public FavouritesAdapter(FavouritesFragment fragment, DistanceApi distanceApi) {
        this.fragment = fragment;
        this.distanceApi = distanceApi;
        setMode(Attributes.Mode.Single);
    }

    public void setStationItems(ArrayList<StationItem> stationItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallBack(this.stationItems, stationItems));
        this.stationItems.clear();
        this.stationItems.addAll(stationItems);
        diffResult.dispatchUpdatesTo(this);
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        for (StationItem item : stationItems) {
            item.setDistanceDuration(DirectionsResult.INVALID);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SwipeableStationItemBinding binding = SwipeableStationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.card.getRoot().setPadding(0, 0, 0, 0);
        binding.card.cardView.setElevation(0);
        binding.card.detailsLayout.setNestedScrollingEnabled(false);
        binding.card.imgBottomSheet.setVisibility(View.GONE);
        return new FavoriteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        final StationItem stationItem = stationItems.get(position);
        final Station station = stationItem.getStation();
        holder.binding.setVm(stationItem);
        holder.binding.card.stationTitleText.setText(station.getAddress().getAddressLine());

        if (userLocation == null) {
            stationItem.setDistanceDuration(new DirectionsResult(-1, -1));
        } else if (stationItem.getDistanceDuration() == null) {
            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
            distanceApi.enqueuJob(userLocation, dest)
                    .observe(fragment, result -> { //choose right lifecycle owner
                        if (result.status == Resource.Status.SUCCESS) {
                            stationItem.setDistanceDuration(result.data);
                        } else if (result.status == Resource.Status.ERROR) {
                            stationItem.setDistanceDuration(DirectionsResult.INVALID);
                        }
                    });
        }

        holder.binding.card.directionsButton.setOnClickListener(v -> {
            if (fragment != null) {
                NavigationAppsHelper.openNavigationApps(fragment.getContext(), station);
            }
        });

        holder.binding.binButton.setOnClickListener((v) -> {
            fragment.removeFavourite(stationItem);
            closeAllItems();
        });

        holder.binding.executePendingBindings();
        mItemManger.bind(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return stationItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeable_layout;
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder {
        SwipeableStationItemBinding binding;

        public FavoriteHolder(@NonNull SwipeableStationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class DiffCallBack extends DiffUtil.Callback {

        private ArrayList<StationItem> oldList;
        private ArrayList<StationItem> newList;

        public DiffCallBack(ArrayList<StationItem> oldList, ArrayList<StationItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList == null ? 0 : oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList == null ? 0 : newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition) == newList.get(newItemPosition);
        }
    }
}
