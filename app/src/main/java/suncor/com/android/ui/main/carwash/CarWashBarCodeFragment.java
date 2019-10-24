package suncor.com.android.ui.main.carwash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashBarcodeBinding;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.common.OnBackPressedListener;

public class CarWashBarCodeFragment extends MainActivityFragment implements OnBackPressedListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCarwashBarcodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_barcode, container, false);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        return binding.getRoot();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }
}
