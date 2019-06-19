package suncor.com.android.ui.main.rewards;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import suncor.com.android.ui.main.common.BaseFragment;

public class RewardsDiscoveryFragment extends BaseFragment {

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
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.webview.postDelayed(() -> isWebViewLoading.set(false), 50);
            }
        });
    }
}
