package suncor.com.android.dataObjects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Station {
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

    public ArrayList<Hour> getHours()
    {
        return this.hours;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public Address getAddress() {
        return address;
    }
}
