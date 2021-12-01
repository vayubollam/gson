package suncor.com.android.data.profiles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.utilities.Consumer;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.Constants.ACCEPT_LANGUAGE;

public class ProfilesApiImpl implements ProfilesApi {
    private static final String EDIT_PROFILES_ADAPTER_PATH = "/adapters/suncor/v9/rfmp-secure/profiles";
    private static final String GET_SECURITY_QUESTION_ON_PROFILES_ADAPTER_PATH = "/adapters/suncor/v5/rfmp-secure/profiles/security-question";
    private static final String SECURITY_ANSWER_VERIFICATION_ON_PROFILES_ADAPTER_PATH = "/adapters/suncor/v7/rfmp-secure/profiles/security-answer-verification";
    private final Gson gson;

    public ProfilesApiImpl(Gson gson) {
        this.gson = gson;
    }


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
            URI adapterPath = new URI(EDIT_PROFILES_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.PUT, SuncorApplication.DEFAULT_TIMEOUT);
            JSONObject body = new JSONObject(gson.toJson(profileRequest));
            Timber.d("Sending request\n" + body.toString());
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader(ACCEPT_LANGUAGE, "fr-CA");
            } else {
                request.addHeader(ACCEPT_LANGUAGE, "en-CA");
            }
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
        } catch (URISyntaxException | JSONException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }

    @Override
    public LiveData<Resource<SecurityQuestion>> getSecurityQuestion() {
        Timber.d("Retrieve security question");
        MutableLiveData<Resource<SecurityQuestion>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(GET_SECURITY_QUESTION_ON_PROFILES_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader(ACCEPT_LANGUAGE, "fr-CA");
            } else {
                request.addHeader(ACCEPT_LANGUAGE, "en-CA");
            }
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Security Question Response:" + jsonText);
                    try {
                        SecurityQuestion question = gson.fromJson(jsonText, SecurityQuestion.class);
                        result.postValue(Resource.success(question));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Timber.e("Retrieving security question failed due to " + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.e("Retrieving security question failed due to:" + wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });

        } catch (URISyntaxException e) {
            result.postValue(Resource.error(e.getMessage()));
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public LiveData<Resource<String>> validateSecurityQuestion(String answer) {
        Timber.d("validating security question");
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(SECURITY_ANSWER_VERIFICATION_ON_PROFILES_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            String base64Answer = BaseEncoding.base64().encode(answer.getBytes());
            request.addHeader("x-security-answer", base64Answer);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Security Question validation Response:" + jsonText);
                    try {
                        String securityAnswerEncrypted = wlResponse.getResponseJSON().getString("securityAnswerEncrypted");
                        result.postValue(Resource.success(securityAnswerEncrypted));
                    } catch (JsonSyntaxException | JSONException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Timber.e("Retrieving security question failed due to " + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.e("Retrieving security question failed due to:" + wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });

        } catch (URISyntaxException e) {
            result.postValue(Resource.error(e.getMessage()));
            e.printStackTrace();
        }

        return result;
    }
}
