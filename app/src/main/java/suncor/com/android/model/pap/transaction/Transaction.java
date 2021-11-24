package suncor.com.android.model.pap.transaction;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import suncor.com.android.R;

public class Transaction {

    private final MutableLiveData<String> correspondingDollars = new MutableLiveData<>();
    private final MutableLiveData<String> rbcAlongWithRedemption = new MutableLiveData<>();
    private final MutableLiveData<Integer> basePointsEarnedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> bonusPointsEarnedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> pointsRedeemedMutableData = new MutableLiveData<>();
    private final MutableLiveData<Integer> newBalanceMutableData = new MutableLiveData<>();
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
    private int pointsRedeemed = 0;
    private int currentBalance;
    private int newBalance = 0;


    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

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
        return formattedSubtotal;
    }

    public String getFormattedTax() {
        return formattedTax;
    }

    public String getFormattedTotal() {
        return formatter.format(totalAmount);
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
        return DateFormat.getDateInstance(DateFormat.LONG).format(date);
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
        return pointsRedeemed;
    }

    public MutableLiveData<String> getCorrespondingDollarOffForBurnedPointsMutableData() {
        getCalculatedFieldValues();
        correspondingDollars.postValue(formatter.format(pointsRedeemed / 1000.0));
        return correspondingDollars;
    }

    public int getCorrespondingDollarOff(){
        return (int) (pointsRedeemed/1000.0);
    }

    public MutableLiveData<String> getRbcAlongWithRedemptionSavingsMutableData() {
        getCalculatedFieldValues();
        rbcAlongWithRedemption.postValue(formatter.format(otherDiscount + pointsRedeemed / 1000.0));
        return rbcAlongWithRedemption;
    }

    public double getRbcAlongWithRedemptionSavings() {
        getCalculatedFieldValues();
        return (int) otherDiscount + pointsRedeemed / 1000.0;

    }

    public MutableLiveData<Integer> getNewBalanceMutableData() {
        getCalculatedFieldValues();
        newBalanceMutableData.postValue(newBalance);
        return newBalanceMutableData;
    }


    public MutableLiveData<Integer> getPointsRedeemedMutableData() {
        getCalculatedFieldValues();
        pointsRedeemedMutableData.postValue(pointsRedeemed);
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

    public int getBonusPoints(){
        return bonusPoints;
    }

    public int getBasePoints(){
        return basePoints;
    }

    public int getPointsRedeemed(){
        return pointsRedeemed;
    }

    public int getNewBalance(){
        return newBalance;
    }


    public void getCalculatedFieldValues() {
        if (isFirstTym) {
            double pointsRedeemed = 0;
            double basePoints = 0;
            double bonusPoints = 0;
            if (loyaltyPointsMessages != null) {
                for (LoyaltyPointsMessages loyaltyPointsMessages : loyaltyPointsMessages) {
                    if (loyaltyPointsMessages.programId.equals("Base Points")) {
                        basePoints += loyaltyPointsMessages.earnedRewardSummary;
                        pointsRedeemed += loyaltyPointsMessages.burnedRewardSummary;
                    }
                    if (loyaltyPointsMessages.programId.equals("RBC Bonus")) {
                        bonusPoints += loyaltyPointsMessages.earnedRewardSummary;
                        pointsRedeemed += loyaltyPointsMessages.burnedRewardSummary;
                    }

                    if (loyaltyPointsMessages.programId.equals("HBC Bonus")) {
                        bonusPoints += loyaltyPointsMessages.earnedRewardSummary;
                        pointsRedeemed += loyaltyPointsMessages.burnedRewardSummary;
                    }

                    if (loyaltyPointsMessages.programId.equalsIgnoreCase("Others")) {
                        bonusPoints += loyaltyPointsMessages.earnedRewardSummary;
                        pointsRedeemed += loyaltyPointsMessages.burnedRewardSummary;

                    }

                }

                this.pointsRedeemed = (int) pointsRedeemed;
                this.bonusPoints = (int) bonusPoints;
                this.basePoints = (int) basePoints;
                newBalance = currentBalance +this.basePoints+this.bonusPoints- this.pointsRedeemed;
            } else {
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
        public String programId;
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

    /*  public MutableLiveData<String> getCorrespondingDollarOffForBurnedPoints(){
        if(loyaltyPointsMessages != null){
            loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
            correspondingDollars.postValue(formatter.format(loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0));

        }else{
            correspondingDollars.postValue("0");
        }
        return correspondingDollars;
    }*/

    //    public double getRbcAlongWithRedemptionSavings(){
//        if(loyaltyPointsMessages != null){
//            loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
//            return otherDiscount + loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0;
//        }else{
//            return 0.0;
//        }
//    }


      /* public MutableLiveData<String> getRbcAlongWithRedemptionSavingsMutableData(){
        if(loyaltyPointsMessages != null){
            loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
            rbcAlongWithRedemption.postValue(formatter.format(otherDiscount + loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0));
        }else{
            rbcAlongWithRedemption.postValue("0");
        }
        return rbcAlongWithRedemption;
    }*/


}
