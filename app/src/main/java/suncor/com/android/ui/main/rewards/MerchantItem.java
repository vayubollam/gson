package suncor.com.android.ui.main.rewards;


import suncor.com.android.model.merchants.Merchant;

public class MerchantItem {
    private Merchant merchant;

    public MerchantItem(Merchant merchant) {
        this.merchant = merchant;
    }

    String getMerchantLargeImage() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "dining_large";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "cineplex_large";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "hbc_large";
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return "winners_large";
        }
        return null;
    }

    String getMerchantSmallImage() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "dining_small";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "cineplex_small";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "hbc_small";
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return "winners_small";
        }
        return null;
    }

    public Merchant getMerchant() {
        return merchant;
    }
}
