package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

public class ActiveSession {
    @SerializedName("activeSession")
    boolean activeSession;
    private Double lastFuelUpAmount;

    public boolean getActiveSession() {
        return activeSession;
    }

    public Double getLastFuelUpAmount() {
        return lastFuelUpAmount;
    }
}
