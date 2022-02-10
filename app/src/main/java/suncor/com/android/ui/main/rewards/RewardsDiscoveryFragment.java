package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableBoolean;
import androidx.navigation.Navigation;

import java.util.Locale;

import suncor.com.android.databinding.FragmentRewardsDiscoveryBinding;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class RewardsDiscoveryFragment extends MainActivityFragment {

    private FragmentRewardsDiscoveryBinding binding;
    private ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private boolean scroll20 = false, scroll40 = false, scroll60 = false, scroll80 = false, scroll100 = false;
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
        AnalyticsUtils.setCurrentScreenName(this.requireActivity(), "discover-petro-points");
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-" + language + ".html");
        isWebViewLoading.set(true);
        AnalyticsUtils.setCurrentScreenName(requireActivity(), "discover-petro-points-loading");
        binding.webview.setOnScrollChangedCallback((l, t, oldl, oldt) -> {
            float contentHeight = binding.webview.getContentHeight() * binding.webview.getScaleY();
            requireView();
            total = contentHeight * getResources().getDisplayMetrics().density - requireView().getHeight();

            if (total >= 0) {
                double scrollPosition = (t / (total - getResources().getDisplayMetrics().density)) * 100d;
                int percentage = (int) scrollPosition;
                if (percentage > 20 && !scroll20) {
                    scroll20 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "20"));
                } else if (percentage > 40 && !scroll40) {
                    scroll40 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "40"));
                } else if (percentage > 60 && !scroll60) {
                    scroll60 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "60"));
                } else if (percentage > 80 && !scroll80) {
                    scroll80 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "80"));
                } else if (percentage > 100 && !scroll100) {
                    scroll100 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "100"));
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
    }

    @Override
    protected String getScreenName() {
        return "rewards-content-loggedin";
    }
}
