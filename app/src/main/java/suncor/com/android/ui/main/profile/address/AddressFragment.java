package suncor.com.android.ui.main.profile.address;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentAddressBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;
import suncor.com.android.ui.enrollment.form.AddressAutocompleteAdapter;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.utilities.AnalyticsUtils;

public class AddressFragment extends MainActivityFragment implements OnBackPressedListener {

    public static final String ADDRESS_FRAGMENT = "address_fragment";
    private AddressViewModel viewModel;
    private ProfileSharedViewModel sharedViewModel;
    private FragmentAddressBinding binding;
    private AddressAutocompleteAdapter addressAutocompleteAdapter;
    private boolean isExpanded = true;
    private ObservableBoolean isEditing = new ObservableBoolean(false);
    @Inject
    ViewModelFactory factory;
    private boolean isLoadedFirstTime = false;
    private static final String CANADAPOST_SEARCH_DESCRIPTIVE_SCREEN_NAME = "canadapost-search-address";

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
        binding.setIsEditing(isEditing);
        binding.provinceInput.setOnClickListener(v -> {
            isExpanded = binding.appBar.isExpanded();
            hideKeyBoard();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_addressFragment_to_provinceProfileFragment);

        });
        binding.appBar.post(() -> {
            binding.appBar.setExpanded(isExpanded, false);
        });
        binding.appBar.setNavigationOnClickListener(v -> goBack());

        binding.streetAutocompleteOverlay.autocompleteList.setAdapter(addressAutocompleteAdapter);
        binding.streetAutocompleteOverlay.autocompleteList.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_divider));
        binding.streetAutocompleteOverlay.autocompleteList.addItemDecoration(dividerDecoration);
        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());
        viewModel.showSaveButtonEvent.observe(getViewLifecycleOwner(), event -> {
            if (event.getContentIfNotHandled() != null) {
                isEditing.set(true);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoadedFirstTime = true;
        viewModel = ViewModelProviders.of(this, factory).get(AddressViewModel.class);
        sharedViewModel = ViewModelProviders.of(requireActivity()).get(ProfileSharedViewModel.class);
        viewModel.setSharedViewModel(sharedViewModel);
        viewModel.setProvincesList(((MainActivity) requireActivity()).getProvinces());
        addressAutocompleteAdapter = new AddressAutocompleteAdapter(viewModel::addressSuggestionClicked);

        viewModel.showAutocompleteLayout.observe(this, (show) -> {
            if (getActivity() == null) {
                return;
            }
            if (show) {
                if (isLoadedFirstTime) {
                    AnalyticsUtils.setCurrentScreenName(getActivity(), CANADAPOST_SEARCH_DESCRIPTIVE_SCREEN_NAME);
                    isLoadedFirstTime = false;
                }

                binding.appBar.setBackgroundColor(getResources().getColor(R.color.black_40));
                binding.appBar.setOnClickListener((v) -> viewModel.hideAutoCompleteLayout());
                binding.streetAutocompleteBackground.setVisibility(View.VISIBLE);
                binding.streetAutocompleteOverlay.setIsVisible(true);
                ViewCompat.setElevation(binding.appBar, 0);
            } else {
                binding.appBar.setOnClickListener(null);
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.white));
                binding.streetAutocompleteBackground.setVisibility(View.GONE);
                binding.streetAutocompleteOverlay.setIsVisible(false);
                ViewCompat.setElevation(binding.appBar, 8);
            }
        });
        viewModel.getAutocompleteResults().observe(this, (resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data.length != 0) {
                addressAutocompleteAdapter.setSuggestions(resource.data);
                binding.streetAutocompleteOverlay.autocompleteList.scrollToPosition(0);
            }
        }));
        viewModel.getAutocompleteRetrievalStatus().observe(this, resource -> {
            hideKeyBoard();
            binding.streetAddressInput.getEditText().clearFocus();
        });
        sharedViewModel.getSelectedProvince().observe(this, province -> {
            viewModel.setSelectedProvince(province);
        });


        viewModel.navigateToProfile.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Navigation.findNavController(requireView()).popBackStack();
            }
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
        if (hasFocus) {
            scrollToView(view);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }


    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }

    void goBack() {
        hideKeyBoard();
        if (viewModel.showAutocompleteLayout.getValue() != null && viewModel.showAutocompleteLayout.getValue()) {
            viewModel.hideAutoCompleteLayout();
        }
        sharedViewModel.setSelectedProvince(null);
        Navigation.findNavController(requireView()).popBackStack();

    }

    private void scrollToView(View view) {
        binding.scrollView.postDelayed(() -> {
            int viewYPosition = view.getTop();
            int scrollPosition;
            if (view == binding.postalcodeInput) {
                scrollPosition = viewYPosition;
            } else {
                int halfHeight = binding.scrollView.getHeight() / 2 - view.getHeight() / 2;
                scrollPosition = Math.max(viewYPosition - halfHeight, 0);
            }
            binding.scrollView.smoothScrollTo(0, scrollPosition);
        }, 400);
    }

}
