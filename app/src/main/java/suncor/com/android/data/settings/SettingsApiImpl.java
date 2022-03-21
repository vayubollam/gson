package suncor.com.android.data.settings;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;

import suncor.com.android.SuncorApplication;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.utilities.Timber;

public class SettingsApiImpl implements SettingsApi {
    private static final String GET_SETTINGS_ADAPTER_PATH = "/adapters/suncor/v6/rfmp-secure/settings";
    private Gson gson;

    public SettingsApiImpl(Gson gson) {
        this.gson = gson;
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
