package suncor.com.android.ui.main.profile.transcations;


import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.io.File;
import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentTransactionDetailBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.model.cards.TransactionDetail;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.pap.receipt.ReceiptViewModel;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.PdfUtil;

public class TransactionDetailFragment extends MainActivityFragment {


    private ReceiptViewModel viewModel;

    private FragmentTransactionDetailBinding binding;
    private Transaction transaction;


    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ReceiptViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_detail, container, false);
        transaction = TransactionDetailFragmentArgs.fromBundle(getArguments()).getTransaction();
        binding.setTd(new TransactionDetail(transaction, getContext()));
        binding.setLifecycleOwner(this);
        binding.transactionToolbar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.viewReceiptBtn.setOnClickListener((v) -> {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.BUTTONTAP, new Pair<>(AnalyticsUtils.Param.buttonText, "View Receipt"));
            binding.receiptLayout.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
        });
        if(transaction.getPartnerTransactionId() != null) {
            observeTransactionData(transaction.getPartnerTransactionId());
        }
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-transaction-detail";
    }


    private void observeTransactionData(String transactionId){
        viewModel.getTransactionDetails(transactionId, true).observe(getViewLifecycleOwner(), result->{
            if (result.status == Resource.Status.LOADING) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else if (result.status == Resource.Status.ERROR) {
                binding.progressBar.setVisibility(View.GONE);
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                binding.setTransaction(result.data);
                binding.progressBar.setVisibility(View.GONE);
                binding.paymentTypeValue.setVisibility(View.VISIBLE);
                binding.paymentTypeDivider.setVisibility(View.VISIBLE);
                binding.paymentTypeTxt.setVisibility(View.VISIBLE);
                binding.paymentTypeValue.setText(result.data.getPaymentType(getContext(), false));
                if (!Objects.isNull(result.data.receiptData) && !result.data.receiptData.isEmpty()) {
                    binding.viewReceiptBtn.setVisibility(View.VISIBLE);
                    binding.receiptDetails.setText(result.data.getReceiptFormatted());
                }
            }

        });
    }
}
