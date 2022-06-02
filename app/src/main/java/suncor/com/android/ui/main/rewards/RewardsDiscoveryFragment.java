package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
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
import androidx.databinding.ObservableBoolean;
import androidx.navigation.Navigation;
import java.util.Locale;
import suncor.com.android.analytics.giftcard.RewardsDiscoveryAnalytics;
import suncor.com.android.databinding.FragmentRewardsDiscoveryBinding;
import suncor.com.android.ui.main.common.MainActivityFragment;

import static suncor.com.android.analytics.AnalyticsConstants.REWARDS_DISCOVERY_FORM_NAME;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_25;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_5;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_50;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_75;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_95;
import static suncor.com.android.analytics.giftcard.RewardsDiscoveryAnalytics.SCREEN_NAME_REWARDS_DISCOVERY;
import static suncor.com.android.analytics.giftcard.RewardsDiscoveryAnalytics.SCREEN_NAME_REWARDS_DISCOVERY_LOADING;

public class RewardsDiscoveryFragment extends MainActivityFragment {

    private FragmentRewardsDiscoveryBinding binding;
    private final ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private boolean scroll5 = false, scroll25 = false, scroll50 = false, scroll75 = false, scroll95 = false;
    private float total;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsDiscoveryBinding.inflate(inflater, container, false);
        binding.setIsWebviewLoading(isWebViewLoading);
        initWebView();
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {

        RewardsDiscoveryAnalytics.logScreenNameClass(requireActivity(), SCREEN_NAME_REWARDS_DISCOVERY_LOADING,
                this.getClass().getSimpleName());


        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-" + language + ".html");
        isWebViewLoading.set(true);

        RewardsDiscoveryAnalytics.logScreenNameClass(requireActivity(),SCREEN_NAME_REWARDS_DISCOVERY,
                this.getClass().getSimpleName());

        binding.webview.setOnScrollChangedCallback((l, t, oldl, oldt) -> {
            float contentHeight = binding.webview.getContentHeight() * binding.webview.getScaleY();
            requireView();
            total = contentHeight * getResources().getDisplayMetrics().density - requireView().getHeight();

            if (total >= 0) {
                double scrollPosition = (t / (total - getResources().getDisplayMetrics().density)) * 100d;
                int percentage = (int) scrollPosition;
                if (percentage > 5 && !scroll5) {
                    scroll5 = true;
                    RewardsDiscoveryAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_5);
                } else if (percentage > 25 && !scroll25) {
                    scroll25 = true;
                    RewardsDiscoveryAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_25);
                } else if (percentage > 50 && !scroll50) {
                    scroll50 = true;
                    RewardsDiscoveryAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_50);
                } else if (percentage > 75 && !scroll75) {
                    scroll75 = true;
                    RewardsDiscoveryAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_75);
                } else if (percentage > 95 && !scroll95) {
                    scroll95 = true;
                    RewardsDiscoveryAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_95);
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

            }
        });
    }

    @Override
    protected String getScreenName() {
        return "rewards-content-loggedin";
    }
}
