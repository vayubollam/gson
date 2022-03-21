package suncor.com.android.data.resetpassword;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import suncor.com.android.model.resetpassword.ResetPasswordRequest;
import suncor.com.android.model.resetpassword.SecurityQuestion;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.Constants.ACCEPT_LANGUAGE;

public class ForgotPasswordProfileApiImpl implements ForgotPasswordProfileApi {


    private final static String SEND_EMAIL_ON_FORGOT_PASSWORD_ADAPTER_PATH = "adapters/suncor/v5/rfmp-secure/profiles/forgot-password";
    private final static String UPDATE_PASSWORD_ADAPTER_PATH = "adapters/suncor/v5/rfmp-secure/profiles/forgot-password";
    private final static String GET_SECURITY_ANSWER_VERIFICATION_TO_RESET_PASSWORD_ADAPTER_PATH = "adapters/suncor/v5/rfmp-secure/profiles/forgot-password/security-answer-verification";
    private final static String GET_SECURITY_QUESTION_TO_RESET_PASSWORD_ADAPTER_PATH = "adapters/suncor/v4/rfmp-secure/profiles/forgot-password/security-question";
    private Gson gson;

    public ForgotPasswordProfileApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<Boolean>> generateResetPasswordEmail(String email) {
        Timber.d("Call PROFILE FORGOT PASSWORD API");
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = createURI(SEND_EMAIL_ON_FORGOT_PASSWORD_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.addHeader("X-Email",email);
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader(ACCEPT_LANGUAGE, "fr-CA");
            } else {
                request.addHeader(ACCEPT_LANGUAGE, "en-CA");
            }
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Profile forgot password API success");
                    String response = wlResponse.getResponseText();
                    Timber.d("Profile forgot password API success, response:\n" + response);
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Profile forgot password API  " + wlFailResponse.toString());
                    int remainingMinutes = 0 ;
                    try {
                        JSONObject failJSONObject = wlFailResponse.getResponseJSON();
                        if (failJSONObject != null) {
                            remainingMinutes = failJSONObject.getInt("remainingMinutes");
                            result.postValue(Resource.error(wlFailResponse.getErrorMsg() + ";" + remainingMinutes));
                        } else {
                            result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                        }
                    } catch (JSONException e) {
                        result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                        e.printStackTrace();
                    }
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }
        return result;
    }

    @Override
    public LiveData<Resource<SecurityQuestion>> getSecurityQuestionToResetPassword(String GUID) {
            Timber.d("Retrieve security question");
            MutableLiveData<Resource<SecurityQuestion>> result = new MutableLiveData<>();
            result.postValue(Resource.loading());
            URI adapterPath;
            try {
                adapterPath = new URI(GET_SECURITY_QUESTION_TO_RESET_PASSWORD_ADAPTER_PATH);
                WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
                request.addHeader("X-Guid",GUID);
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
    public LiveData<Resource<String>> validateSecurityQuestion(String questionId,String answer, String profileIdEncrypted, String GUID) {
        Timber.d("validating security question");
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(GET_SECURITY_ANSWER_VERIFICATION_TO_RESET_PASSWORD_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.addHeader("X-Security-Question-Id", questionId);
            request.addHeader("X-Security-Answer", answer);
            request.addHeader("X-Profile-Id-Encrypted", profileIdEncrypted);
            request.addHeader("X-GUID", GUID);
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

    @Override
    public LiveData<Resource<Boolean>> resetPassword(String profileIdEncrypted, String GUID, ResetPasswordRequest resetPasswordRequest) {
        Timber.d("Call Reset PASSWORD API");
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = createURI(UPDATE_PASSWORD_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.PUT, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.addHeader("X-Profile-Id-Encrypted",profileIdEncrypted);
            request.addHeader("X-GUID",GUID);

            JSONObject body = new JSONObject(gson.toJson(resetPasswordRequest));

            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Reset password API success");
                    String response = wlResponse.getResponseText();
                    Timber.d("Reset password API success, response:\n" + response);
                    result.postValue(Resource.success(true));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Reset password API  " + wlFailResponse.toString());
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

    private URI createURI(String uri) throws URISyntaxException {
        return new URI(uri);
    }
}
