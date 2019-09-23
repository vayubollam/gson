package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import suncor.com.android.ui.common.webview.ObservableWebView;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class RewardsDiscoveryFragment extends MainActivityFragment {

    private FragmentRewardsDiscoveryBinding binding;
    private ObservableBoolean isWebViewLoading = new ObservableBoolean();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsDiscoveryBinding.inflate(inflater, container, false);
        binding.setIsWebviewLoading(isWebViewLoading);
        initWebView();
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-" + language + ".html");
        isWebViewLoading.set(true);
        binding.webview.setOnScrollChangedCallback(new ObservableWebView.OnScrollChangedCallback(){
            public void onScroll(int l, int t, int oldl, int oldt){
                if(t> oldt){
                    float contentHeight = binding.webview.getContentHeight() * binding.webview.getScaleY();
                    float total = contentHeight * getResources().getDisplayMetrics().density - getView().getHeight();

                    double scrollPosition = (t / (total - getResources().getDisplayMetrics().density))  * 100d;
                    int pourcentage = (int) scrollPosition;
                    if (pourcentage == 5 || pourcentage == 25 || pourcentage == 50|| pourcentage == 75 || pourcentage == 95  ){
                        AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold",Integer.toString(pourcentage) ));

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
    }

    @Override
    protected String getScreenName() {
        return "rewards-content-loggedin";
    }
}
