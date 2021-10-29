package suncor.com.android.model.cards;

import androidx.annotation.Nullable;

public class CardDetail {

    public static final int INVALID_BALANCE = -1;

    private CardType cardType;
    private String cardNumber;
    private String cardNumberEncrypted;
    private String serviceId;
    private String ticketNumber;
    private String status;
    private int pointsBalance = INVALID_BALANCE;
    private int litresRemaining = INVALID_BALANCE;
    private int unitsRemaining = INVALID_BALANCE;
    private int daysRemaining = INVALID_BALANCE;

    //Defaulting to 5cent if the balance service is down
    private float cpl = 0.05f;

    public CardDetail(CardType cardType, String cardNumber, int pointsBalance) {
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.pointsBalance = pointsBalance;
    }

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

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getStatus() {
        return status;
    }

    public int getBalance() {
        switch (cardType) {
            case PPTS:
                return pointsBalance;
            case FSR:
            case PPC:
                return litresRemaining;
            case ST:
            case WAG:
                return unitsRemaining;
            case SP:
                return daysRemaining;
            default:
                return INVALID_BALANCE;
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
            case ST:
                return CardCategory.PETRO_CANADA;
            default:
                return CardCategory.PARTNER;
        }
    }

    public String getCardName() {
        switch (cardType) {
            case FSR:
                return "fsr-" + (cpl == 0.05 ? 5 : 10) + "c";
            case PPC:
                return "ppc";
            case WAG:
                return "wash-and-go";
            case SP:
                return "season-pass";
            case CAA:
                return "caa";
            case BCAA:
                return "bcaa";
            case HBC:
                return "bbc";
            case MORE:
                return "more-rewards";
            case RBC:
                return "rbc";
            case ST:
                return "single_ticket";
        }
        return "card";
    }

    public String getFirebaseScreenName() {
        return "my-petro-points-wallet-view-"+getCardName();
    }

    public String getFirebaseCarwashScreenName() {
        return "car-wash-card-view-"+getCardName();
    }

    public String getLongName() {
        switch (cardType) {
            case PPTS:
                return "Petro Points Card";
            case FSR:
                return "Fuel Saving Reward Card";
            case PPC:
                return "Preferred Price Card";
            case SP:
                return "Season Pass";
            case WAG:
                return "Wash and Go Card";
            case MORE:
                return "More Rewards Partner Card";
            case CAA:
                return "CAA";
            case BCAA:
                return "BCAA";
            case HBC:
                return "Hudson Bay Company Partner Card";
            case RBC:
                return "RBC Credit Card";
            default:
                return "";
        }
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof CardDetail)) {
            return false;
        } else {
            CardDetail cardDetail = (CardDetail) obj;
            if (cardDetail.cardType != CardType.ST) {
                return (cardDetail.getCardType() == CardType.RBC && this.getCardType() == CardType.RBC)
                        || cardDetail.getCardNumber().equals(this.getCardNumber());
            } else {
                return cardDetail.getTicketNumber().equals(this.getTicketNumber());
            }
        }
    }

    public float getCpl() {
        return cpl;
    }

    public enum CardCategory {
        PPTS, PETRO_CANADA, PARTNER
    }

    public boolean isSuspendedCard(){
        return (status != null && status.equals("Suspended"));
    }
}
