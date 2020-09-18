package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

public class ActiveSession {
    @SerializedName("activeSession")
    public boolean activeSession;

    public Double lastFuelUpAmount;

    @SerializedName("LastStatus")
    public String lastStatus;
    public String lastPaymentSourceId;
    public String lastPaymentProviderName;
    public String lastStatusUpdated;
    public String lastTransId;

    public String status;
    public String storeId;
    public String transId;
    public String pumpNumber;
}
