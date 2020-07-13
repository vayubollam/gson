package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

public class ActiveSession {
    @SerializedName("activeSession")
    boolean activeSession;

    public boolean getActiveSession() {
        return activeSession;
    }
}
