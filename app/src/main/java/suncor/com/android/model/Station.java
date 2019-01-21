package suncor.com.android.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
import suncor.com.android.R;

public class Station {

    public static HashMap<String, String> FULL_AMENITIES = new HashMap<>();
    public static HashMap<String, String> SERVICE_AMENITIES = new HashMap<>();
    public static HashMap<String, String> WASH_AMENITIES = new HashMap<>();
    public static HashMap<String, String> FUEL_AMENITIES = new HashMap<>();

    private String id;
    private ArrayList<Hour> hours;
    private List<String> amenities;
    private Address address;


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

        for (int i = 0; i < serviceAmenitiesEnum.size(); i++) {
            SERVICE_AMENITIES.put(serviceAmenitiesEnum.get(i), serviceAmenitiesValues.get(i));
            FULL_AMENITIES.put(serviceAmenitiesEnum.get(i), serviceAmenitiesValues.get(i));
        }
        for (int i = 0; i < fuelAmenitiesEnum.size(); i++) {
            FUEL_AMENITIES.put(fuelAmenitiesEnum.get(i), fuelAmenitiesValues.get(i));
            FULL_AMENITIES.put(fuelAmenitiesEnum.get(i), fuelAmenitiesValues.get(i));
        }
        for (int i = 0; i < washAmenitiesEnum.size(); i++) {
            WASH_AMENITIES.put(washAmenitiesEnum.get(i), washAmenitiesValues.get(i));
            FULL_AMENITIES.put(washAmenitiesEnum.get(i), washAmenitiesValues.get(i));
        }
    }

    public void setId(String id) {
        this.id = id;
    }


    public void setHours(ArrayList<Hour> hours) {
        this.hours = hours;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Hour> getHours() {
        return this.hours;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public Address getAddress() {
        return address;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof Station)) {
            return false;
        }
        return ((Station) obj).id.equals(this.id);
    }
}
