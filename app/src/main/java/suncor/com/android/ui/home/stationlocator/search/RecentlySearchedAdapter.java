package suncor.com.android.ui.home.stationlocator.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.RecentlySearchedItemBinding;
import suncor.com.android.utilities.Consumer;

public class RecentlySearchedAdapter extends RecyclerView.Adapter<RecentlySearchedAdapter.RecentlySearchedHolder> {
    private  Consumer<RecentSearch> clickCallback;
    private ArrayList<RecentSearch> recentSearches;


    public RecentlySearchedAdapter(Consumer<RecentSearch> clickCallback, ArrayList<RecentSearch> recentSearches) {
        this.clickCallback = clickCallback;
        this.recentSearches = recentSearches;
    }

    @NonNull
    @Override
    public RecentlySearchedAdapter.RecentlySearchedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecentlySearchedItemBinding recentlySearchedLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.recently_searched_item, parent, false);
        return new RecentlySearchedAdapter.RecentlySearchedHolder(recentlySearchedLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlySearchedAdapter.RecentlySearchedHolder holder, int position) {
        final RecentSearch recentSearch = recentSearches.get(position);
        holder.binding.setRecentlysearched(recentSearch);
        holder.binding.executePendingBindings();
        holder.binding.getRoot().setOnClickListener((v) -> {
            clickCallback.accept(recentSearch);
        });
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    public class RecentlySearchedHolder extends RecyclerView.ViewHolder {
        RecentlySearchedItemBinding binding;

        public RecentlySearchedHolder(@NonNull RecentlySearchedItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
