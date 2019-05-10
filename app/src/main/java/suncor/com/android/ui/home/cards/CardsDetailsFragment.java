package suncor.com.android.ui.home.cards;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import suncor.com.android.R;
import suncor.com.android.ui.home.common.BaseFragment;

public class CardsDetailsFragment extends BaseFragment {

    private AppCompatImageView barCodeImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards_details, container, false);
        barCodeImage = view.findViewById(R.id.barcode);
        barCodeImage.setImageBitmap(generateBarcode());
        return view;
    }

    private Bitmap generateBarcode() {
        String petroPointsCardNumber = "7069410001143011";
        String dataForBarCode = petroPointsCardNumber.substring(4, petroPointsCardNumber.length() - 1);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int width = 400;
            int height = 88;
            BitMatrix bitMatrix = multiFormatWriter.encode(petroPointsCardNumber, BarcodeFormat.CODE_128, width, height);
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
