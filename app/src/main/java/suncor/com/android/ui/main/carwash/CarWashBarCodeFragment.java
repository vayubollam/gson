package suncor.com.android.ui.main.carwash;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashBarcodeBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class CarWashBarCodeFragment extends MainActivityFragment implements OnBackPressedListener {
    private Integer clickedCardIndex;
    private Boolean loadFromCarWash;
    private float previousBrightness;
    private CarWashSharedViewModel carWashSharedViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        carWashSharedViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CarWashSharedViewModel.class);
        carWashSharedViewModel.getClickedCardIndex().observe(getViewLifecycleOwner(), integer -> clickedCardIndex = integer);
        carWashSharedViewModel.getIsFromCarWash().observe(getViewLifecycleOwner(), isLoadFromCarWash -> loadFromCarWash = isLoadFromCarWash);

        FragmentCarwashBarcodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_barcode, container, false);
        binding.setVm(carWashSharedViewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener(v -> goBack(false));
        binding.buttonClose.setOnClickListener(closeListener);
        binding.reEnterButton.setOnClickListener(v -> goBack(true));
        binding.barCodeImage.setImageBitmap(generateBarcode());
        return binding.getRoot();
    }

    private View.OnClickListener closeListener = view -> {
        CarWashBarCodeFragmentDirections.ActionCarWashBarCodeFragmentToCardsDetailsFragment action
                = CarWashBarCodeFragmentDirections.actionCarWashBarCodeFragmentToCardsDetailsFragment();
        action.setCardIndex(clickedCardIndex);
        if (loadFromCarWash) {
            action.setIsCardFromCarWash(true);
        } else {
            action.setIsCardFromProfile(false);
        }
        Navigation.findNavController(getView()).navigate(action);
    };

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        previousBrightness = attributes.screenBrightness;
        attributes.screenBrightness = 1f;
        getActivity().getWindow().setAttributes(attributes);
    }

    @Override
    public void onStop() {
        super.onStop();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.screenBrightness = previousBrightness;
        getActivity().getWindow().setAttributes(attributes);
    }

    private void goBack(boolean reEnter) {
        carWashSharedViewModel.setReEnter(reEnter);
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onBackPressed() {
        goBack(false);
    }

    private Bitmap generateBarcode() {
        String encryptedCarWashCode = carWashSharedViewModel.getEncryptedCarWashCode().getValue();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Resources r = getResources();
        int width = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.carwash_barcode_image_width), r.getDisplayMetrics()));
        int height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.carwash_barcode_image_height), r.getDisplayMetrics()));
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(encryptedCarWashCode, BarcodeFormat.EAN_13, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
