package suncor.com.android.ui.common;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.analytics.BaseAnalytics;
import suncor.com.android.databinding.ModalDialogBinding;

public class ModalDialog extends BottomSheetDialogFragment {
    public static final String TAG = "MODAL_DIALOG";
    private ModalDialogBinding binding;
    private CharSequence title;
    private CharSequence message;
    private String formName;
    private String analyticsTitle;
    private CharSequence rightButtonText;
    private View.OnClickListener rightButtonListener;
    private CharSequence centerButtonText;
    private View.OnClickListener centerButtonListener;
    private CharSequence leftButtonText;
    private View.OnClickListener leftButtonListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ModalDialogBinding.inflate(inflater, container, false);
        binding.title.setText(title);
        binding.message.setText(message);
        binding.rightButton.setText(rightButtonText);
        binding.rightButton.setOnClickListener(rightButtonListener);
        binding.centerButton.setText(centerButtonText);
        binding.centerButton.setOnClickListener(centerButtonListener);
        analyticsTitle = title+"("+message+")";
        if (!TextUtils.isEmpty(leftButtonText)) {
            binding.leftButton.setVisibility(View.VISIBLE);
            binding.leftButton.setText(leftButtonText);
            binding.leftButton.setOnClickListener(leftButtonListener);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseAnalytics.logAlertDialogShown(requireContext(), analyticsTitle,formName);
    }

    public ModalDialog setTitle(CharSequence title) {
        this.title = title;
        return this;
    }

    public ModalDialog setMessage(CharSequence message) {
        this.message = message;
        return this;
    }

    public ModalDialog setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    public String getAnalyticsTitle() {
        return analyticsTitle;
    }

    public ModalDialog setRightButton(CharSequence buttonText, View.OnClickListener clickListener) {
        this.rightButtonText = buttonText;
        this.rightButtonListener = clickListener;
        return this;
    }

    public ModalDialog setCenterButton(CharSequence buttonText, View.OnClickListener clickListener) {
        this.centerButtonText = buttonText;
        this.centerButtonListener = clickListener;
        return this;
    }

    public ModalDialog setLeftButton(CharSequence buttonText, View.OnClickListener clickListener) {
        this.leftButtonText = buttonText;
        this.leftButtonListener = clickListener;
        return this;
    }
}
