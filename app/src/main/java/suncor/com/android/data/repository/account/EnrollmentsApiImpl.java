package suncor.com.android.data.repository.account;

import android.util.Log;

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
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.NewEnrollment;
import suncor.com.android.model.Resource;

public class EnrollmentsApiImpl implements EnrollmentsApi {
    private final static String ADAPTER_PATH = "/adapters/suncor/v1/enrollments";

    @Override
    public LiveData<Resource<Boolean>> registerAccount(NewEnrollment account) {
        Log.d(EnrollmentsApi.class.getSimpleName(), "Call enrollments API, account: " + account.getEmail());
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST);
            JSONObject body = new JSONObject(new Gson().toJson(account));
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Log.d(EnrollmentsApi.class.getSimpleName(), "Enrollments API success response\n" + wlResponse.getResponseText());
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(EnrollmentsApi.class.getSimpleName(), "Enrollments API failed, " + wlFailResponse.toString());
                    Log.e(EmailCheckApiImpl.class.getSimpleName(), wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Log.e(EmailCheckApiImpl.class.getSimpleName(), e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        } catch (JSONException e) {
            Log.e(EnrollmentsApiImpl.class.getSimpleName(), e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }
}
