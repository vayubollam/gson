package suncor.com.android.model.pap.transaction;

import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.MutableLiveData;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import suncor.com.android.R;
import suncor.com.android.utilities.Timber;

public class Transaction {

    private final MutableLiveData<String> correspondingDollars = new MutableLiveData<>();
    private final MutableLiveData<String> rbcAlongWithRedemption = new MutableLiveData<>();
    private final MutableLiveData<Integer> basePointsEarnedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> bonusPointsEarnedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> pointsRedeemedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> newBalanceMutableData = new MutableLiveData<>();
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
    public List<String> receiptData;
    private String transactionId;
    private Address address;
    private String storeName;
    private String merchantId;
    private String authCode;
    private String cardType;
    private String lastFour;
    private String posDatetimeUtc;
    private String posDatetimeLocal;
    private String formattedPosDatetimeLocal;
    private String timeZone;
    private long utcOffsetSeconds;
    private double totalAmount;
    private double otherDiscount;
    private String currency;
    private double subtotal;
    private int totalPointsRedeemed;
    private double taxAmount;
    private String formattedOtherDiscounts;
    private String formattedSubtotal;
    private String formattedTax;
    private String formattedTotal;
    private List<LineItem> lineItems;
    private List<LoyaltyPointsMessages> loyaltyPointsMessages = null;
    private String terminalType;
    private String fuelBrand;
    private String appChannel;
    private String appDisplayName;
    private String basketPaymentState;
    private String storeTenantId;
    private String storeTenantName;
    private String storeNumber;
    private String posTransactionId;
    private boolean isFirstTym = true;
    private int bonusPoints = 0;
    private int basePoints = 0;
    private double pointsRedeemed = 0;
    private int currentBalance;
    private int newBalance = 0;
    private boolean isCLPEDown = false;

    public Address getAddress() {
        return address;
    }

    public String getAuthCode() {
        return authCode;
    }

