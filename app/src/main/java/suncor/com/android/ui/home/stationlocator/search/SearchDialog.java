package suncor.com.android.ui.home.stationlocator.search;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.utilities.LocationUtils;

public class SearchDialog extends DialogFragment implements View.OnClickListener {
    SearchViewModel stationsViewModel;
    LatLng userLocation;
    RecyclerView nearby_recycler;
    SearchNearByAdapter searchNearByAdapter;
    AppCompatImageView img_back;
    ProgressBar pb_nearby;
    LinearLayout nearBy_linear_layout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        stationsViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        nearby_recycler = rootView.findViewById(R.id.nearby_recycler);
        nearby_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        //  userLocation = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        pb_nearby = rootView.findViewById(R.id.pb_nearBY);
        img_back = rootView.findViewById(R.id.btn_back);
        img_back.setOnClickListener(this);
        nearBy_linear_layout = rootView.findViewById(R.id.nearBy_linear_layout);
        return rootView;
    }


    @Override
    public int getTheme() {
        return R.style.FullScreenDialog;

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();
        if (LocationUtils.isLocationEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            LocationLiveData locationLiveData = new LocationLiveData(getContext());
            locationLiveData.observe(this, location -> {
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                stationsViewModel.setUserLocation(userLocation);
                stationsViewModel.refreshStations(userLocation);


            });


            stationsViewModel.stationsAround.observe(this, arrayListResource -> {
                if (arrayListResource.data != null) {
                    ArrayList<StationNearbyItem> stationItems = arrayListResource.data;
                    searchNearByAdapter = new SearchNearByAdapter(stationItems, userLocation, getActivity());
                    nearby_recycler.setAdapter(searchNearByAdapter);
                }
            });
            stationsViewModel.isBusy.observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        pb_nearby.setVisibility(View.VISIBLE);
                    } else {
                        pb_nearby.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            nearBy_linear_layout.setVisibility(View.GONE);

        }


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dismiss();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v == img_back) {
            dismiss();
        }
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }
}
