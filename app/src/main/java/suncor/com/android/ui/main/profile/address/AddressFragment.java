package suncor.com.android.ui.main.profile.address;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentAddressBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.enrollment.form.AddressAutocompleteAdapter;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class AddressFragment extends MainActivityFragment implements OnBackPressedListener {

    private AddressViewModel viewModel;
    private FragmentAddressBinding binding;
    private AddressAutocompleteAdapter addressAutocompleteAdapter;
    private boolean isExpanded = true;

    public static AddressFragment newInstance() {
        return new AddressFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_address, container, false);
        binding.setEventHandler(this);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        viewModel.setProvincesList(((MainActivity) getActivity()).getProvinces());
        addressAutocompleteAdapter = new AddressAutocompleteAdapter(viewModel::addressSuggestionClicked);

        //show and hide autocomplete layout
        viewModel.showAutocompleteLayout.observe(this, (show) -> {
            if (getActivity() == null || binding.appBar.isExpanded()) {
                return;
            }
            if (show) {
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.black_40));
                binding.appBar.setOnClickListener((v) -> viewModel.hideAutoCompleteLayout());
                binding.streetAutocompleteBackground.setVisibility(View.VISIBLE);
                binding.streetAutocompleteOverlay.setIsVisible(true);
                ViewCompat.setElevation(binding.appBar, 0);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_40_transparent));
            } else {
                binding.appBar.setOnClickListener(null);
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.white));
                binding.streetAutocompleteBackground.setVisibility(View.GONE);
                binding.streetAutocompleteOverlay.setIsVisible(false);
                ViewCompat.setElevation(binding.appBar, 8);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            }
        });
        //binding autocomplete results to adapter
        viewModel.getAutocompleteResults().observe(this, (resource ->
        {
            if (resource.status == Resource.Status.SUCCESS && resource.data.length != 0) {
                addressAutocompleteAdapter.setSuggestions(resource.data);
                binding.streetAutocompleteOverlay.autocompleteList.scrollToPosition(0);
            }
        }));
        viewModel.getAutocompleteRetrievalStatus().observe(this, resource ->
        {
            hideKeyBoard();
            binding.streetAddressInput.getEditText().clearFocus();
        });

        binding.provinceInput.setOnClickListener(v -> {
            isExpanded = binding.appBar.isExpanded();
            hideKeyBoard();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_enrollment_form_fragment_to_provinceFragment);

        });
        binding.appBar.post(() -> {
            binding.appBar.setExpanded(isExpanded, false);
        });
    }

    public void focusChanged(View view, boolean hasFocus) {
        if (view == binding.postalcodeInput) {
            viewModel.getPostalCodeField().setHasFocus(hasFocus);
        }
        if (view == binding.streetAddressInput) {
            viewModel.getStreetAddressField().setHasFocus(hasFocus);
        }
        if (view == binding.cityInput) {
            viewModel.getCityField().setHasFocus(hasFocus);
        }
    }

    @Override
    public void onBackPressed() {
        hideKeyBoard();
        if (viewModel.showAutocompleteLayout.getValue() != null && viewModel.showAutocompleteLayout.getValue()) {
            viewModel.hideAutoCompleteLayout();
        }
    }


    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


}
