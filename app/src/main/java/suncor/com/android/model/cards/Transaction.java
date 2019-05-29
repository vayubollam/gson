package suncor.com.android.model.cards;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static suncor.com.android.model.cards.Transaction.TransactionType.REDEMPTION;

public class Transaction implements Comparable<Transaction> {
    private TransactionType transactionType;
    private String date;
    private String rewardDescription;
    private String locationAddress;
    private int basePoints;
    private int bonusPoints;
    private int totalPoints;
    private float purchaseAmount;

    public Transaction(TransactionType transactionType, String date, String rewardDescription, String locationAddress, int basePoints, int bonusPoints, int totalPoints, float purchaseAmount) {
        this.transactionType = transactionType;
        this.date = date;
        this.rewardDescription = rewardDescription;
        this.locationAddress = locationAddress;
        this.basePoints = basePoints;
        this.bonusPoints = bonusPoints;
        this.totalPoints = totalPoints;
        this.purchaseAmount = purchaseAmount;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
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
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
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

    public String getFormattedBasePoints() {
        switch (transactionType) {
            case PURCHASE:
            case BONUS:
                return "+" + NumberFormat.getInstance().format(getBasePoints());
            case REDEMPTION:
                return "-" + NumberFormat.getInstance().format(getBasePoints());
            case CUSTOMER_SERVICE_ADJ:
                return getBasePoints() > 0 ? "+" + NumberFormat.getInstance().format(getBasePoints()) : NumberFormat.getInstance().format(getBasePoints());
            default:
                return NumberFormat.getInstance().format(getBasePoints());
        }

    }

    public String getFormattedBonusPoints() {
        return NumberFormat.getInstance().format(getBonusPoints());
    }

    public String getFormattedTotalPoints() {
        if (getTransactionType() != REDEMPTION) {
            return "+" + NumberFormat.getInstance().format(getTotalPoints()) + " Points";
        }
        return NumberFormat.getInstance().format(getTotalPoints()) + " Points";
    }

    public String getFormattedPurchaseAmount() {
        return "$" + NumberFormat.getInstance().format(getPurchaseAmount());
    }

    public int getMonth() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
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
            return getDate().compareTo(o.getDate());
        }
    }

    public enum TransactionType {
        @SerializedName("redemption")
        REDEMPTION,
        @SerializedName("purchase")
        PURCHASE,
        @SerializedName("bonus")
        BONUS,
        @SerializedName("Customer Service Adj")
        CUSTOMER_SERVICE_ADJ,
        @SerializedName("Partner Points Transfer")
        PARTNER_POINTS_TRANSFER,
        @SerializedName("Petro Points")
        PETRO_POINTS
    }


}
