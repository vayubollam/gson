package suncor.com.android.data.repository.account;

import android.util.Log;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SecurityQuestion;

public class FetchSecurityQuestionApiImpl implements FetchSecurityQuestionApi {
    private final static String ADAPTER_PATH = "/adapters/suncor/v1/enrollments/security-questions";

    @Override
    public LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions() {
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
                    Log.d(FetchSecurityQuestionApiImpl.class.getSimpleName(), "security Question Response:" + jsonText);
                    try {
                        final JSONArray jsonArray = new JSONArray(jsonText);
                        Gson gson = new Gson();
                        ArrayList<SecurityQuestion> questions = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jo = jsonArray.getJSONObject(i);
                            SecurityQuestion question = gson.fromJson(jo.toString(), SecurityQuestion.class);
                            questions.add(question);
                        }
                        result.postValue(Resource.success(questions));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result.postValue(Resource.error(e.getMessage()));
                        Log.d(FetchSecurityQuestionApiImpl.class.getSimpleName(), "security Question Response:" + jsonText);
                    }

                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Log.d(FetchSecurityQuestionApiImpl.class.getSimpleName(), "security Question Response:" + wlFailResponse.getErrorMsg());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }
}
