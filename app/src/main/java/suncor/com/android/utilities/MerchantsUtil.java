package suncor.com.android.utilities;

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

    public static String getMerchantShortName(int merchantId) {
        switch (merchantId) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return "Cara";
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return "Cineplex";
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "HBC";
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
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return "hudsons-bay-egift-card";
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return "homesense-marshalls-winners-egift-card";
        }
        return null;
    }
}
