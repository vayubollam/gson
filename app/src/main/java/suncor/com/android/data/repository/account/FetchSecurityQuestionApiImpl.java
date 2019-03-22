package suncor.com.android.data.repository.account;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SecurityQuestion;

public class FetchSecurityQuestionApiImpl implements FetchSecurityQuestionApi {
    private final static String ADAPTER_PATH = "/adapters/suncor/v1/enrollments/security-questions";

    @Override
    public LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions() {
        Log.d(FetchSecurityQuestionApiImpl.class.getSimpleName(), "Retrieve security questions");
        MutableLiveData<Resource<ArrayList<SecurityQuestion>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Log.d(FetchSecurityQuestionApiImpl.class.getSimpleName(), "Security Question Response:" + jsonText);
                    try {
                        Gson gson = new Gson();
                        SecurityQuestion[] questions = gson.fromJson(jsonText, SecurityQuestion[].class);
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(questions))));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Log.e(FetchSecurityQuestionApiImpl.class.getSimpleName(), "Retrieving security questions failed due to " + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.e(FetchSecurityQuestionApiImpl.class.getSimpleName(), "Retrieving security questions failed due to:" + wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }
}
