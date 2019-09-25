package suncor.com.android.ui.main.rewards;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import suncor.com.android.R;
import suncor.com.android.model.merchants.Merchant;

public class MerchantItem implements Parcelable {
    private Merchant merchant;
    private Context context;

    public MerchantItem(Merchant merchant, Context context) {
        this.merchant = merchant;
        this.context = context;
    }

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

    protected MerchantItem(Parcel in) {
        merchant = in.readParcelable(Merchant.class.getClassLoader());
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

    public String getMerchantSmallImage() {
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

    public String getLocalizedMerchantName() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return context.getResources().getString(R.string.merchant_dining_card);
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return context.getResources().getString(R.string.merchant_cineplex);
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return context.getResources().getString(R.string.merchant_Hudson_bay);
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return context.getResources().getString(R.string.merchant_winners);
        }
        return null;
    }

    public int getRedeemingDescription() {
        switch (merchant.getMerchantId()) {
            case MerchantsIds.Cara_EN:
            case MerchantsIds.Cara_FR:
                return R.string.rewards_signedin_redeeming_your_rewards_desc_dining_card;
            case MerchantsIds.Cineplex_EN:
            case MerchantsIds.Cineplex_FR:
                return R.string.rewards_signedin_redeeming_your_rewards_desc_cineplex;
            case MerchantsIds.Hudson_Bay_EN:
            case MerchantsIds.Hudson_Bay_FR:
                return R.string.rewards_signedin_redeeming_your_rewards_desc_Hudson_bay;
            case MerchantsIds.WINNERS_HomeSense_Marshalls_EN:
            case MerchantsIds.WINNERS_HomeSense_Marshalls_FR:
                return R.string.rewards_signedin_redeeming_your_rewards_desc_winners;
        }
        return 0;
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
