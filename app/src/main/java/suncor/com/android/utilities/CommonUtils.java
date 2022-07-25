package suncor.com.android.utilities;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public class CommonUtils {

    public static String getMockResponse(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getFormattedPoints(int enrollmentsPoints){
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(enrollmentsPoints);
    }
}
