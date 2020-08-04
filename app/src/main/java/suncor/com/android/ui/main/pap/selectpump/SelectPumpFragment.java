package suncor.com.android.ui.main.pap.selectpump;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentSelectPumpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class SelectPumpFragment extends MainActivityFragment {

    private FragmentSelectPumpBinding binding;
    private SelectPumpViewModel viewModel;
    private SelectPumpAdapter adapter;
    private ObservableBoolean isLoading = new ObservableBoolean(true);

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SelectPumpViewModel.class);
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "Select Pump"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectPumpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(isLoading);

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        adapter = new SelectPumpAdapter(getActivity());
        binding.pumpRecyclerView.setAdapter(adapter);
        binding.helpButton.setOnClickListener(v -> showHelp());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String storeId = SelectPumpFragmentArgs.fromBundle(getArguments()).getStoreId();

        viewModel.isPAPAvailable(storeId).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                if (!result.data) {
                    Alerts.prepareCustomDialog(
                            getString(R.string.pap_not_available_header),
                            getString(R.string.pap_not_available_description),
                            getContext(),
                            (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                goBack();
                            }).show();
                } else {
                    viewModel.getStoreDetails(storeId).observe(getViewLifecycleOwner(), storeDetailsResponseResource -> {
                        if (storeDetailsResponseResource.status == Resource.Status.SUCCESS && storeDetailsResponseResource.data != null) {
                            P97StoreDetailsResponse storeDetailsResponse = storeDetailsResponseResource.data;

                            ArrayList<String> pumpNumbers = new ArrayList<String>();

                            for (P97StoreDetailsResponse.PumpStatus pumpStatus: storeDetailsResponse.fuelService.pumpStatuses) {

                                if (pumpStatus.status.equals("Available")) {
                                    pumpNumbers.add(String.valueOf(pumpStatus.pumpNumber));
                                }
                            }

                            adapter.setPumpNumbers(pumpNumbers);
                            adapter.notifyDataSetChanged();

                            isLoading.set(false);
                        }
                    });

                }
            }
        });
    }



    private void showHelp() {
        DialogFragment fragment = new SelectPumpHelpDialogFragment();
        fragment.show(getFragmentManager(), "dialog");
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

}
