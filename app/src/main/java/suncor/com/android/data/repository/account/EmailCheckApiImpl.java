package suncor.com.android.data.repository.account;

import android.util.Log;

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
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;

public class EmailCheckApiImpl implements EmailCheckApi {

    private final static String ADAPTER_PATH = "/adapters/suncor/v1/enrollments/email-validation";

    @Override
    public LiveData<Resource<EmailState>> checkEmail(String email) {
        Log.d(EmailCheckApiImpl.class.getSimpleName(), "validating email: " + email);
        MutableLiveData<Resource<EmailState>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
            request.addHeader("x-email", email);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Log.d(EmailCheckApiImpl.class.getSimpleName(), "response: " + wlResponse.getResponseText());

                    JSONObject json = wlResponse.getResponseJSON();
                    if (json == null) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                    try {
                        boolean isAlreadyRegistered = json.getBoolean("isAlreadyRegistered");
                        if (isAlreadyRegistered) {
                            result.postValue(Resource.success(EmailState.INVALID));
                        } else {
                            result.postValue(Resource.success(EmailState.VALID));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.e(EmailCheckApiImpl.class.getSimpleName(), wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Log.e(EmailCheckApiImpl.class.getSimpleName(), e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }
}
