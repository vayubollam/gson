package suncor.com.android.ui.home.stationlocator.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.SearchNearbyItemBinding;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.Consumer;

public class SearchNearByAdapter extends RecyclerView.Adapter<SearchNearByAdapter.NearByHolder> {
    private final Consumer<StationItem> clickCallback;
    private ArrayList<StationItem> stationItems;

    public SearchNearByAdapter(ArrayList<StationItem> stationItems, Consumer<StationItem> clickCallback) {
        this.stationItems = stationItems;
        this.clickCallback = clickCallback;
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
        final StationItem stationItem = stationItems.get(position);
        holder.binding.setStationItem(stationItem);
        holder.binding.executePendingBindings();
        holder.binding.getRoot().setOnClickListener((v) -> {
            clickCallback.accept(stationItem);
        });
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
