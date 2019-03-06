package suncor.com.android.ui.home.stationlocator.favourites;

import android.Manifest;
import android.animation.AnimatorListenerAdapter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FavouritesFragmentBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.utilities.LocationUtils;
import suncor.com.android.utilities.UserLocalSettings;

public class FavouritesFragment extends Fragment {

    private static final String SHOW_FAVS_HINT = "show_favs_hint";
    private FavouritesViewModel mViewModel;
    public static String FAVOURITES_FRAGMENT_TAG = "FAVOURITES_FRAGMENT";
    private FavouritesAdapter favouritesAdapter;
    private FavouritesFragmentBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(true);
    private ObservableBoolean noResult = new ObservableBoolean(false);
    private ObservableBoolean appBarDragable = new ObservableBoolean(false);
    private LocationLiveData locationLiveData;

    public static FavouritesFragment newInstance() {
        return new FavouritesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));
        binding = FavouritesFragmentBinding.inflate(inflater, container, false);
        binding.setIsLoading(isLoading);
        binding.setNoResult(noResult);
        binding.setAppBarDragable(appBarDragable);
        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.appBar.setNavigationOnClickListener(this::goBack);
        binding.favouriteRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        favouritesAdapter = new FavouritesAdapter(this);
        binding.favouriteRecycler.setAdapter(favouritesAdapter);
        binding.favouriteRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    favouritesAdapter.closeAllItems();
                }
            }
        });

        locationLiveData = new LocationLiveData(getContext());
        view.requestApplyInsets();
        binding.addFavoriteBackButton.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FavouritesViewModelFactory favouritesViewModelFactory = new FavouritesViewModelFactory(SuncorApplication.favouriteRepository);
        mViewModel = ViewModelProviders.of(this, favouritesViewModelFactory).get(FavouritesViewModel.class);
        mViewModel.stations.observe(this, stations -> {
            isLoading.set(false);
            if (stations.size() == 0) {
                noResult.set(true);
                appBarDragable.set(false);
            } else {
                noResult.set(false);
                binding.favouriteRecycler.setVisibility(View.VISIBLE);
                favouritesAdapter.setStationItems(stations);

                if (UserLocalSettings.getBool(SHOW_FAVS_HINT, true)) {
                    binding.favouriteRecycler.post(() -> {
                        FavouritesAdapter.FavoriteHolder viewHolder = (FavouritesAdapter.FavoriteHolder) binding.favouriteRecycler.findViewHolderForAdapterPosition(0);
                        viewHolder.binding.swipeableLayout.showHint(getResources().getDimensionPixelSize(R.dimen.station_remove_hint_width));
                    });
                    UserLocalSettings.setBool(SHOW_FAVS_HINT, false);
                }

                //Check if recyclerview content is not fully visible then enable dragging for appbar layout
                LinearLayoutManager layoutManager = (LinearLayoutManager) binding.favouriteRecycler.getLayoutManager();
                appBarDragable.set(layoutManager.findLastCompletelyVisibleItemPosition() < favouritesAdapter.getItemCount() - 1);
            }

        });
        if (LocationUtils.isLocationEnabled()
                && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Observer<Location> locationObserver = new Observer<Location>() {
                @Override
                public void onChanged(Location location) {
                    mViewModel.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    favouritesAdapter.setUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    mViewModel.refreshStations();
                    locationLiveData.removeObserver(this);
                }
            };
            locationLiveData.observe(this, locationObserver);
        } else {
            mViewModel.refreshStations();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void goBack(View view) {
        getFragmentManager().popBackStack();
    }


    public void removeFavourite(StationItem stationItem) {
        //override recyclerview animation after remove to avoid overriding first loading animation
        binding.favouriteRecycler.setItemAnimator(new Animator());
        mViewModel.removeStation(stationItem).observe(getActivity(), (r) -> {
            if (r.status == Resource.Status.SUCCESS) {
                SuncorToast.makeText(SuncorApplication.getInstance(), "Station removed", Toast.LENGTH_SHORT).show();
            } else if (r.status == Resource.Status.ERROR) {
                Toast.makeText(SuncorApplication.getInstance(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class Animator extends DefaultItemAnimator {

        @Override
        public boolean animateAdd(RecyclerView.ViewHolder holder) {
            final View view = holder.itemView;
            view.setTranslationX(-holder.itemView.getRootView().getWidth());
            final ViewPropertyAnimator animation = view.animate();
            animation.setDuration(getRemoveDuration()).translationX(0).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(android.animation.Animator animator) {
                            dispatchAddStarting(holder);
                        }

                        @Override
                        public void onAnimationEnd(android.animation.Animator animator) {
                            animation.setListener(null);
                            view.setTranslationX(0);
                            dispatchAddFinished(holder);
                        }
                    }).start();
            return true;
        }

        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            final View view = holder.itemView;
            final ViewPropertyAnimator animation = view.animate();
            animation.setDuration(getRemoveDuration()).translationX(-holder.itemView.getRootView().getWidth()).setListener(
                    new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(android.animation.Animator animator) {
                            dispatchRemoveStarting(holder);
                        }

                        @Override
                        public void onAnimationEnd(android.animation.Animator animator) {
                            animation.setListener(null);
                            view.setTranslationX(0);
                            dispatchRemoveFinished(holder);
                        }
                    }).start();
            return true;
        }
    }
}
