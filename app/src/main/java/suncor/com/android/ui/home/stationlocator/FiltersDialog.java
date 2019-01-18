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
import androidx.lifecycle.ViewModelProviders;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.databinding.FiltersFragmentBinding;
import suncor.com.android.model.Station;
import suncor.com.android.ui.common.FullScreenDialog;

public class FiltersDialog extends FullScreenDialog {


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
        getDialog().getWindow().getAttributes().windowAnimations = R.style.FiltersDialogAnimation;
        StationViewModelFactory factory = new StationViewModelFactory(SuncorApplication.favouriteRepository);
        parentViewModel = ViewModelProviders.of(getTargetFragment(), factory).get(StationsViewModel.class);
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
            parentViewModel.filters.postValue(filters);
            dismiss();
        });
        binding.clearButton.setOnClickListener((v) -> {
            for (CheckBox checkBox : checkBoxes.values()) {
                checkBox.setChecked(false);
            }
        });
        binding.backButton.setOnClickListener((v) -> {
            dismiss();
        });
        return binding.getRoot();
    }

    private void initCheckBoxes() {
        for (String amenityKey : Station.SERVICE_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.SERVICE_AMENITIES.get(amenityKey));
            binding.serviceAmenitiesContainer.addView(checkBox);
            checkBoxes.put(amenityKey, checkBox);
        }
        for (String amenityKey : Station.FUEL_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.FUEL_AMENITIES.get(amenityKey));
            binding.fuelAmenitiesContainer.addView(checkBox);
            checkBoxes.put(amenityKey, checkBox);
        }
        for (String amenityKey : Station.WASH_AMENITIES.keySet()) {
            AppCompatCheckBox checkBox = getCheckbox(Station.WASH_AMENITIES.get(amenityKey));
            binding.carwashAmenitiesContainer.addView(checkBox);
            checkBoxes.put(amenityKey, checkBox);
        }
    }

    private AppCompatCheckBox getCheckbox(String amenityText) {
        AppCompatCheckBox checkbox = new AppCompatCheckBox(getContext());
        checkbox.setText(amenityText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int verticalMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(0, verticalMargin, 0, verticalMargin);
        checkbox.setLayoutParams(params);
        return checkbox;
    }
}
