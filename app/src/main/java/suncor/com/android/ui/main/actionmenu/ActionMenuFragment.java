package suncor.com.android.ui.main.actionmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

public class ActionMenuFragment extends BottomSheetDialogFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private ActionMenuViewModel mViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(ActionMenuViewModel.class);
        mViewModel.navigateToPetroPoints.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_cardsDetailsFragment);
                dismiss();
            }
        });

        mViewModel.navigateToProfile.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_profile_tab);
                dismiss();
            }
        });

        mViewModel.navigateToCarWash.observe(this, booleanEvent -> {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_carWashFragment);
            dismiss();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentActionButtonMenuBinding binding = FragmentActionButtonMenuBinding.inflate(inflater, container, false);
        binding.setVm(mViewModel);
        return binding.getRoot();
    }
}