    public int getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(int currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getCardType() {
        return cardType;
    }

    public int getTotalPointsRedeemed() {
        return totalPointsRedeemed;
    }

    public String getLastFour() {
        return lastFour;
    }

    public String getFormattedPosDatetimeLocal() {
        return formattedPosDatetimeLocal;
    }

    public String getCurrency() {
        return currency;
    }

    public String getFormattedOtherDiscounts() {
        return formatter.format(otherDiscount);
    }

    public String getFormattedSubtotal() {
        return formatter.format(subtotal);
    }

    public String getFormattedTax() {
        return formattedTax;
    }

    public String getFormattedTotal() {
        return formatter.format(totalAmount);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public String getFuelBrand() {
        return fuelBrand;
    }

    public String getAppChannel() {
        return appChannel;
    }

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public String getBasketPaymentState() {
        return basketPaymentState;
    }


    public String getReceiptFormatted() {
        StringBuilder sb = new StringBuilder();
        receiptData.forEach(data -> sb.append(data).append("\n"));
        return sb.toString();
    }

    public String getFormattedDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA);
        Date date;
        try {
            date = dateFormat.parse(posDatetimeUtc);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    public String getPaymentType(Context context, boolean isGooglePay) {
        StringBuilder sb = new StringBuilder(cardType);
        sb.append(" ");
        sb.append(context.getString(R.string.payment_card_number, lastFour.replaceAll("[^\\d]", "")));
        if (isGooglePay) {
            sb.append(" (Google Pay)");
        }
        return sb.toString();
    }

    public boolean getIsCLPEDown() {
        return isCLPEDown;
    }

    public double getOtherDiscount() {
        return otherDiscount;
    }

    public void setOtherDiscount(double otherDiscount) {
        this.otherDiscount = otherDiscount;
    }

    public List<LoyaltyPointsMessages> getLoyaltyPointsMessages() {
        return loyaltyPointsMessages;
    }

    public void setLoyaltyPointsMessages(List<LoyaltyPointsMessages> loyaltyPointsMessages) {
        this.loyaltyPointsMessages = loyaltyPointsMessages;
    }

    public int getBurnedPoints() {
        getCalculatedFieldValues();
        return (int) pointsRedeemed;
    }

    public MutableLiveData<String> getCorrespondingDollarOffForBurnedPointsMutableData() {
        getCalculatedFieldValues();
        correspondingDollars.postValue(formatter.format(pointsRedeemed / 1000.0));
        return correspondingDollars;
    }

    public double getCorrespondingDollarOff() {
        return pointsRedeemed / 1000.0;
    }

    public MutableLiveData<String> getRbcAlongWithRedemptionSavingsMutableData() {
        getCalculatedFieldValues();
        rbcAlongWithRedemption.postValue(formatter.format(otherDiscount + pointsRedeemed / 1000.0));
        return rbcAlongWithRedemption;
    }

    public double getRbcAlongWithRedemptionSavings() {
        getCalculatedFieldValues();
        return otherDiscount + pointsRedeemed / 1000.0;
    }

    public MutableLiveData<Integer> getNewBalanceMutableData() {
        getCalculatedFieldValues();
        newBalanceMutableData.postValue(newBalance);
        return newBalanceMutableData;
    }


    public MutableLiveData<Integer> getPointsRedeemedMutableData() {
        getCalculatedFieldValues();
        pointsRedeemedMutableData.postValue((int) pointsRedeemed);
        return pointsRedeemedMutableData;
    }

    public MutableLiveData<Integer> getBasePointsMutableData() {
        getCalculatedFieldValues();
        basePointsEarnedMutableData.postValue(basePoints);
        return basePointsEarnedMutableData;
    }


    public MutableLiveData<Integer> getBonusPointsMutableData() {
        getCalculatedFieldValues();
        bonusPointsEarnedMutableData.postValue(bonusPoints);
        return bonusPointsEarnedMutableData;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public int getBasePoints() {
        return basePoints;
    }

    public int getPointsRedeemed() {
        return (int) pointsRedeemed;
    }

    public int getNewBalance() {
        return newBalance;
    }


    public void getCalculatedFieldValues() {
        if (isFirstTym) {
            double pointsRedeemed = 0;
            double basePoints = 0;
            double bonusPoints = 0;
            if (loyaltyPointsMessages != null && loyaltyPointsMessages.size() > 0 && loyaltyPointsMessages.get(0).programId != null) {
                for (LoyaltyPointsMessages loyaltyPointsMessages : loyaltyPointsMessages) {
                    if (loyaltyPointsMessages.programId != null && loyaltyPointsMessages.programId.contains("Base")) {
                        basePoints += loyaltyPointsMessages.earnedRewardSummary;
                    }

                    if (loyaltyPointsMessages.programId != null && loyaltyPointsMessages.programId.contains("Bonus")) {
                        bonusPoints += loyaltyPointsMessages.earnedRewardSummary;
                    }

                    pointsRedeemed += loyaltyPointsMessages.burnedRewardSummary;
                }

                isCLPEDown = false;
                this.pointsRedeemed = pointsRedeemed;
                this.bonusPoints = (int) bonusPoints;
                this.basePoints = (int) basePoints;
                newBalance = (int) (currentBalance + basePoints + bonusPoints - pointsRedeemed);
            } else {
                isCLPEDown = true;
                this.pointsRedeemed = 0;
                this.bonusPoints = 0;
                this.basePoints = 0;
                newBalance = 0;
            }
            isFirstTym = false;
        }
    }

    public static class Address {
        private String streetAddress;
        private String city;
        private String stateCode;
        private String postalCode;
        private String countryIsoCode;

        public String getStreetAddress() {
            return streetAddress;
        }

        public String getCity() {
            return city;
        }

        public String getStateCode() {
            return stateCode;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountryIsoCode() {
            return countryIsoCode;
        }
    }

    public static class LoyaltyPointsMessages {

        public String associatedLoyaltyId;
        public String p97programId;
        public String programName;
        public String loyaltyInstrument;
        public String programId = null;
        public String unit;
        public double earnedRewardSummary;
        public double burnedRewardSummary;
        public double finalRewardsBalance;
        public double finalRewardsLimit;
        public List<PetroRedeemedRewards> RedeemedRewards;

        public int getBurnedRewardSummary() {

            return (int) burnedRewardSummary;
        }
    }

    public static class PetroRedeemedRewards {

        public double amount;
        public String description;
    }

    public enum TransactionStatus {
        NORMAL,
        NO_REDEMPTION
    }


    public TransactionStatus getTransactionStatus(String preAuthRedeemPoints, String preAuthFuelAmount) {
       try {
           double requestedFuelUpAmount = Double.parseDouble(preAuthFuelAmount);
           boolean isUnderPump = subtotal < requestedFuelUpAmount;

           int requestedRedeemPoints = Integer.parseInt(preAuthRedeemPoints);

           if (isCLPEDown || requestedRedeemPoints == 0 || getPointsRedeemed() == requestedRedeemPoints)
               return TransactionStatus.NORMAL;

           if (getPointsRedeemed() == 0)
               return TransactionStatus.NO_REDEMPTION;

           // Under-pump and points redeemed == actual Fuel-up Amount in points
           if (isUnderPump && getPointsRedeemed() == subtotal * 1000) {
               return TransactionStatus.NORMAL;
           }

           return TransactionStatus.NORMAL;
       }catch (Exception e){
           Timber.e(e.getMessage());
           return TransactionStatus.NORMAL;
       }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    public void setTestValues(double pumped,boolean isCLPEDown, double pointsRedeemed ){
        this.subtotal = pumped;
        this.isCLPEDown = isCLPEDown;
        this.pointsRedeemed = pointsRedeemed;
    }
}
