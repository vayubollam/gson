package suncor.com.android.ui.main.rewards;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;

public class RewardsReader {

    private final SuncorApplication application;
    private final Gson gson;

    @Inject
    public RewardsReader(SuncorApplication application, Gson gson) {
        this.application = application;
        this.gson = gson;
    }

    public Reward[] getRewards() {
        InputStream jsonFile = application.getResources().openRawResource(R.raw.rewards_signedin);
        Reward[] rewards = gson.fromJson(new InputStreamReader(jsonFile), Reward[].class);
        return rewards;
    }

    public ThirdPartyGiftCardCategory[] getThirdPartyRawFile() {
        InputStream jsonFile = application.getResources().openRawResource(R.raw.thirdpartyrawresponse);
        return gson.fromJson(new InputStreamReader(jsonFile), ThirdPartyGiftCardCategory[].class);
    }

}
