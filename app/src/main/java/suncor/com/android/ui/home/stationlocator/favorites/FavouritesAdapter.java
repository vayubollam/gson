package suncor.com.android.ui.home.stationlocator.favorites;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.api.DirectionsApi;
import suncor.com.android.databinding.CardStationItemBinding;
import suncor.com.android.model.DirectionsResult;
import suncor.com.android.model.Resource;
import suncor.com.android.model.Station;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.NavigationAppsHelper;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavoriteHolder> {
    ArrayList<StationItem> stationItems;
    LatLng userLocation = null;
    FragmentActivity activity;

    public FavouritesAdapter() {
    }

    public void setStationItems(ArrayList<StationItem> stationItems) {
        this.stationItems = stationItems;
    }

    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardStationItemBinding binding = CardStationItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.detailsLayout.setNestedScrollingEnabled(false);
        binding.imgBottomSheet.setVisibility(View.INVISIBLE);
        setMargins(binding.getRoot(), 0, 16, 0, 0);
        binding.getRoot().setElevation(pxFromDp(activity, 8));
        return new FavoriteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteHolder holder, int position) {
        final StationItem stationItem = stationItems.get(position);
        final Station station = stationItem.getStation();
        holder.binding.setVm(stationItem);
        holder.binding.stationTitleText.setText(station.getAddress().getAddressLine());

        if (userLocation == null) {
            stationItem.setDistanceDuration(new DirectionsResult(-1, -1));
        } else if (stationItem.getDistanceDuration() == null) {
            LatLng dest = new LatLng(station.getAddress().getLatitude(), station.getAddress().getLongitude());
            DirectionsApi.getInstance().enqueuJob(userLocation, dest)
                    .observe(activity, result -> { //TODO choose right lifecycle owner
                        if (result.status == Resource.Status.SUCCESS) {
                            stationItem.setDistanceDuration(result.data);
                        } else if (result.status == Resource.Status.ERROR) {
                            stationItem.setDistanceDuration(DirectionsResult.INVALID);
                        }
                    });
        }


        holder.binding.directionsButton.setOnClickListener(v -> {
            if (activity != null) {
                NavigationAppsHelper.openNavigationApps(activity, station);
            }
        });


        holder.binding.executePendingBindings();

    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, top, activity.getResources()
                        .getDisplayMetrics());
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, marginInDp, right, bottom);
            view.requestLayout();
        }
    }

    public float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getItemCount() {
        return stationItems.size();
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder {
        CardStationItemBinding binding;

        public FavoriteHolder(@NonNull CardStationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
