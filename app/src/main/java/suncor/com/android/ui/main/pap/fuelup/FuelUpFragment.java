package suncor.com.android.ui.main.pap.fuelup;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;


import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.databinding.FragmentFuelUpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.uicomponents.dropdown.ExpandableViewListener;
import suncor.com.android.utilities.AnalyticsUtils;

public class FuelUpFragment extends MainActivityFragment implements ExpandableViewListener {

    private FragmentFuelUpBinding binding;
    private FuelUpViewModel viewModel;
    private ObservableBoolean isLoading = new ObservableBoolean(true);

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FuelUpViewModel.class);
      //  AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "Select Pump"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFuelUpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
      //  binding.setIsLoading(isLoading);

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.fuelUpLimit.initListener(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getSettingResponse().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                SettingsResponse.Pap papData = result.data.getSettings().getPap();
                if(Objects.nonNull(papData) && Objects.nonNull(papData.getPreAuthLimits())){
                    papData.getPreAuthLimits().put("100", 0);
                    binding.fuelUpLimit.setDropDownData( papData.getPreAuthLimits());
                }

            }
        });

    }

    @Override
    public void onExpandCollapseListener(boolean isExpand) {
        
    }

    @Override
    public void onSelectFuelUpLimit(String value) {

        Log.i("FuelUpFragment", "Selected PreAuth value " + value );
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

}
