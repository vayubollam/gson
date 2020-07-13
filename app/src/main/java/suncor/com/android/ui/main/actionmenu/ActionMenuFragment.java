package suncor.com.android.ui.main.actionmenu;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.ui.main.wallet.payments.add.AddPaymentViewModel;

public class ActionMenuFragment extends BottomSheetDialogFragment {
    FragmentActionButtonMenuBinding binding;
    private ActionMenuViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    public ActionMenuFragment(){};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ActionMenuViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentActionButtonMenuBinding.inflate(inflater, container, false);

        binding.actionAccountButton.setOnClickListener(view -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_profile_tab);
            dismiss();
        });
        binding.actionFuelUpButton.setOnClickListener(view -> {

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getActiveSession().observe(getViewLifecycleOwner(), result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                boolean activeSession = result.data.getActiveSession();

                binding.actionFuelUpButton.setText(activeSession ? R.string.action_fuelling : R.string.action_fuel_up);
                binding.actionFuelUpButton.setLoading(activeSession);
            }
        });
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
