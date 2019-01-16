package suncor.com.android.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import suncor.com.android.R;

public class Station {

    public static ArrayList<String> SERVICE_AMENITIES_ENUM;
    public static ArrayList<String> FUEL_AMENITIES_ENUM;
    public static ArrayList<String> WASH_AMENITIES_ENUM;
    public static ArrayList<String> SERVICE_AMENITIES_VALUES;
    public static ArrayList<String> FUEL_AMENITIES_VALUES;
    public static ArrayList<String> WASH_AMENITIES_VALUES;

    private int id;
    private ArrayList<Hour> hours;
    private List<String> amenities;
    private Address address;


    public Station(int id, ArrayList<Hour> hours, List<String> amenities, Address address) {
        this.id = id;
        this.hours = hours;
        this.amenities = amenities;
        this.address = address;
    }

    public static void initiateAmenities(Context Context) {
        SERVICE_AMENITIES_ENUM = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_services_enum)));
        FUEL_AMENITIES_ENUM = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_fuel_enum)));
        WASH_AMENITIES_ENUM = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_wash_enum)));

        SERVICE_AMENITIES_VALUES = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_services_values)));
        FUEL_AMENITIES_VALUES = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_fuel_values)));
        WASH_AMENITIES_VALUES = new ArrayList<>(Arrays.asList(Context.getResources().getStringArray(R.array.station_wash_values)));
    }

    public void setId(int id) {
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

    public int getId() {
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
            if (SERVICE_AMENITIES_ENUM.contains(amenitie)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFuelOptions() {
        for (String amenitie : amenities) {
            if (FUEL_AMENITIES_ENUM.contains(amenitie)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasWashOptions() {
        for (String amenitie : amenities) {
            if (WASH_AMENITIES_ENUM.contains(amenitie)) {
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
        return ((Station) obj).id == this.id;
    }
}
