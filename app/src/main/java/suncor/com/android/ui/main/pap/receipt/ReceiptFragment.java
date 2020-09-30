package suncor.com.android.ui.main.pap.receipt;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.File;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentReceiptBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.PdfUtil;

public class ReceiptFragment extends MainActivityFragment {

    private ReceiptViewModel viewModel;
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
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReceiptViewModel.class);
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
                binding.receiptDetails.setText(result.data.getReceipt());
                binding.setTransaction(result.data);

                binding.shareButton.setOnClickListener(v -> {
                    File pdfFile = PdfUtil.createPdf(getContext(), result.data.receiptData, transactionId);

                    // TODO: Create error handling
                    if (pdfFile == null) return;

                    Uri pdfUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pdfUri = FileProvider.getUriForFile(getContext(), getActivity().getPackageName() + ".provider", pdfFile);
                    } else {
                        pdfUri = Uri.fromFile(pdfFile);
                    }

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, pdfUri);
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share"));
                });
            }
        });
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(getView());
        navController.popBackStack();
    }
}
