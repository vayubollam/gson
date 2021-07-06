package suncor.com.android.data.users;

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
import suncor.com.android.utilities.Timber;

public class UsersApiImpl implements UsersApi {
    private final static String UPDATE_PHRASE_IN_PROFILE_ADAPTER_PATH = "/adapters/suncor/v2/rfmp-secure/users/passwords";


    @Override
    public LiveData<Resource<Boolean>> createPassword(String email, String password, String emailEncrypted) {
        Timber.d("create password for account: " + email);
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(UPDATE_PHRASE_IN_PROFILE_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.PUT, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("emailEncrypted", emailEncrypted);
            body.put("newPassword", password);
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Password created with success");
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Passwords API failed, " + wlFailResponse.toString());
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
