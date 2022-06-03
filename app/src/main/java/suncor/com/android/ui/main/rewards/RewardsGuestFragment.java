package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import java.util.Locale;
import suncor.com.android.R;
import suncor.com.android.analytics.giftcard.RewardsGuestAnalytics;
import suncor.com.android.databinding.FragmentRewardsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.webview.ObservableWebView;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.enrollment.form.SecurityQuestionViewModel;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.utilities.ConnectionUtil;
import suncor.com.android.utilities.Constants;

import static suncor.com.android.analytics.AnalyticsConstants.REWARDS_GUEST_FORM_NAME;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_25;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_5;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_50;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_75;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_95;
import static suncor.com.android.analytics.giftcard.RewardsDiscoveryAnalytics.SCREEN_NAME_REWARDS_DISCOVERY;
import static suncor.com.android.analytics.giftcard.RewardsDiscoveryAnalytics.SCREEN_NAME_REWARDS_DISCOVERY_LOADING;

import javax.inject.Inject;

public class RewardsGuestFragment extends BottomNavigationFragment {

    private FragmentRewardsBinding binding;
    private final ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private final ObservableBoolean isButtonVisible = new ObservableBoolean();
    private ObservableWebView webView;
    private SecurityQuestionViewModel securityQuestionViewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    private boolean scroll5 = false, scroll25 = false, scroll50 = false, scroll75 = false, scroll95 = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        securityQuestionViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(SecurityQuestionViewModel.class);
        securityQuestionViewModel.fetchQuestion();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        binding.setIsWebviewLoading(isWebViewLoading);
        binding.setIsButtonVisible(isButtonVisible);
        if (webView == null || isWebViewLoading.get()) {
            initWebView();
        } else {
            binding.layout.removeView(binding.webview);
            binding.layout.addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        binding.joinButton.bringToFront();
        binding.joinButton.setOnClickListener(v -> {
           checkForApiResponse();
        });

        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";

        RewardsGuestAnalytics.logScreenNameClass(requireActivity(),SCREEN_NAME_REWARDS_DISCOVERY_LOADING,
                this.getClass().getSimpleName());
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-guest-" + language + ".html");
        isWebViewLoading.set(true);
        isButtonVisible.set(true);

        RewardsGuestAnalytics.logScreenNameClass(requireActivity(),SCREEN_NAME_REWARDS_DISCOVERY,
                this.getClass().getSimpleName());
        binding.webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t, int oldl, int oldt){
                if(t> oldt){
                    float contentHeight = binding.webview.getContentHeight() * binding.webview.getScaleY();
                    float total = contentHeight * getResources().getDisplayMetrics().density - getView().getHeight();
                    double scrollPosition = (t / (total - getResources().getDisplayMetrics().density))  * 100d;
                    int percentage = (int) scrollPosition;
                    if (percentage > 5 && !scroll5) {
                        scroll5 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_5);
                    } else if (percentage > 25 && !scroll25) {
                        scroll25 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_25);
                    } else if (percentage > 50 && !scroll50) {
                        scroll50 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_50);
                    } else if (percentage > 75 && !scroll75) {
                        scroll75 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_75);
                    } else if (percentage > 95 && !scroll95) {
                        scroll95 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_95);
                    }
                }
            }
        });
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.webview.postDelayed(() -> isWebViewLoading.set(false), 50);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                isButtonVisible.set(false);
            }
        });
        webView = binding.webview;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.layout.removeView(webView);
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @Override
    protected String getScreenName() {
        return "rewards-content";
    }

    private void navigateToEnrollmentScreen(){
        try{
            Intent intent = new Intent(getActivity(), EnrollmentActivity.class);
            intent.putExtra(Constants.IS_COME_FROM_REWARDS_GUEST_SCREEN, true);
            requireActivity().startActivity(intent);
        }catch (Exception e){
            showErrorAlertPopup().show();
        }

    }

    private void checkForApiResponse(){
        securityQuestionViewModel.securityQuestions.observe(getViewLifecycleOwner(), arrayListResource -> {
            switch (arrayListResource.status) {
                case SUCCESS:
                   navigateToEnrollmentScreen();
                    break;
                case ERROR:
                    assert arrayListResource.message != null;
                    RewardsGuestAnalytics.logFormErrorEvent(requireActivity(),
                            arrayListResource.message,
                            REWARDS_GUEST_FORM_NAME,
                            ""
                            );

                    showErrorAlertPopup().show();
            }
        });
    }

    private AlertDialog showErrorAlertPopup(){
        boolean hasInternetConnection = ConnectionUtil.haveNetworkConnection(requireContext());

        String analyticsName = requireActivity().getString(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                + "(" + requireActivity().getString(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message) + ")";

        RewardsGuestAnalytics.logAlertDialogShown(requireActivity(),
                analyticsName,
                REWARDS_GUEST_FORM_NAME);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(hasInternetConnection ? R.string.msg_e001_title : R.string.msg_e002_title)
                .setMessage(hasInternetConnection ? R.string.msg_e001_message : R.string.msg_e002_message)

                .setPositiveButton(R.string.ok, (dialog, which) -> {

                    RewardsGuestAnalytics.logAlertDialogInteraction(requireActivity(),
                            analyticsName,
                            requireContext().getString(R.string.ok),
                            REWARDS_GUEST_FORM_NAME);
                    dialog.dismiss();
                });
        return builder.create();
    }


}
