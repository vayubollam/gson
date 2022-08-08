package suncor.com.android.data.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;

import suncor.com.android.SuncorApplication;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.utilities.SharedPrefsHelper;
import suncor.com.android.utilities.Timber;

public class SettingsApiImpl implements SettingsApi {
    private static final String GET_SETTINGS_ADAPTER_PATH = "/adapters/suncor/v9/rfmp-secure/settings";
    private Gson gson;
    private SharedPrefsHelper sharedPrefsHelper;

    public SettingsApiImpl(Gson gson, SharedPrefsHelper sharedPrefsHelper) {
        this.gson = gson;
        this.sharedPrefsHelper = sharedPrefsHelper;
    }

    @Override
    public LiveData<Resource<SettingsResponse>> retrieveSettings() {
        Timber.d("Retrieving MFP settings");

        MutableLiveData<Resource<SettingsResponse>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterURI = new URI(GET_SETTINGS_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterURI, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Settings API response:\n" + jsonText);
                    SettingsResponse settingsResponse = gson.fromJson(jsonText, SettingsResponse.class);
                    if (settingsResponse != null && settingsResponse.getSettings() != null && settingsResponse.getSettings().toggleFeature != null) {
                        sharedPrefsHelper.put(SharedPrefsHelper.SETTING_VACUUM_TOGGLE, settingsResponse.getSettings().toggleFeature.isVacuumScanBarcode());
                        sharedPrefsHelper.put(SharedPrefsHelper.SETTING_DONATE_TOGGLE, settingsResponse.getSettings().toggleFeature.isDonatePetroPoints());
                    }
                    result.postValue(Resource.success(settingsResponse));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.e("Retrieving settings failed due to " + wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            result.postValue(Resource.error(e.getMessage()));
        }
        return result;
    }
}
