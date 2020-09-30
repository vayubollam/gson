package suncor.com.android.ui.main.pap.receipt;

import android.os.Bundle;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentReceiptBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel;

public class ReceiptFragment extends MainActivityFragment {

    private FuelUpViewModel viewModel;
    private FragmentReceiptBinding binding;
    private String transactionId;
    private ObservableBoolean isLoading = new ObservableBoolean(false);

    @Inject
    SessionManager sessionManager;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FuelUpViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReceiptBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setContext(getContext());
        binding.setTransaction(null);
        binding.setIsLoading(isLoading);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        transactionId = ReceiptFragmentArgs.fromBundle(getArguments()).getTransactionId();
        observeTransactionData(transactionId);
        binding.viewReceiptBtn.setOnClickListener((v) -> {
            binding.receiptLayout.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
        });
        binding.buttonDone.setOnClickListener(view1 -> goBack());
        binding.failsDone.setOnClickListener(view1 -> goBack());
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void observeTransactionData(String transactionId){
        viewModel.getTransactionDetails(transactionId).observe(getViewLifecycleOwner(), result->{
            if (result.status == Resource.Status.LOADING) {
                isLoading.set(true);
            } else if (result.status == Resource.Status.ERROR) {
                isLoading.set(false);
                binding.errorView.setVisibility(View.VISIBLE);
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                isLoading.set(false);
                binding.transactionGreetings.setText(String.format(getString(R.string.thank_you), sessionManager.getProfile().getFirstName()));
                binding.receiptDetails.setText(fromHtml(result.data.getReceipt()));
                binding.setTransaction(result.data);
            }
        });
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(getView());
        navController.popBackStack();
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
