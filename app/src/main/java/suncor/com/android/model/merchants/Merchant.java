package suncor.com.android.model.merchants;

import androidx.annotation.Nullable;

import java.util.List;

public class Merchant {
    private String merchantName;
    private int displayOrder;
    private int merchantId;
    private List<EGift> eGifts;

    public Merchant(String merchantName, int displayOrder, int merchantId, List<EGift> eGifts) {
        this.merchantName = merchantName;
        this.displayOrder = displayOrder;
        this.merchantId = merchantId;
        this.eGifts = eGifts;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public List<EGift> geteGifts() {
        return eGifts;
    }

    public void seteGifts(List<EGift> eGifts) {
        this.eGifts = eGifts;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Merchant)) {
            return false;
        }
        return ((Merchant) obj).getMerchantId() == this.getMerchantId();
    }
}
