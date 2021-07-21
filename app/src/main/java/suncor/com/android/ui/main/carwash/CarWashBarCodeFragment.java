package suncor.com.android.ui.main.carwash;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
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
import suncor.com.android.utilities.AnalyticsUtils;

public class CarWashBarCodeFragment extends MainActivityFragment implements OnBackPressedListener {
    private Boolean loadFromCarWash;
    private float previousBrightness;
    private CarWashSharedViewModel carWashSharedViewModel;
    private boolean isSingleTicket;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentCarwashBarcodeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_barcode, container, false);
        binding.buttonClose.setOnClickListener(closeListener);
        binding.appBar.setNavigationOnClickListener(v -> goBack(false));
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        isSingleTicket = CarWashBarCodeFragmentArgs.fromBundle(getArguments()).getIsSingleTicket();
        loadFromCarWash = CarWashBarCodeFragmentArgs.fromBundle(getArguments()).getIsFromCarWash();
        if (isSingleTicket) {
            String singleTicketNumber = CarWashBarCodeFragmentArgs.fromBundle(getArguments()).getSingleTicketNumber();
            binding.setIsSingleTicket(true);
            binding.setSingleTicketNumber(singleTicketNumber);
            binding.barCodeImage.setImageBitmap(generateBarcode(size.x, singleTicketNumber, BarcodeFormat.UPC_A));
        } else {
            carWashSharedViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CarWashSharedViewModel.class);
            binding.setVm(carWashSharedViewModel);
            binding.setIsSingleTicket(false);
            binding.setLifecycleOwner(this);
            binding.reEnterButton.setOnClickListener(v -> goBack(true));
            if (carWashSharedViewModel.getEncryptedCarWashCode().getValue() != null)
            {
                binding.barCodeImage.setImageBitmap(generateBarcode(size.x, carWashSharedViewModel.getEncryptedCarWashCode().getValue(), BarcodeFormat.EAN_13));
            }
        }

        return binding.getRoot();
    }

    private View.OnClickListener closeListener = view -> {
        Navigation.findNavController(requireView()).popBackStack(R.id.cardsDetailsFragment, false);
        /*if (loadFromCarWash) {
                Navigation.findNavController(getView()).navigate(R.id.action_carWashBarCodeFragment_to_carWashCardFragment);
        } else {
            Navigation.findNavController(getView()).navigate(R.id.action_carWashBarCodeFragment_to_cards_tab);
        }
         */

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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        AnalyticsUtils.setCurrentScreenName(getActivity(), "car-wash-barcode");
    }

    private void goBack(boolean reEnter) {
        if (!isSingleTicket) {
            carWashSharedViewModel.setReEnter(reEnter);
            carWashSharedViewModel.setIsBackFromBarCode(true);
        }
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onBackPressed() {
        goBack(false);
    }

    private Bitmap generateBarcode(int screenSize, String encryptedCarWashCode, BarcodeFormat barcodeFormat) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Resources r = getResources();
        int width = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, screenSize, r.getDisplayMetrics()));
        int height = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, r.getDimension(R.dimen.carwash_barcode_image_height), r.getDisplayMetrics()));
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(encryptedCarWashCode, barcodeFormat, width, height);
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
