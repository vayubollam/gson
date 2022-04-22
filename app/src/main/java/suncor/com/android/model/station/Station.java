package suncor.com.android.model.station;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import suncor.com.android.R;

public class Station {

    public static HashMap<String, String> FULL_AMENITIES = new LinkedHashMap<>();
    public static HashMap<String, String> SERVICE_AMENITIES = new LinkedHashMap<>();
    public static HashMap<String, String> WASH_AMENITIES = new LinkedHashMap<>();
    public static HashMap<String, String> FUEL_AMENITIES = new LinkedHashMap<>();
    public static HashMap<String, String> ANALYTICS_ABBREVIATIONS_MAP = new LinkedHashMap<>();



    private String id;
    private ArrayList<Hour> hours;
    private List<String> amenities;
    private Address address;
    private static final String INDEPENDENT_STATION_KEY = "carWashBrandOther";


    public Station(String id, ArrayList<Hour> hours, List<String> amenities, Address address) {
        this.id = id;
        this.hours = hours;
        this.amenities = amenities;
        this.address = address;
    }

    public static void initiateAmenities(Context Context) {
        ArrayList<String> serviceAmenitiesEnum = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_services_enum)));
        ArrayList<String> fuelAmenitiesEnum = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_fuel_enum)));
        ArrayList<String> washAmenitiesEnum = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_wash_enum)));

        ArrayList<String> serviceAmenitiesValues = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_services_values)));
        ArrayList<String> fuelAmenitiesValues = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_fuel_values)));
        ArrayList<String> washAmenitiesValues = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_wash_values)));

        ArrayList<String> serviceAbbreviations = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.services_abbreviation)));
        ArrayList<String> fuelAbbreviations = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.fuel_abbreviations)));
        ArrayList<String> washAbbreviations = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.wash_abbreviations)));

        for (int i = 0; i < serviceAmenitiesEnum.size(); i++) {
            SERVICE_AMENITIES.put(serviceAmenitiesEnum.get(i), serviceAmenitiesValues.get(i));
            FULL_AMENITIES.put(serviceAmenitiesEnum.get(i), serviceAmenitiesValues.get(i));
            ANALYTICS_ABBREVIATIONS_MAP.put(serviceAmenitiesEnum.get(i),serviceAbbreviations.get(i));
        }
        for (int i = 0; i < fuelAmenitiesEnum.size(); i++) {
            FUEL_AMENITIES.put(fuelAmenitiesEnum.get(i), fuelAmenitiesValues.get(i));
            FULL_AMENITIES.put(fuelAmenitiesEnum.get(i), fuelAmenitiesValues.get(i));
            ANALYTICS_ABBREVIATIONS_MAP.put(fuelAmenitiesEnum.get(i),fuelAbbreviations.get(i));
        }
        for (int i = 0; i < washAmenitiesEnum.size(); i++) {
            WASH_AMENITIES.put(washAmenitiesEnum.get(i), washAmenitiesValues.get(i));
            FULL_AMENITIES.put(washAmenitiesEnum.get(i), washAmenitiesValues.get(i));
            ANALYTICS_ABBREVIATIONS_MAP.put(washAmenitiesEnum.get(i),washAbbreviations.get(i));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Hour> getHours() {
        return this.hours;
    }

    public void setHours(ArrayList<Hour> hours) {
        this.hours = hours;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean hasService() {
        for (String amenitie : amenities) {
            if (SERVICE_AMENITIES.containsKey(amenitie)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFuelOptions() {
        for (String amenitie : amenities) {
            if (FUEL_AMENITIES.containsKey(amenitie)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasWashOptions() {
        for (String amenitie : amenities) {
            if (WASH_AMENITIES.containsKey(amenitie)) {
                return true;
            }
        }
        return false;
    }

    public String getCarWashType() {
        for (String amenitie : amenities) {
            if (WASH_AMENITIES.containsKey(amenitie)) {
                return WASH_AMENITIES.get(amenitie);
            }
        }
        return null;
    }

    public boolean isStationIndependentDealer() {
        for (String amenitie : amenities) {
            if (amenitie.equals(INDEPENDENT_STATION_KEY)) return true;
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Station)) {
            return false;
        }
        return ((Station) obj).id.equals(this.id);
    }
}
