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

import suncor.com.android.databinding.FragmentRewardsBinding;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.main.BottomNavigationFragment;

public class RewardsFragment extends BottomNavigationFragment {

    private FragmentRewardsBinding binding;
    private ObservableBoolean isWebViewLoading = new ObservableBoolean();
    private WebView webView;

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
        binding.joinButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EnrollmentActivity.class);
            getActivity().startActivity(intent);
        });
        return binding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        String language = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr" : "en";
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("file:///android_asset/rewards/index-guest-" + language + ".html");
        isWebViewLoading.set(true);
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
}
