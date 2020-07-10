package suncor.com.android.ui.main.wallet.payments.add;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.Locale;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentAddPaymentBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.webview.ObservableWebView;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class AddPaymentFragment extends MainActivityFragment {
    private FragmentAddPaymentBinding binding;
    private AddPaymentViewModel viewModel;

    private ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private ObservableBoolean isAdding = new ObservableBoolean(false);

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddPaymentViewModel.class);
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "Add Payment"));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddPaymentBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setIsAdding(isAdding);
        binding.setLifecycleOwner(this);

        binding.appBar.setNavigationOnClickListener(v -> goBack());

        initWebView();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getAddPaymentEndpoint().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                binding.webView.loadUrl(result.data.toString());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        AnalyticsUtils.setCurrentScreenName(this.getActivity(), "add-payment");
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.clearCache(true);
        isWebViewLoading.set(true);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.webView.postDelayed(() -> isWebViewLoading.set(false), 50);

                if (url.toLowerCase().contains(viewModel.redirectUrl.toLowerCase())) {
                    isAdding.set(true);
                    new Handler().postDelayed(() -> {
                        goBack();
                    }, 200);
                }
            }
        });
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
