package suncor.com.android.ui.main.actionmenu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import suncor.com.android.R;
import suncor.com.android.ui.main.actionmenu.ActionMenuType;
import suncor.com.android.ui.main.actionmenu.OnActionMenuButtonClickedListener;

public class ActionMenuFragment extends BottomSheetDialogFragment {

    private OnActionMenuButtonClickedListener actionMenuButtonClickedListener;

    public ActionMenuFragment(OnActionMenuButtonClickedListener listener) {
        this.actionMenuButtonClickedListener = listener;
    }

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
        CardView washAndGoCard = view.findViewById(R.id.action_wash_car_button);
        CardView scanMyCard = view.findViewById(R.id.action_scan_card_button);
        accountCard.setOnClickListener(v -> {
            if (actionMenuButtonClickedListener != null)
                actionMenuButtonClickedListener.onActionMenuButtonClicked(ActionMenuType.ACCOUNT);
            dismiss();
        });

        washAndGoCard.setOnClickListener(v -> {
            if (actionMenuButtonClickedListener != null)
                actionMenuButtonClickedListener.onActionMenuButtonClicked(ActionMenuType.WASH_AND_GO);
            dismiss();
        });

        scanMyCard.setOnClickListener(v -> {
            if (actionMenuButtonClickedListener != null)
                actionMenuButtonClickedListener.onActionMenuButtonClicked(ActionMenuType.SCAN_MY_CARD);
            dismiss();
        });
        return view;
    }
}
