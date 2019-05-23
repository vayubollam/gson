package suncor.com.android.model.cards;

import androidx.annotation.Nullable;

public class CardDetail {

    public static final int INVALID_BALANCE = -1;

    private CardType cardType;
    private String cardNumber;
    private String cardNumberEncrypted;
    private String serviceId;
    private int pointsBalance = INVALID_BALANCE;
    private int litresRemaining = INVALID_BALANCE;
    private int unitsRemaining = INVALID_BALANCE;
    private int daysRemaining = INVALID_BALANCE;

    //Defaulting to 5cent if the balance service is down
    private float cpl = 0.05f;

    public CardType getCardType() {
        return cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardNumberEncrypted() {
        return cardNumberEncrypted;
    }

    public String getServiceId() {
        return serviceId;
    }

    public int getBalance() {
        switch (cardType) {
            case PPTS:
                return pointsBalance;
            case FSR:
            case PPC:
                return litresRemaining;
            case WAG:
                return unitsRemaining;
            case SP:
                return daysRemaining;
            default:
                throw new IllegalStateException("partner cards have no balance");
        }
    }

    public CardCategory getCardCategory() {
        switch (cardType) {
            case PPTS:
                return CardCategory.PPTS;
            case FSR:
            case PPC:
            case SP:
            case WAG:
                return CardCategory.PETRO_CANADA;
            default:
                return CardCategory.PARTNER;
        }
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CardDetail)) {
            return false;
        } else {
            CardDetail cardDetail = (CardDetail) obj;
            return (cardDetail.getCardType() == CardType.RBC && this.getCardType() == CardType.RBC)
                    || cardDetail.getCardNumber().equals(this.getCardNumber());
        }
    }

    public float getCpl() {
        return cpl;
    }

    public enum CardCategory {
        PPTS, PETRO_CANADA, PARTNER
    }
}
