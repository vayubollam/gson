package suncor.com.android.ui.main.actionmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentActionButtonMenuBinding;
import suncor.com.android.ui.main.MainActivity;

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
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_cardsDetailsFragment);
            dismiss();
        });
        binding.actionWashCarButton.setOnClickListener(view -> {
        if (((MainActivity) getActivity()).isNearestStationIndependent()) {
            new AlertDialog.Builder(getContext())
                    .setTitle("This is an independently owned car wash")
                    .setMessage("Petro-Canada car wash cards and tickets are not supported at this location. You can still purchase a car wash ticket at this location in order to wash your car.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_to_carWashFragment);
            dismiss();
        }
        });

        return binding.getRoot();
    }
}
