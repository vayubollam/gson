package suncor.com.android.data.profiles;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.utilities.Consumer;
import suncor.com.android.utilities.Timber;

public class ProfilesApiImpl implements ProfilesApi {
    private static final String ADAPTER_PATH = "/adapters/suncor/v1/profiles";

    @Override
    public void retrieveProfile(Consumer<Profile> successCallback, Consumer<String> errorCallback) {
        //TODO
    }

    @Override
    public LiveData<Resource<Boolean>> updateProfile(ProfileRequest profileRequest) {
        Timber.d("Updating profile, account: " + profileRequest.getEmail());
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.PUT, SuncorApplication.DEFAULT_TIMEOUT);
            JSONObject body = new JSONObject(new Gson().toJson(profileRequest));
            Timber.d("Sending request\n" + body.toString());
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Update profile with success");
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Update profile failed, " + wlFailResponse.toString());
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        } catch (JSONException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }
}
