package suncor.com.android.ui.home.stationlocator.search;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.home.stationlocator.StationItem;
import suncor.com.android.ui.home.stationlocator.StationsViewModel;

public class SearchDialog extends DialogFragment implements View.OnClickListener {
   SearchViewModel stationsViewModel;
   LatLng userLocation;
   RecyclerView nearby_recycler;
   SearchNearByAdapter searchNearByAdapter;
   AppCompatImageView img_back;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.search,container,false);
        stationsViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        nearby_recycler=rootView.findViewById(R.id.nearby_recycler);
        nearby_recycler.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        userLocation=new LatLng(getArguments().getDouble("lat"),getArguments().getDouble("lng"));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        img_back=rootView.findViewById(R.id.btn_back);
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
        stationsViewModel.refreshStations(userLocation,null);
        stationsViewModel.stationsAround.observe(this, arrayListResource -> {
            if(arrayListResource.data!=null){
                ArrayList<StationItem> stationItems=arrayListResource.data;
                searchNearByAdapter=new SearchNearByAdapter(stationItems,userLocation,getActivity());
                nearby_recycler.setAdapter(searchNearByAdapter);
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(),getTheme()){
            @Override
            public void onBackPressed() {
                super.onBackPressed();
                dismiss();
            }
        };
    }

    @Override
    public void onClick(View v) {
        if(v==img_back){
            dismiss();
        }
    }
}
