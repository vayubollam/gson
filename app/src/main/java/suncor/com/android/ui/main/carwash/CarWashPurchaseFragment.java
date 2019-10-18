package suncor.com.android.ui.main.carwash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;

// placeholder for buy ticket flow
public class CarWashPurchaseFragment extends MainActivityFragment implements OnBackPressedListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carwash_buy_ticket, container, false);
        return view;
    }

    @Override
    public void onBackPressed() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
