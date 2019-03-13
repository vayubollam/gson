package suncor.com.android.model;

import android.content.Context;

import suncor.com.android.R;

public class DirectionsResult {
    public static final DirectionsResult INVALID = new DirectionsResult(-1, -1);
    private int distance;
    private int duration;

    public DirectionsResult(int distance, int duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public static String formatDistanceDuration(Context context, DirectionsResult result) {
        if (result == null) {
            return "";
        } else if (result.distance == -1) {
            return context.getString(R.string.station_distance_unavailable);
        }
        String distanceText = context.getString(R.string.station_distance_generic, ((float) result.getDistance()) / 1000);
        String durationText = context.getString(R.string.station_duration_generic, secondsToString(result.duration, context));

        return distanceText + " · " + durationText;
    }

    public static String formatDistance(Context context, DirectionsResult result) {
        if (result == null) {
            return "";
        }
        return context.getString(R.string.station_distance_generic, ((float) result.getDistance()) / 1000);
    }

    private static String secondsToString(int totalSeconds, Context context) {
        int seconds = totalSeconds % 60;
        int totalMinutes = totalSeconds / 60;
        int minutes = totalMinutes % 60;
        int hours = totalMinutes / 60;
        if (hours == 0 && minutes == 0 && seconds > 0) {
            minutes = 1;
        }

        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(context.getResources().getQuantityString(R.plurals.hours_distance, hours, hours))
                    .append(minutes != 0 ? " " : "");
        }
        if (minutes != 0) {
            builder.append(minutes)
                    .append(" ")
                    .append(context.getString(R.string.minutes_distance));
        }
        return builder.toString();
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
}
