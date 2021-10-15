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
    private final MutableLiveData<String> correspondingDollars = new MutableLiveData<>();
    private final MutableLiveData<String> rbcAlongWithRedemption = new MutableLiveData<>();

    private double taxAmount;
    private String formattedOtherDiscounts;
    private String formattedSubtotal;
    private String formattedTax;
    private String formattedTotal;
    private List<LineItem> lineItems;
    private List<LoyaltyPointsMessages> loyaltyPointsMessages;

    public List<String> receiptData;
    private String terminalType;
    private String fuelBrand;
    private String appChannel;
    private String appDisplayName;
    private String basketPaymentState;
    private String storeTenantId;
    private String storeTenantName;
    private String storeNumber;
    private String posTransactionId;


    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    public Address getAddress() {
        return address;
    }

    public String getAuthCode() {
        return authCode;
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
        receiptData.forEach(data-> sb.append(data).append("\n") );
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

    public String getPaymentType(Context context, boolean isGooglePay){
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

    public List<LoyaltyPointsMessages> getLoyaltyPointsMessages() {
        return loyaltyPointsMessages;
    }

    public void setLoyaltyPointsMessages(List<LoyaltyPointsMessages> loyaltyPointsMessages) {
        this.loyaltyPointsMessages = loyaltyPointsMessages;
    }

    public static class LoyaltyPointsMessages{

        public String associatedLoyaltyId;
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

    public static class PetroRedeemedRewards{
        public double amount;
        public String description;
    }

    public MutableLiveData<String> getCorrespondingDollarOffForBurnedPoints(){
        loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
          correspondingDollars.postValue(formatter.format(loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0));
          return correspondingDollars;
    }

    public MutableLiveData<String> getRbcAlongWithRedemptionSavingsMutableData(){
        loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
        rbcAlongWithRedemption.postValue(formatter.format(otherDiscount + loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0));
        return rbcAlongWithRedemption;

    }

    public double getRbcAlongWithRedemptionSavings(){
        loyaltyPointsMessages.get(0).burnedRewardSummary   = (int)  loyaltyPointsMessages.get(0).burnedRewardSummary;
        return otherDiscount + loyaltyPointsMessages.get(0).burnedRewardSummary / 1000.0;

    }

}
