package suncor.com.android.ui.home.stationlocator.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.SearchNearbyItemBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;

public class SearchNearByAdapter extends RecyclerView.Adapter<SearchNearByAdapter.NearByHolder> {
    private ArrayList<StationNearbyItem> stationItems;
    private LatLng userLocation;
    private FragmentActivity activity;

    public SearchNearByAdapter(ArrayList<StationNearbyItem> stationItems, LatLng userLocation, FragmentActivity activity) {
        this.stationItems = stationItems;
        this.userLocation = userLocation;
        this.activity = activity;
    }


    @NonNull
    @Override
    public NearByHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchNearbyItemBinding nearbyItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.search_nearby_item, parent, false);
        return new NearByHolder(nearbyItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchNearByAdapter.NearByHolder holder, int position) {
        final StationNearbyItem stationItem = stationItems.get(position);
        holder.binding.setStationItem(stationItem);
        holder.binding.executePendingBindings();

    }

    @Override
    public int getItemCount() {
        return stationItems.size();
    }

    public class NearByHolder extends RecyclerView.ViewHolder {
        SearchNearbyItemBinding binding;

        public NearByHolder(@NonNull SearchNearbyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
