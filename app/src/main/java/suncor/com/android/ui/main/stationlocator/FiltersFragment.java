package suncor.com.android.ui.main.stationlocator;

import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.HashMap;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFiltersBinding;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class FiltersFragment extends MainActivityFragment {
    private FragmentFiltersBinding binding;
    private final HashMap<String, CheckBox> checkBoxes = new HashMap<>();
    private final HashMap<String, String> categories = new HashMap<>();

    private StationsViewModel parentViewModel;

    public static final String CARWASH_TOUCHLESS_KEY = "carWashBrushTypeTouchless";
    public static final String CARWASH_ALL_WASHES_KEY = "carWashAllWashes";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFiltersBinding.inflate(inflater, container, false);

        parentViewModel = ViewModelProviders.of(getActivity()).get(StationsViewModel.class);
        initCheckBoxes();
        if (parentViewModel.filters.getValue() != null) {
            for (String filter : parentViewModel.filters.getValue()) {
                checkBoxes.get(filter).setChecked(true);
            }
        }
        binding.applyButton.setOnClickListener((v) -> {
            ArrayList<String> filters = new ArrayList<>();
            StringBuilder serviceFilters = new StringBuilder();
            StringBuilder fuelFilters = new StringBuilder();
            StringBuilder carwashFilters = new StringBuilder();
            for (String amenity : checkBoxes.keySet()) {
                if (checkBoxes.get(amenity).isChecked()) {
                    filters.add(amenity);
                    switch (categories.get(amenity)) {
                        case "services":
                            if (serviceFilters.length() != 0) {
                                serviceFilters.append(",");
                            }
                            serviceFilters.append(amenity);
                            break;
                        case "carwash":
                            if (carwashFilters.length() != 0) {
                                carwashFilters.append(",");
                            }
                            carwashFilters.append(amenity);
                            break;
                        case "fuel":
                            if (fuelFilters.length() != 0) {
                                fuelFilters.append(",");
                            }
                            fuelFilters.append(amenity);
                            break;
                    }
                }
            }

            StringBuilder analyticsParameterValue = new StringBuilder();
            if (serviceFilters.length() != 0) {
                analyticsParameterValue.append(serviceFilters);
            }
            if (fuelFilters.length() != 0) {
                if (analyticsParameterValue.length() != 0) {
                    analyticsParameterValue.append("|");
                }
                analyticsParameterValue.append(fuelFilters);
            }
            if (carwashFilters.length() != 0) {
                if (analyticsParameterValue.length() != 0) {
                    analyticsParameterValue.append("|");
                }
                analyticsParameterValue.append(carwashFilters);
            }

            AnalyticsUtils.logEvent(getContext(), "filters_applyed", new Pair<>("filtersApplied", analyticsParameterValue.toString()));

            parentViewModel.setCurrentFilters(filters);
            Navigation.findNavController(getView()).popBackStack();
        });
        binding.clearButton.setOnClickListener((v) -> {
            for (CheckBox checkBox : checkBoxes.values()) {
                checkBox.setChecked(false);
            }
        });
        binding.appBar.setNavigationOnClickListener((v) -> {
            Navigation.findNavController(getView()).popBackStack();
        });
        return binding.getRoot();
    }

    @Override
    protected String getScreenName() {
        return "gas-station-locations-filter";
    }

    private void initCheckBoxes() {

        for (String amenityKey : Station.SERVICE_AMENITIES.keySet()) {
            String amenityTitle = Station.SERVICE_AMENITIES.get(amenityKey);
            if (amenityKey.equals("beerAndWine")) {
                amenityTitle = amenityTitle.concat(" ").concat(getString(R.string.filters_only_in_qc_message));
            }
            AppCompatCheckBox checkBox = getCheckbox(amenityTitle);
            binding.serviceAmenitiesContainer.addView(checkBox);
            binding.serviceAmenitiesContainer.addView(getDivider());
            checkBoxes.put(amenityKey, checkBox);
            categories.put(amenityKey, "services");
        }
        for (String amenityKey : Station.FUEL_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.FUEL_AMENITIES.get(amenityKey));
            binding.fuelAmenitiesContainer.addView(checkBox);
            binding.fuelAmenitiesContainer.addView(getDivider());
            checkBoxes.put(amenityKey, checkBox);
            categories.put(amenityKey, "fuel");
        }

        AppCompatCheckBox touchlessCheckBox = getCheckbox(getString(R.string.station_filter_touchless_only));
        binding.carwashAmenitiesContainer.addView(touchlessCheckBox);
        binding.carwashAmenitiesContainer.addView(getDivider());
        checkBoxes.put(CARWASH_TOUCHLESS_KEY, touchlessCheckBox);
        categories.put(CARWASH_TOUCHLESS_KEY, "carwash");


        AppCompatCheckBox allWashesCheckBox = getCheckbox(getString(R.string.station_filters_all_washes));
        binding.carwashAmenitiesContainer.addView(allWashesCheckBox);
        binding.carwashAmenitiesContainer.addView(getDivider());
        checkBoxes.put(CARWASH_ALL_WASHES_KEY, allWashesCheckBox);
        categories.put(CARWASH_ALL_WASHES_KEY, "carwash");


        touchlessCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxes.get(CARWASH_ALL_WASHES_KEY).setChecked(false);
            }
        });
        allWashesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                checkBoxes.get(CARWASH_TOUCHLESS_KEY).setChecked(false);
            }
        });
    }

    private AppCompatCheckBox getCheckbox(String amenityText) {
        AppCompatCheckBox checkbox = new AppCompatCheckBox(getContext());
        checkbox.setText(amenityText);
        checkbox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        checkbox.setTextColor(getResources().getColor(R.color.black_80));
        checkbox.setTypeface(ResourcesCompat.getFont(getContext(), R.font.gibson_regular));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(0, verticalMargin, 0, verticalMargin);
        checkbox.setLayoutParams(params);
        return checkbox;
    }

    private View getDivider() {
        View divider = new View(getContext());
        divider.setBackground(getResources().getDrawable(R.drawable.horizontal_divider));
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        int leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getResources().getDisplayMetrics()); //44 = 32 (width checkbox) + 12 (padding)

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        params.setMargins(leftMargin, 0, 0, 0);
        divider.setLayoutParams(params);
        return divider;
    }
}
