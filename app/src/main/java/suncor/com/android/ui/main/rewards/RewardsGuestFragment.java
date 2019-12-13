package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.content.Intent;
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

import java.util.Locale;

import suncor.com.android.databinding.FragmentRewardsBinding;
import suncor.com.android.ui.common.webview.ObservableWebView;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class RewardsGuestFragment extends BottomNavigationFragment {

    private FragmentRewardsBinding binding;
    private ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private ObservableWebView webView;

    private boolean scroll20 = false, scroll40 = false, scroll60 = false, scroll80 = false, scroll100 = false;

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
            getActivity().startActivity(intent);
        });

        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        AnalyticsUtils.setCurrentScreenName(this.getActivity(), "discover-petro-points");
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-guest-" + language + ".html");
        isWebViewLoading.set(true);
        binding.webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t, int oldl, int oldt){
                if(t> oldt){
                    float contentHeight = binding.webview.getContentHeight() * binding.webview.getScaleY();
                    float total = contentHeight * getResources().getDisplayMetrics().density - getView().getHeight();
                    double scrollPosition = (t / (total - getResources().getDisplayMetrics().density))  * 100d;
                    int percentage = (int) scrollPosition;
                    if (percentage > 20 && !scroll20) {
                        scroll20 = true;
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold","20"));
                    } else if (percentage > 40 && !scroll40){
                        scroll40 = true;
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold","40"));
                    } else if (percentage > 60 && !scroll60){
                        scroll60 = true;
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold","60"));
                    } else if (percentage > 80 && !scroll80){
                        scroll80 = true;
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold","80"));
                    } else if (percentage > 100 && !scroll100){
                        scroll100 = true;
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold","100"));
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
