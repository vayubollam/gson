package suncor.com.android.dataObjects;

public class StationMatrix {
    private String Distance;
    private String Duration;

    public StationMatrix(String distance, String duration) {
        Distance = distance;
        Duration = duration;
    }

    public String getDistance() {
        return Distance;
    }

    public void setDistance(String distance) {
        Distance = distance;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }
}
