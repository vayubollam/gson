package suncor.com.android.data.repository.account;

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
import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.utilities.Timber;

public class EnrollmentsApiImpl implements EnrollmentsApi {
    private final static String ADAPTER_PATH = "/adapters/suncor/v1/enrollments";

    @Override
    public LiveData<Resource<Integer>> registerAccount(NewEnrollment account) {
        Timber.d("Call enrollments API, account: " + account.getEmail());
        MutableLiveData<Resource<Integer>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT);
            JSONObject body = new JSONObject(new Gson().toJson(account));
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Enrollments API success");
                    try {
                        int rewardedPoints = wlResponse.getResponseJSON().getInt("enrollmentPoints");
                        result.postValue(Resource.success(rewardedPoints));
                    } catch (JSONException e) {
                        Timber.e(e.toString());
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Enrollments API failed, " + wlFailResponse.toString());
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

    @Override
    public LiveData<Resource<EmailState>> checkEmail(String email) {
        Timber.d("validating email: " + email);
        MutableLiveData<Resource<EmailState>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH.concat("/email-validation"));
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT);
            request.addHeader("x-email", email);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("response: " + wlResponse.getResponseText());

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
                    Timber.e(wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            Timber.e(e.toString());
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
        }

        return result;
    }

    @Override
    public LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions() {
        Timber.d("Retrieve security questions");
        MutableLiveData<Resource<ArrayList<SecurityQuestion>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(ADAPTER_PATH.concat("/security-questions"));
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String jsonText = wlResponse.getResponseText();
                    Timber.d("Security Question Response:" + jsonText);
                    try {
                        Gson gson = new Gson();
                        SecurityQuestion[] questions = gson.fromJson(jsonText, SecurityQuestion[].class);
                        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(questions))));
                    } catch (JsonSyntaxException e) {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                        Timber.e("Retrieving security questions failed due to " + e.toString());
                    }
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.e("Retrieving security questions failed due to:" + wlFailResponse.toString());
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
    public LiveData<Resource<CardStatus>> checkCardStatus(String cardNumber, String postalCode, String lastName) {
        Timber.d("Checking card status, cardNumber: " + cardNumber + " , postalCode: " + postalCode + ", lastName: " + lastName);

        MutableLiveData<Resource<CardStatus>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH.concat("/card-status"));
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT);
            request.addHeader("x-card-number", cardNumber);
            request.addHeader("x-postal-code", postalCode);
            request.addHeader("x-last-name", lastName);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String response = wlResponse.getResponseText();
                    Timber.d("card status response:" + response);
                    if (wlResponse.getResponseJSON().keys().hasNext()) {
                        Gson gson = new Gson();
                        CardStatus cardStatus = gson.fromJson(response, CardStatus.class);
                        result.postValue(Resource.success(cardStatus));
                    } else {
                        result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                    }

                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Checking card status failed:" + wlFailResponse.toString());
                    result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return result;
    }
}
