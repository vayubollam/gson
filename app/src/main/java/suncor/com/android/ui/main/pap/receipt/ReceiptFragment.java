package suncor.com.android.ui.main.pap.receipt;

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
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.model.ReviewErrorCode;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.RuntimeExecutionException;
import com.google.android.play.core.tasks.Task;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentReceiptBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.PdfUtil;

import static com.google.android.play.core.review.model.ReviewErrorCode.PLAY_STORE_NOT_FOUND;

public class ReceiptFragment extends MainActivityFragment {

    private ReceiptViewModel viewModel;
    private FragmentReceiptBinding binding;
    private String transactionId;
    private boolean isGooglePay;
    private boolean isReceiptValid = false;
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
        isGooglePay = ReceiptFragmentArgs.fromBundle(getArguments()).getIsGooglePay();

        observeTransactionData(transactionId);

        binding.viewReceiptBtn.setOnClickListener((v) -> {
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.buttonTap, new Pair<>(AnalyticsUtils.Param.buttonText, "View Receipt"));
            binding.receiptLayout.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
        });

        binding.buttonDone.setOnClickListener(view1 -> {
            if (isReceiptValid && viewModel.isFirstTransactionOfMonth()) {
                checkForReview();
            } else {
                goBack();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-receipt");
    }

    private void observeTransactionData(String transactionId){
        viewModel.getTransactionDetails(transactionId, false).observe(getViewLifecycleOwner(), result->{
            if (result.status == Resource.Status.LOADING) {
                isLoading.set(true);
                AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-receipt-loading");
            } else if (result.status == Resource.Status.ERROR) {
                isLoading.set(false);
                if (sessionManager.getProfile().getFirstName() != null) {
                    binding.transactionGreetings.setText(String.format(getString(R.string.thank_you), sessionManager.getProfile().getFirstName()));
                } 
                binding.receiptTvDescription.setText(R.string.your_transaction_availble_in_your_account);
                binding.transactionLayout.setVisibility(View.GONE);
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                isLoading.set(false);

                AnalyticsUtils.setCurrentScreenName(requireActivity(), "pay-at-pump-receipt");
                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.paymentComplete,
                        new Pair<>(AnalyticsUtils.Param.paymentMethod, isGooglePay ? "Google Pay" : "Credit Card"));

                binding.paymentType.setText(result.data.getPaymentType(requireContext(), isGooglePay));
                binding.transactionGreetings.setText(String.format(getString(R.string.thank_you), sessionManager.getProfile().getFirstName()));
                if(Objects.isNull(result.data.receiptData) || result.data.receiptData.isEmpty()){
                    binding.shareButton.setVisibility(View.GONE);
                    binding.viewReceiptBtn.setVisibility(View.GONE);
                } else {
                    isReceiptValid = true;
                    binding.receiptDetails.setText(result.data.getReceiptFormatted());
                }
                binding.setTransaction(result.data);

                binding.shareButton.setOnClickListener(v -> {
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.buttonTap, new Pair<>(AnalyticsUtils.Param.buttonText, "Share receipt"));
                    File pdfFile = PdfUtil.createPdf(getContext(), result.data.receiptData, transactionId);

                    // TODO: Create error handling
                    if (pdfFile == null) return;

                    Uri pdfUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        pdfUri = FileProvider.getUriForFile(requireContext(), getActivity().getPackageName() + ".provider", pdfFile);
                    } else {
                        pdfUri = Uri.fromFile(pdfFile);
                    }

                    Intent share = new Intent();
                    share.setAction(Intent.ACTION_SEND);
                    share.setType("application/pdf");
                    share.putExtra(Intent.EXTRA_STREAM, pdfUri);
                    share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_receipt_subject, result.data.getFormattedDate()));
                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_receipt_body, result.data.getFormattedDate()));
                    share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(share, "Share"));
                });


            }
        });
    }

    private void goBack() {
        NavController navController = Navigation.findNavController(requireView());
        navController.popBackStack();
    }

    private void checkForReview() {
        //Check for review
        ReviewManager manager = ReviewManagerFactory.create(requireContext());
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(requireActivity(), reviewInfo);

                flow.addOnCompleteListener(reviewed -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    goBack();
                });
            } else {
                // There was some problem, log or handle the error code.
                // TODO: Handle error when launching in app review
                @ReviewErrorCode int reviewErrorCode = ((RuntimeExecutionException) task.getException()).getErrorCode();

                if (reviewErrorCode == PLAY_STORE_NOT_FOUND) { }
                
                goBack();
            }
        });
    }
}
