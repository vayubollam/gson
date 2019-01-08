package suncor.com.android.utilities;

import android.widget.TextView;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import suncor.com.android.dataObjects.Station;

public class BindingAdapters {
    @BindingAdapter({"app:station", "app:amenitieType"})
    public static void setAmenities(AppCompatTextView view, Station station, int amenitieType) {
        StringBuffer buffer = new StringBuffer();
        ArrayList allAmenities = new ArrayList();
        switch (amenitieType) {
            case 0:
                allAmenities = Station.SERVICE_AMENITIES;
                break;
            case 1:
                allAmenities = Station.FUEL_AMENITIES;
                break;
            case 2:
                allAmenities = Station.WASH_AMENITIES;
                break;
        }
        for (String amenitie : station.getAmenities()) {
            if (allAmenities.contains(amenitie)) {
                buffer.append(amenitie);
                buffer.append("\n");
            }
        }
        view.setText(buffer.toString().trim());
    }
}
