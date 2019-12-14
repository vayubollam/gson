package suncor.com.android.ui.main.actionmenu;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.ui.main.cards.CardsLoadType;

public class ActionMenuFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActionButtonMenuBinding binding = FragmentActionButtonMenuBinding.inflate(inflater, container, false);
        binding.actionAccountButton.setOnClickListener(view -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_profile_tab);
            dismiss();
        });
        binding.actionScanCardButton.setOnClickListener(view -> {
            HomeNavigationDirections.ActionToCardsDetailsFragment action = HomeNavigationDirections.actionToCardsDetailsFragment();
            action.setLoadType(CardsLoadType.PETRO_POINT_ONLY);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            dismiss();
        });
        binding.actionWashCarButton.setOnClickListener(view -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_carWashFragment);
            dismiss();
        });

        return binding.getRoot();
    }

    //This is for fixing bottom sheet dialog not fully extended issue.
    //See: https://medium.com/@OguzhanAlpayli/bottom-sheet-dialog-fragment-expanded-full-height-65b725c8309
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(mDialog -> {
            BottomSheetDialog d = (BottomSheetDialog) mDialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
