package suncor.com.android.ui.home.stationlocator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.databinding.PromptLoginDialogBinding;
import suncor.com.android.ui.login.LoginActivity;

public class PromptLoginDialog extends BottomSheetDialogFragment {


    private PromptLoginDialogBinding binding;
    public static final String TAG = "PROMPT_LOGIN_DIALOG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = PromptLoginDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.cancelButton.setOnClickListener(v -> {
            dismiss();

        });
        binding.signingButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), LoginActivity.class));
            dismiss();

        });
    }
}
