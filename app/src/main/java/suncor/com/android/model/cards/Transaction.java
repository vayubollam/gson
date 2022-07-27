package suncor.com.android.model.cards;

import android.os.Parcel;
import android.os.Parcelable;


import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import suncor.com.android.utilities.Timber;

public class Transaction implements Comparable<Transaction>, Parcelable {


    private TransactionType transactionType;
    private String date;
    private String rewardDescription;
    private String locationAddress;
    private int basePoints;
    private int bonusPoints;
    private int totalPoints;
    private String redeemPoints;
    private int redeemPointsInt;
    private float purchaseAmount;
    private String partnerTransactionId;

    public Transaction(TransactionType transactionType, String date, String rewardDescription, String locationAddress, int basePoints, int redeemPointsInt, int bonusPoints, String redeemPoints, int totalPoints, float purchaseAmount) {
        this.transactionType = transactionType;
        this.date = date;
        this.rewardDescription = rewardDescription;
        this.locationAddress = locationAddress;
        this.basePoints = basePoints;
        this.bonusPoints = bonusPoints;
        this.redeemPointsInt = redeemPointsInt;
        this.redeemPoints = redeemPoints;
        this.totalPoints = totalPoints;
        this.purchaseAmount = purchaseAmount;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getPartnerTransactionId() {
        return partnerTransactionId;
    }

    public void setPartnerTransactionId(String partnerTransactionId) {
        this.partnerTransactionId = partnerTransactionId;
    }

    public String getDate() {
        return date;
    }

    public String getFormattedDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date = null;
        try {
            date = dateFormat.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRewardDescription() {
        return rewardDescription;
    }

    public void setRewardDescription(String rewardDescription) {
        this.rewardDescription = rewardDescription;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(int basePoints) {
        this.basePoints = basePoints;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void setBonusPoints(int bonusPoints) {
        this.bonusPoints = bonusPoints;
    }

    public int getRedeemPointsInt() {
        if (redeemPoints != null) {
            return Integer.parseInt(redeemPoints);
        } else
            return 0;
    }

    public String getRedeemPoints() {
        return redeemPoints;
    }
//    public void setRedeemPointsInt(int redeemPoints) {
//        this.redeemPoints = ;
//    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public float getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(float purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getFormattedTotalPoints() {
        String totalPoints = NumberFormat.getInstance(Locale.getDefault()).format(getTotalPoints());
        return getTotalPoints() > 0 ? "+" + totalPoints : totalPoints;
    }

    public String getFormattedPurchaseAmount() {
        String amount = NumberFormat.getCurrencyInstance(Locale.getDefault()).format(getPurchaseAmount());

        switch (getTransactionType()) {
            case PURCHASE:
                return getPurchaseAmount() == 0 ? "" : amount;
            case REDEMPTION:
                return getBonusPoints() == 0 || getPurchaseAmount() == 0 ? "" : amount;
            default:
                return "";
        }
    }

    public int getMonth() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    @Override
    public int compareTo(Transaction o) {
        if (o == null) {
            return 0;
        } else {
            return o.getDate().compareTo(getDate());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionType.name());
        dest.writeString(date);
        dest.writeString(rewardDescription);
        dest.writeString(locationAddress);
        dest.writeInt(basePoints);
        dest.writeInt(bonusPoints);
        dest.writeInt(totalPoints);
        dest.writeFloat(purchaseAmount);
    }
    public enum TransactionType {
        @SerializedName("redemption")
        REDEMPTION,
        @SerializedName("purchase")
        PURCHASE,
        @SerializedName("bonus")
        BONUS,
        @SerializedName("adjustment")
        CUSTOMER_SERVICE_ADJ,
        @SerializedName("transfer")
        PARTNER_POINTS_TRANSFER,
        @SerializedName("points")
        PETRO_POINTS,
        @SerializedName("void")
        VOID
    }

    protected Transaction(Parcel in) {
        date = in.readString();
        rewardDescription = in.readString();
        locationAddress = in.readString();
        basePoints = in.readInt();
        bonusPoints = in.readInt();
        totalPoints = in.readInt();
        purchaseAmount = in.readFloat();
        transactionType = TransactionType.valueOf(in.readString());
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };


}
