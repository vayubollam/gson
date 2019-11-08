package suncor.com.android.model.singleticket;

public class SingleTicketRedeem {
    private String description;
    private int value;
    private int petroPointsRequired;

    public SingleTicketRedeem(String description, int petroPointsRequired, int value) {
        this.description = description;
        this.value = value;
        this.petroPointsRequired = petroPointsRequired;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public int getPetroPointsRequired() {
        return petroPointsRequired;
    }
}