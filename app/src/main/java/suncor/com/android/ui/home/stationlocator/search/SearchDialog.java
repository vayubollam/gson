package suncor.com.android.ui.home.stationlocator.search;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
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
import suncor.com.android.R;
import suncor.com.android.ui.home.stationlocator.StationItem;

public class SearchDialog extends DialogFragment implements View.OnClickListener {
    SearchViewModel stationsViewModel;
    LatLng userLocation;
    RecyclerView nearby_recycler;
    SearchNearByAdapter searchNearByAdapter;
    AppCompatImageView img_back;
    ProgressBar pb_nearby;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search, container, false);
        stationsViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        nearby_recycler = rootView.findViewById(R.id.nearby_recycler);
        nearby_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
      //  userLocation = new LatLng(getArguments().getDouble("lat"), getArguments().getDouble("lng"));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        pb_nearby=rootView.findViewById(R.id.pb_nearBY);
        img_back = rootView.findViewById(R.id.btn_back);
        img_back.setOnClickListener(this);
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
        stationsViewModel.setUserLocation(userLocation);
        stationsViewModel.refreshStations(userLocation, null);
        stationsViewModel.stationsAround.observe(this, arrayListResource -> {
            if (arrayListResource.data != null) {
                ArrayList<StationItem> stationItems = arrayListResource.data;
                searchNearByAdapter = new SearchNearByAdapter(stationItems, userLocation, getActivity());
                nearby_recycler.setAdapter(searchNearByAdapter);
            }
        });
        stationsViewModel.isBusy.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean)
                {
                  pb_nearby.setVisibility(View.VISIBLE);
                }else{
                    pb_nearby.setVisibility(View.GONE);
                }
            }
        });
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
        this.userLocation=userLocation;
    }
}
