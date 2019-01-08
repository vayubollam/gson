package suncor.com.android.dataObjects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Station {

    public static ArrayList<String> SERVICE_AMENITIES = new ArrayList();
    public static ArrayList<String> FUEL_AMENITIES = new ArrayList();
    public static ArrayList<String> WASH_AMENITIES = new ArrayList();

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

    public ArrayList<Hour> getHours() {
        return this.hours;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public Address getAddress() {
        return address;
    }

    public Hour getTodaysHours() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return getHours().get(dayOfWeek - 1);
    }

    public boolean hasService()
    {
        for(String amenitie : amenities){
            if(SERVICE_AMENITIES.contains(amenitie))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasFuelOptions()
    {
        for(String amenitie : amenities){
            if(FUEL_AMENITIES.contains(amenitie))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasWashOptions()
    {
        for(String amenitie : amenities){
            if(WASH_AMENITIES.contains(amenitie))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isOpen(Station station) {
        Hour workHour = station.getTodaysHours();
        return workHour.isInRange(Calendar.getInstance());
    }
}
