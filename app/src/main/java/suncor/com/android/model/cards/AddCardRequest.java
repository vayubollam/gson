package suncor.com.android.model.cards;

import com.google.gson.annotations.SerializedName;

public class AddCardRequest {
    private Category cardCategory;
    private String cardNumber;
    private String cardCVV;

    public AddCardRequest(Category cardCategory, String cardNumber, String cardCVV) {
        this.cardCategory = cardCategory;
        this.cardNumber = cardNumber;
        this.cardCVV = cardCVV;
    }

    public Category getCardCategory() {
        return cardCategory;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardCVV() {
        return cardCVV;
    }

    public enum Category {
        @SerializedName("FSR") FSR,
        @SerializedName("PPC") PPC,
        @SerializedName("CARWASH") CARWASH
    }
}
