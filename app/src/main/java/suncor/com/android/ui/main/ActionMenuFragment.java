package suncor.com.android.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import suncor.com.android.R;

public class ActionMenuFragment extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_action_button_menu, container, false);
        CardView accountCard = view.findViewById(R.id.action_account_button);
        accountCard.setOnClickListener(v -> {
            final NavController controller = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            controller.navigate(R.id.profile_tab);
            dismiss();
        });
        return view;
    }
}
