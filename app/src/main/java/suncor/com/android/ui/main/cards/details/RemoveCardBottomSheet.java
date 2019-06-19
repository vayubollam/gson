package suncor.com.android.ui.main.cards.details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import suncor.com.android.R;
import suncor.com.android.databinding.RemoveCardBottomSheetBinding;

public class RemoveCardBottomSheet extends BottomSheetDialogFragment {
    public static final String TAG = "removeCard";
    private RemoveCardBottomSheetBinding binding;
    private View.OnClickListener clickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.remove_card_bottom_sheet, container, false);
        binding.cancelButton.setOnClickListener(v -> dismiss());
        binding.removeArea.setOnClickListener(clickListener);
        return binding.getRoot();
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
