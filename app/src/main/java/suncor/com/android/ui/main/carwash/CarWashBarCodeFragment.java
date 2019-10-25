package suncor.com.android.ui.main.carwash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashBarcodeBinding;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class CarWashBarCodeFragment extends MainActivityFragment implements OnBackPressedListener {
    private Integer clickedCardIndex;
    private Boolean loadFromCarWash;
    private CarWashSharedViewModel carWashSharedViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        carWashSharedViewModel = ViewModelProviders.of(getActivity()).get(CarWashSharedViewModel.class);
        carWashSharedViewModel.getClickedCardIndex().observe(getViewLifecycleOwner(), integer -> clickedCardIndex = integer);
        carWashSharedViewModel.getIsFromCarWash().observe(getViewLifecycleOwner(), isLoadFromCarWash -> loadFromCarWash = isLoadFromCarWash);

        FragmentCarwashBarcodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_barcode, container, false);
        binding.setVm(carWashSharedViewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack(false));
        binding.buttonClose.setOnClickListener(v -> {
            CarWashBarCodeFragmentDirections.ActionCarWashBarCodeFragmentToCardsDetailsFragment action
                    = CarWashBarCodeFragmentDirections.actionCarWashBarCodeFragmentToCardsDetailsFragment();
            action.setCardIndex(clickedCardIndex);
            if (loadFromCarWash) {
                action.setIsCardFromCarWash(true);
            } else {
                action.setIsCardFromProfile(false);
            }
            Navigation.findNavController(getView()).navigate(action);
        });

        binding.reEnterButton.setOnClickListener(v -> {
            goBack(true);
        });
        return binding.getRoot();
    }

    private void goBack(boolean reEnter) {
        carWashSharedViewModel.setReEnter(reEnter);
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onBackPressed() {
        goBack(false);
    }
}
