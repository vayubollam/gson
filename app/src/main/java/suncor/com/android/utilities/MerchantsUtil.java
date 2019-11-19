package suncor.com.android.utilities;

import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.ui.main.rewards.MerchantsIds;

public class MerchantsUtil {

    public static String getMerchantSmallImage(int merchantId) {
        switch (merchantId) {
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

    public static String getRewardSmallImage(PetroCanadaProduct.Category category) {
        switch (category) {
            case WAG:
            case SP:
            case ST:
                return "member_small_cards_cw_ticket";
            case FSR:
        }
        return null;
    }
}