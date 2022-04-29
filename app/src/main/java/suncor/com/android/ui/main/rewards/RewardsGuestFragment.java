package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;

import java.util.Locale;

import suncor.com.android.analytics.giftcard.RewardsGuestAnalytics;
import suncor.com.android.databinding.FragmentRewardsBinding;
import suncor.com.android.ui.common.webview.ObservableWebView;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.main.BottomNavigationFragment;

import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_25;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_5;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_50;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_75;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_95;

public class RewardsGuestFragment extends BottomNavigationFragment {

    private FragmentRewardsBinding binding;
    private final ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private ObservableWebView webView;

    private boolean scroll5 = false, scroll25 = false, scroll50 = false, scroll75 = false, scroll95 = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        binding.setIsWebviewLoading(isWebViewLoading);
        if (webView == null || isWebViewLoading.get()) {
            initWebView();
        } else {
            binding.layout.removeView(binding.webview);
            binding.layout.addView(webView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        binding.joinButton.bringToFront();
        binding.joinButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EnrollmentActivity.class);
            requireActivity().startActivity(intent);
        });

        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        RewardsGuestAnalytics.logRewardGuestScreenName(requireActivity());

        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-guest-" + language + ".html");
        isWebViewLoading.set(true);

        RewardsGuestAnalytics.logRewardGuestLoadingScreenName(requireActivity());
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
                    } else if (percentage > 80 && !scroll75) {
                        scroll75 = true;
                        RewardsGuestAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_75);
                    } else if (percentage > 100 && !scroll95) {
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
}
