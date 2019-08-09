package suncor.com.android.ui.common.webview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.fragment.app.DialogFragment;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentWebDialogBinding;

public class WebDialogFragment extends DialogFragment {

    public static final String TAG = WebDialogFragment.class.getSimpleName();

    public static final String HEADER = "header";
    public static final String URL = "url";
    private FragmentWebDialogBinding binding;
    private ObservableBoolean isLoading = new ObservableBoolean(true);

    public static WebDialogFragment newInstance(String url, String header) {
        WebDialogFragment adf = new WebDialogFragment();
        Bundle bundle = new Bundle(2);
        bundle.putString(HEADER, header);
        bundle.putString(URL, url);
        adf.setArguments(bundle);
        return adf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.ModalWebViewStyle);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_web_dialog, container, false);
        binding.setIsLoading(isLoading);
        binding.url.setText(getArguments().getString(URL));
        binding.header.setText(getArguments().getString(HEADER));
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl(getArguments().getString(URL));
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isLoading.set(false);
            }
        });
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.exit.setOnClickListener(v -> dismiss());
    }
}