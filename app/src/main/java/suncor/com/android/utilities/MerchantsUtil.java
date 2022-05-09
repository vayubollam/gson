package suncor.com.android.utilities;

import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.ui.main.rewards.MerchantsIds;

public class MerchantsUtil {

    public static String getMerchantSmallImage(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "ultimate_dining_small";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "cineplex_small";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "gap_small_new";
            case MerchantsIds.TIM_HORTONS_EN:
            case MerchantsIds.TIM_HORTONS_FR:
                return "tim_hortons_small_new";
            case MerchantsIds.BEST_BUY_EN:
            case MerchantsIds.BEST_BUY_FR:
                return "best_buy_small";
            case MerchantsIds.WALMART_EN:
            case MerchantsIds.WALMART_FR:
                return "walmart_small";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "hudson_bay_small";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "pc_card_small";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
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
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "Cara";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "Cineplex";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "Gap";
            case MerchantsIds.TIM_HORTONS_EN:
            case MerchantsIds.TIM_HORTONS_FR:
                return "THC";
            case MerchantsIds.BEST_BUY_EN:
            case MerchantsIds.BEST_BUY_FR:
                return "BBC";
            case MerchantsIds.WALMART_EN:
            case MerchantsIds.WALMART_FR:
                return "Walmart";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "HBC";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "PCC";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "TJX";
        }
        return null;
    }

    public static String getMerchantScreenName(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "ultimate-dining-egift-card";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "cineplex-egift-card";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "petro-canada-egift-card";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "hudsons-bay-egift-card";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "homesense-marshalls-winners-egift-card";
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return "gap_egift_card";
            case MerchantsIds.TIM_HORTONS_EN:
            case MerchantsIds.TIM_HORTONS_FR:
                return "tim-hortons_egift_card";
            case MerchantsIds.BEST_BUY_EN:
            case MerchantsIds.BEST_BUY_FR:
                return "best-buy_egift_card";
            case MerchantsIds.WALMART_EN:
            case MerchantsIds.WALMART_FR:
                return "walmart-egift-card";
        }
        return null;
    }
}