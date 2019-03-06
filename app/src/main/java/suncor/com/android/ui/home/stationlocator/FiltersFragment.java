package suncor.com.android.ui.home.stationlocator;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FiltersFragmentBinding;
import suncor.com.android.model.Station;

public class FiltersFragment extends Fragment {


    public static final String FILTERS_FRAGMENT_TAG = "filters-tag";
    private FiltersFragmentBinding binding;
    private HashMap<String, CheckBox> checkBoxes = new HashMap<>();
    private StationsViewModel parentViewModel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FiltersFragmentBinding.inflate(inflater, container, false);

        StationViewModelFactory factory = new StationViewModelFactory(SuncorApplication.favouriteRepository);
        parentViewModel = ViewModelProviders.of(getActivity(), factory).get(StationsViewModel.class);
        initCheckBoxes();
        if (parentViewModel.filters.getValue() != null) {
            for (String filter : parentViewModel.filters.getValue()) {
                checkBoxes.get(filter).setChecked(true);
            }
        }
        binding.applyButton.setOnClickListener((v) -> {
            ArrayList<String> filters = new ArrayList<>();
            for (String amenity : checkBoxes.keySet()) {
                if (checkBoxes.get(amenity).isChecked()) {
                    filters.add(amenity);
                }
            }
            parentViewModel.setCurrentFilters(filters);
            getFragmentManager().popBackStack();
        });
        binding.clearButton.setOnClickListener((v) -> {
            for (CheckBox checkBox : checkBoxes.values()) {
                checkBox.setChecked(false);
            }
        });
        binding.appBar.setNavigationOnClickListener((v) -> {
            getFragmentManager().popBackStack();
        });
        return binding.getRoot();
    }

    private void initCheckBoxes() {

        for (String amenityKey : Station.SERVICE_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.SERVICE_AMENITIES.get(amenityKey));
            binding.serviceAmenitiesContainer.addView(checkBox);
            binding.serviceAmenitiesContainer.addView(getDivider());
            checkBoxes.put(amenityKey, checkBox);
        }
        for (String amenityKey : Station.FUEL_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.FUEL_AMENITIES.get(amenityKey));
            binding.fuelAmenitiesContainer.addView(checkBox);
            binding.fuelAmenitiesContainer.addView(getDivider());
            checkBoxes.put(amenityKey, checkBox);
        }
        for (String amenityKey : Station.WASH_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.WASH_AMENITIES.get(amenityKey));
            binding.carwashAmenitiesContainer.addView(checkBox);
            binding.carwashAmenitiesContainer.addView(getDivider());
            checkBoxes.put(amenityKey, checkBox);
        }
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
