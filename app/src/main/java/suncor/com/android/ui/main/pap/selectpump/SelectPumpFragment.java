package suncor.com.android.ui.main.pap.selectpump;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.analytics.pap.SelectPumpAnalytics;
import suncor.com.android.databinding.FragmentSelectPumpBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.P97StoreDetailsResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.home.HomeViewModel;

public class SelectPumpFragment extends MainActivityFragment implements SelectPumpListener {

    private FragmentSelectPumpBinding binding;
    private SelectPumpViewModel viewModel;
    private SelectPumpAdapter adapter;
    private ObservableBoolean isLoading = new ObservableBoolean(true);
    private String storeId;
    private HomeViewModel homeViewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SelectPumpViewModel.class);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);

        SelectPumpAnalytics.logFormStart(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSelectPumpBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setIsLoading(isLoading);

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.helpButton.setOnClickListener(v -> {
            SelectPumpAnalytics.logSelectPumpInfoTap(requireContext());
            showHelp();
        });

        adapter = new SelectPumpAdapter(this);
        binding.pumpRecyclerView.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isLoading.set(true);
        SelectPumpAnalytics.logLoadingScreenName(requireActivity());
        storeId = SelectPumpFragmentArgs.fromBundle(getArguments()).getStoreId();
        String location = SelectPumpFragmentArgs.fromBundle(getArguments()).getLocation();
        if(Objects.nonNull(location)) {
           binding.actionLocation.setText(location);
        } else {
            homeViewModel.nearestStation.observe(getViewLifecycleOwner(), result -> {
                if (result.status == Resource.Status.SUCCESS && result.data != null) {
                    binding.actionLocation.setText(getString(R.string.action_location, result.data.getStation().getAddress().getAddressLine()));
                }
            });
        }

        viewModel.isPAPAvailable(storeId).observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                goBack();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                if (!result.data) {
                    Alerts.prepareCustomDialog(
                            getString(R.string.pap_not_available_header),
                            getString(R.string.pap_not_available_description),
                            getContext(),
                            (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                SelectPumpAnalytics.logInAppPaymentUnAvailableAlert(requireContext());
                                goBack();
                            }, "Pay at Pump").show();

                    binding.selectPumpLayout.setVisibility(View.GONE);
                } else {
                    viewModel.getStoreDetails(storeId).observe(getViewLifecycleOwner(), storeDetailsResponseResource -> {
                        if (storeDetailsResponseResource.status == Resource.Status.SUCCESS && storeDetailsResponseResource.data != null
                                && storeDetailsResponseResource.data.fuelService.pumpStatuses != null) {
                            P97StoreDetailsResponse storeDetailsResponse = storeDetailsResponseResource.data;

                            ArrayList<String> pumpNumbers = new ArrayList<String>();

                            for (P97StoreDetailsResponse.PumpStatus pumpStatus: storeDetailsResponse.fuelService.pumpStatuses) {

                                if (pumpStatus.status.equals("Available")) {
                                    pumpNumbers.add(String.valueOf(pumpStatus.pumpNumber));
                                }
                            }

                            adapter.setPumpNumbers(pumpNumbers);

                            isLoading.set(false);

                            // Update the storeId to the P97 store id
                            storeId = storeDetailsResponseResource.data.storeNumber;
                        } else if (storeDetailsResponseResource.status != Resource.Status.LOADING){
                            Alerts.prepareGeneralErrorDialog(getContext(), "Pay at Pump").show();
                            goBack();
                        }
                    });

                }
            }
        });
    }


    @Override
    public void selectPumpNumber(String pumpNumber) {
        new Handler().postDelayed(() -> {
            HomeNavigationDirections.ActionToFuelUpFragment action = HomeNavigationDirections.actionToFuelUpFragment(storeId, pumpNumber);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).popBackStack();
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
        }, 200);
    }

    private void showHelp() {
        DialogFragment fragment = new SelectPumpHelpDialogFragment();
        fragment.show(getFragmentManager(), "dialog");
    }

    private void goBack() {
       Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onResume() {
        super.onResume();
        SelectPumpAnalytics.logScreenName(requireActivity());
    }
}
