package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActiveSession {
    @SerializedName("activeSession")
    public boolean activeSession;

    public Double lastFuelUpAmount;

    public String preAuthPoints;
    public String fuelUpAmount;
    public int pointsToRedeem;

    @SerializedName("LastStatus")
    public String lastStatus;
    public String lastPaymentSourceId;
    public String lastPaymentProviderName;
    public String lastStatusUpdated;
    public int lastPointsToRedeem;
    public String lastTransId;

    public String status;
    public String storeId;
    public String transId;
    public String pumpNumber;

    public Long getLastStatusUpdated() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS", Locale.CANADA);
        try {
            Date date = dateFormat.parse(lastStatusUpdated);

            if (date != null)
                return date.getTime();
            else
                return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
