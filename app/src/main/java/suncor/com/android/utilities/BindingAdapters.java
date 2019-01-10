package suncor.com.android.utilities;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import suncor.com.android.dataObjects.Station;

public class BindingAdapters {
    @BindingAdapter({"app:station", "app:amenitieType"})
    public static void setAmenities(AppCompatTextView view, Station station, int amenitieType) {
        if(station == null)
        {
            return;
        }
        StringBuilder buffer = new StringBuilder();
        ArrayList amenitiesEnum = new ArrayList();
        ArrayList amenitiesValues = new ArrayList();
        switch (amenitieType) {
            case 0:
                amenitiesEnum = Station.SERVICE_AMENITIES_ENUM;
                amenitiesValues = Station.SERVICE_AMENITIES_VALUES;
                break;
            case 1:
                amenitiesEnum = Station.FUEL_AMENITIES_ENUM;
                amenitiesValues = Station.FUEL_AMENITIES_VALUES;
                break;
            case 2:
                amenitiesEnum = Station.WASH_AMENITIES_ENUM;
                amenitiesValues = Station.WASH_AMENITIES_VALUES;
                break;
        }
        for (String amenitie : station.getAmenities()) {
            int index = amenitiesEnum.indexOf(amenitie);
            if (index != -1) {
                buffer.append(amenitiesValues.get(index));
                buffer.append("\n");
            }
        }
        view.setText(buffer.toString().trim());
    }
}
