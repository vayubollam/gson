package suncor.com.android.ui.main.rewards;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import suncor.com.android.R;
import suncor.com.android.model.merchants.Merchant;

public class MerchantItem implements Parcelable {
    public static final Creator<MerchantItem> CREATOR = new Creator<MerchantItem>() {
        @Override
        public MerchantItem createFromParcel(Parcel in) {
            return new MerchantItem(in);
        }

        @Override
        public MerchantItem[] newArray(int size) {
            return new MerchantItem[size];
        }
    };
    private Merchant merchant;
    private Context context;

    public MerchantItem(Merchant merchant, Context context) {
        this.merchant = merchant;
        this.context = context;
    }

    protected MerchantItem(Parcel in) {
        merchant = in.readParcelable(Merchant.class.getClassLoader());
    }

    public String getMerchantShortName() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "Cara";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "Petro_Canada";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "Cineplex";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "HBC";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "TJX";
        }
        return null;
    }

    public String getMerchantScreenName() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "ultimate-dining-egift-card";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "petro-canada-egift-card";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "cineplex-egift-card";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "hudsons-bay-egift-card";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "homesense-marshalls-winners-egift-card";
        }
        return null;
    }

    public String getMerchantLargeImage() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "ultimate_dining_large";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "petro_canada_large";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "cineplex_large";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "hbc_large";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "winners_large";
        }
        return null;
    }

    public String getMerchantSmallImage() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return "ultimate_dining_small";
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return "pc_card_small";
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return "cineplex_small";
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return "hbc_small";
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return "winners_small";
        }
        return null;
    }

    public String getSubtitleMerchantName() {
        return context.getResources().getString(R.string.rewards_egift_card_subtitle);
    }

    public String getPointsMerchantName() {
        return context.getResources().getString(R.string.rewards_e_gift_card_starting_points);
    }

    public String getLocalizedMerchantName() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return context.getResources().getString(R.string.merchant_dining_card);
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return context.getResources().getString(R.string.merchant_petrocanada_card);
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return context.getResources().getString(R.string.merchant_cineplex);
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return context.getResources().getString(R.string.merchant_Hudson_bay);
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return context.getResources().getString(R.string.merchant_winners);
            case MerchantsIds.GAP_EN:
            case MerchantsIds.GAP_FR:
                return context.getResources().getString(R.string.merchant_gap_card);
            case MerchantsIds.BEST_BUY_EN:
            case MerchantsIds.BEST_BUY_FR:
                return context.getResources().getString(R.string.merchant_best_buy_card);
            case MerchantsIds.TIM_HORTONS_EN:
            case MerchantsIds.TIM_HORTONS_FR:
                return context.getResources().getString(R.string.merchant_tim_hortons_card);
            case MerchantsIds.WALMART_EN:
            case MerchantsIds.WALMART_FR:
                return context.getResources().getString(R.string.merchant_walmart_card);
        }
        return null;
    }

    public String getRedeemingDescription() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.CARA_EN:
            case MerchantsIds.CARA_FR:
                return context.getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_dining_card);
            case MerchantsIds.PETRO_CANADA_EN:
            case MerchantsIds.PETRO_CANADA_FR:
                return context.getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_petro_canada);
            case MerchantsIds.CINEPLEX_EN:
            case MerchantsIds.CINEPLEX_FR:
                return context.getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_cineplex);
            case MerchantsIds.HUDSON_BAY_EN:
            case MerchantsIds.HUDSON_BAY_FR:
                return context.getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_Hudson_bay);
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_EN:
            case MerchantsIds.WINNERS_HOMESENSE_MARSHALLS_FR:
                return context.getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_winners);
        }
        return "";
    }

    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(merchant, flags);
    }
}
