package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Transaction implements Parcelable {

    private String transactionDate;
    @SerializedName("transactionType")
    private TransactionType transactionType;
    private TransactionAmount transactionAmount;
    private Card card;


    public Transaction(String transactionDate, TransactionType transactionType, TransactionAmount transactionAmount, Card card) {
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.transactionAmount = transactionAmount;
        this.card = card;
    }

    protected Transaction(Parcel in) {
        transactionType = TransactionType.valueOf(in.readString());
        transactionDate = in.readString();
        transactionAmount = in.readParcelable(TransactionAmount.class.getClassLoader());
        card = in.readParcelable(Card.class.getClassLoader());
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public TransactionAmount getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(TransactionAmount transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(transactionType.name());
        parcel.writeString(transactionDate);
        parcel.writeParcelable(transactionAmount, i);
        parcel.writeParcelable(card, i);
    }
    
    public enum TransactionType {
        @SerializedName("purchase")
        PURCHASE,
        @SerializedName("redemption")
        REDEMPTION
    }
}
