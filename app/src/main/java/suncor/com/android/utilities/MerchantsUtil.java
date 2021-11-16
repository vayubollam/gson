package suncor.com.android.utilities;

import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.ui.main.rewards.MerchantsIds;

public class MerchantsUtil {

    public static String getMerchantSmallImage(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "ultimate_dining_small";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "cineplex_small";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "gap_small_new";
            case MerchantsIds.Tim_hortons_EN:
            case MerchantsIds.Tim_hortons_FR:
                return "tim_hortons_small_new";
            case MerchantsIds.Best_Buy_EN:
            case MerchantsIds.Best_Buy_FR:
                return "best_buy_small";
            case MerchantsIds.Walmart_EN:
            case MerchantsIds.Walmart_FR:
                return "walmart_small";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "hbc_small";
            case MerchantsIds.Petro_Canada_EN:
            case MerchantsIds.Petro_Canada_FR:
                return "pc_card_small";
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

    public static String getMerchantShortName(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "Cara";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "Cineplex";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "Gap";
            case MerchantsIds.Tim_hortons_EN:
            case MerchantsIds.Tim_hortons_FR:
                return "TIm Hortons";
            case MerchantsIds.Best_Buy_EN:
            case MerchantsIds.Best_Buy_FR:
                return "Best Buy";
            case MerchantsIds.Walmart_EN:
            case MerchantsIds.Walmart_FR:
                return "Walmart";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "HBC";
            case MerchantsIds.Petro_Canada_EN:
            case MerchantsIds.Petro_Canada_FR:
                return "Petro_Canada";
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return "TJX";
        }
        return null;
    }

    public static String getMerchantScreenName(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "ultimate-dining-egift-card";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "cineplex-egift-card";
            case MerchantsIds.Petro_Canada_EN:
            case MerchantsIds.Petro_Canada_FR:
                return "petro-canada-egift-card";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "hudsons-bay-egift-card";
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return "homesense-marshalls-winners-egift-card";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "Gap_egift_card";
            case MerchantsIds.Tim_hortons_EN:
            case MerchantsIds.Tim_hortons_FR:
                return "TIm Hortons_egift_card";
            case MerchantsIds.Best_Buy_EN:
            case MerchantsIds.Best_Buy_FR:
                return "Best Buy_egift_card";
            case MerchantsIds.Walmart_EN:
            case MerchantsIds.Walmart_FR:
                return "Walmart_egift_card";
        }
        return null;
    }
}