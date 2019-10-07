package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class RedeemTransaction implements Parcelable {

    @SerializedName("transactionType")
    private TransactionType transactionType; // redemption

    @SerializedName("transactionAmount")
    private RedeemTransactionAmount redeemTransactionAmount;

    @SerializedName("card")
    private RedeemCard redeemCard;

    public enum TransactionType {
        @SerializedName("purchase")
        PURCHASE,
        @SerializedName("redemption")
        REDEMPTION
    }

    public RedeemTransaction(TransactionType transactionType, RedeemTransactionAmount redeemTransactionAmount, RedeemCard redeemCard) {
        this.transactionType = transactionType;
        this.redeemTransactionAmount = redeemTransactionAmount;
        this.redeemCard = redeemCard;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public RedeemTransactionAmount getRedeemTransactionAmount() {
        return redeemTransactionAmount;
    }

    public void setRedeemTransactionAmount(RedeemTransactionAmount redeemTransactionAmount) {
        this.redeemTransactionAmount = redeemTransactionAmount;
    }

    public RedeemCard getRedeemCard() {
        return redeemCard;
    }

    public void setRedeemCard(RedeemCard redeemCard) {
        this.redeemCard = redeemCard;
    }

    protected RedeemTransaction(Parcel in) {
        transactionType = TransactionType.valueOf(in.readString());
        redeemCard = in.readParcelable(RedeemCard.class.getClassLoader());
        redeemTransactionAmount = in.readParcelable(RedeemTransactionAmount.class.getClassLoader());
    }

    public static final Creator<RedeemTransaction> CREATOR = new Creator<RedeemTransaction>() {
        @Override
        public RedeemTransaction createFromParcel(Parcel in) {
            return new RedeemTransaction(in);
        }

        @Override
        public RedeemTransaction[] newArray(int size) {
            return new RedeemTransaction[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        dest.writeString(transactionType.name());
        dest.writeParcelable(redeemTransactionAmount, flag);
        dest.writeParcelable(redeemCard, flag);

    }
}
