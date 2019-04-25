package suncor.com.android.model.cards;

public class CardDetail {
    private CardType cardType;
    private String cardNumber;
    private String cardNumberEncrypted;
    private String serviceId;
    private int pointsBalance = -1;
    private int litresRemaining = -1;
    private int unitsRemaining = -1;
    private int daysRemaining = -1;
    private float cpl;

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

    public float getCpl() {
        return cpl;
    }

    public enum CardCategory {
        PPTS, PETRO_CANADA, PARTNER
    }
}
