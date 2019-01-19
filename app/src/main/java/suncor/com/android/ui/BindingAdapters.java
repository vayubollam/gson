package suncor.com.android.ui;

import android.view.View;

import java.util.HashMap;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import suncor.com.android.model.Station;

public class BindingAdapters {
    @BindingAdapter({"station", "amenitieType"})
    public static void setAmenities(AppCompatTextView view, Station station, int amenitieType) {
        if (station == null) {
            return;
        }
        StringBuilder buffer = new StringBuilder();
        HashMap<String, String> amenitiesMap = null;
        switch (amenitieType) {
            case 0:
                amenitiesMap = Station.SERVICE_AMENITIES;
                break;
            case 1:
                amenitiesMap = Station.FUEL_AMENITIES;
                break;
            case 2:
                amenitiesMap = Station.WASH_AMENITIES;
                break;
        }
        for (String amenitie : station.getAmenities()) {
            if (amenitiesMap.containsKey(amenitie)) {
                buffer.append(amenitiesMap.get(amenitie));
                buffer.append("\n");
            }
        }
        view.setText(buffer.toString().trim());
    }

    @BindingAdapter({"visibleGone"})
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

}
