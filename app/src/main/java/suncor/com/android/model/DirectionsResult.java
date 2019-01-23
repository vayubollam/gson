package suncor.com.android.model;

import android.content.Context;

import suncor.com.android.R;

public class DirectionsResult {
    private int distance;
    private int duration;

    public DirectionsResult(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }

    /**
     * @return distance in meter
     */
    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * @return duration in seconds
     */
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static String formatDistanceDuration(Context context, DirectionsResult result) {
        if (result == null) {
            return "";
        }
        String distanceText = context.getString(R.string.distance_generic, ((float) result.getDistance()) / 1000);
        String durationText = context.getString(R.string.duration_generic, secondsToString(result.duration));

        return distanceText + " · " + durationText;
    }

    public static String formatDistance(Context context, DirectionsResult result) {
        if (result == null) {
            return "";
        }
        return context.getString(R.string.distance_generic, ((float) result.getDistance()) / 1000);
    }

    private static String secondsToString(int totalSeconds) {
        int seconds = totalSeconds % 60;
        int totalMinutes = totalSeconds / 60;
        int minutes = totalMinutes % 60;
        int hours = totalMinutes / 60;

        if (hours != 0) {
            return hours + " hours " + minutes + " minutes";
        } else {
            if (minutes == 0 && seconds > 0) {
                minutes = 1;
            }
            return minutes + " minutes";
        }
    }
}
