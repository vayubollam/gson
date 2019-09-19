package suncor.com.android.model.merchants;

class EGift {
    private int id;
    private String description;
    private int value;
    private int petroPointsRequired;
    private int merchantId;

    public EGift(int id, String description, int value, int petroPointsRequired, int merchantId) {
        this.id = id;
        this.description = description;
        this.value = value;
        this.petroPointsRequired = petroPointsRequired;
        this.merchantId = merchantId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getPetroPointsRequired() {
        return petroPointsRequired;
    }

    public void setPetroPointsRequired(int petroPointsRequired) {
        this.petroPointsRequired = petroPointsRequired;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }
}
