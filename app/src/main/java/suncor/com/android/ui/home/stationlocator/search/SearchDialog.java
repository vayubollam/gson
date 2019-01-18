package suncor.com.android.ui.home.stationlocator.search;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.LocationLiveData;
import suncor.com.android.R;
import suncor.com.android.data.repository.PlaceSuggestionsProviderImpl;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.FullScreenDialog;
import suncor.com.android.utilities.LocationUtils;

public class SearchDialog extends FullScreenDialog implements View.OnClickListener, TextWatcher {
    private SearchViewModel viewModel;
    private LatLng userLocation;
    private RecyclerView nearbyRecycler;
    private RecyclerView suggestionsRecycler;
    private SearchNearByAdapter searchNearByAdapter;
    private AppCompatImageView imgBack;
    private ProgressBar pbNearby;
    private LinearLayout nearByLinearLayout;
    private LinearLayout suggestionsLinearLayout;
    private AppCompatEditText txtSearchAddress;
    private SuggestionsAdapter suggestionsAdapter;
    private ProgressBar pbSuggestion;
    private AppCompatTextView txtNoResult;
    private AppCompatImageButton btnClear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.search_fragment, container, false);
        //instantiating
        GeoDataClient geoDataClient = Places.getGeoDataClient(getContext());
        SearchViewModelFactory viewModelFactory = new SearchViewModelFactory(new PlaceSuggestionsProviderImpl(geoDataClient));
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchViewModel.class);
        //retrieving views
        nearbyRecycler = rootView.findViewById(R.id.nearby_recycler);
        suggestionsLinearLayout = rootView.findViewById(R.id.suggestions_linear_layout);
        suggestionsRecycler = rootView.findViewById(R.id.sugestions_recycler);
        pbNearby = rootView.findViewById(R.id.pb_nearBY);
        imgBack = rootView.findViewById(R.id.btn_back);
        pbSuggestion = rootView.findViewById(R.id.pb_suggestions);
        txtNoResult = rootView.findViewById(R.id.txt_no_result);
        nearByLinearLayout = rootView.findViewById(R.id.nearBy_linear_layout);
        txtSearchAddress = rootView.findViewById(R.id.txt_search_address);
        btnClear = rootView.findViewById(R.id.btn_clear);

        //layout manager for recycler views
        suggestionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        nearbyRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.SearchDialogAnimation;

        //listener
        imgBack.setOnClickListener(this);
        txtSearchAddress.addTextChangedListener(this);
        btnClear.setOnClickListener(this);

        return rootView;
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
                viewModel.setUserLocation(userLocation);
                viewModel.refreshStations(userLocation);


            });

            viewModel.stationsAround.observe(this, arrayListResource -> {
                if (arrayListResource.status == Resource.Status.SUCCESS) {
                    pbNearby.setVisibility(View.GONE);
                    ArrayList<StationNearbyItem> stationItems = arrayListResource.data;
                    searchNearByAdapter = new SearchNearByAdapter(stationItems, userLocation, getActivity());
                    nearbyRecycler.setAdapter(searchNearByAdapter);
                } else if (arrayListResource.status == Resource.Status.LOADING) {
                    pbNearby.setVisibility(View.VISIBLE);
                }
            });

        } else {
            nearByLinearLayout.setVisibility(View.GONE);

        }
        viewModel.getSuggestions().observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                pbSuggestion.setVisibility(View.GONE);
                ArrayList<PlaceSuggestion> suggestions = arrayListResource.data;
                if (suggestionsAdapter == null) {
                    suggestionsAdapter = new SuggestionsAdapter(suggestions);
                    suggestionsRecycler.setAdapter(suggestionsAdapter);
                }

                suggestionsAdapter.getSuggestions().clear();
                suggestionsAdapter.setSuggestions(suggestions);
                if (suggestions.isEmpty()) {
                    suggestionsLinearLayout.setVisibility(View.GONE);
                    txtNoResult.setVisibility(View.VISIBLE);
                } else {
                    nearbyRecycler.setVisibility(View.VISIBLE);
                    txtNoResult.setVisibility(View.GONE);
                }
            } else if (arrayListResource.status == Resource.Status.LOADING) {
                //nearbyRecycler.setVisibility(View.GONE);
                txtNoResult.setVisibility(View.GONE);
                pbSuggestion.setVisibility(View.VISIBLE);
            } else if (arrayListResource.status == Resource.Status.ERROR) {
                //TODO : handle error
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
        if (v == imgBack) {
            dismiss();
        } else if (v == btnClear) {
            txtSearchAddress.getText().clear();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String query = txtSearchAddress.getText().toString();
        if (query.isEmpty()) {
            nearByLinearLayout.setVisibility(View.VISIBLE);
            suggestionsLinearLayout.setVisibility(View.GONE);

        } else {
            nearByLinearLayout.setVisibility(View.GONE);
            suggestionsLinearLayout.setVisibility(View.VISIBLE);
            viewModel.refreshPlaceSuggestions(query);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
