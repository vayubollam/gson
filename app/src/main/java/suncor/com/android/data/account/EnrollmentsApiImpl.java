package suncor.com.android.data.account;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.EnrollmentPointsAndHours;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.Constants.ACCEPT_LANGUAGE;

public class EnrollmentsApiImpl implements EnrollmentsApi {
    private final static String ENROLLMENT_ADAPTER_PATH = "/adapters/suncor/v6/rfmp-secure/enrollments";
    private final static String GET_SECURITY_QUESTIONS_FOR_ENROLLMENT_ADAPTER_PATH = "/adapters/suncor/v4/rfmp-secure/enrollments/security-questions";
    private final static String GET_CARD_STATUS_ADAPTER_PATH = "/adapters/suncor/v6/rfmp-secure/enrollments/card-status";
    public final int ENROLLMENT_TIMEOUT = 60_000;
    private Gson gson;

    public EnrollmentsApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<EnrollmentPointsAndHours>> registerAccount(NewEnrollment account) {
        Timber.d("Call enrollments API, account: " + account.getEmail());
        MutableLiveData<Resource<EnrollmentPointsAndHours>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ENROLLMENT_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, ENROLLMENT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader(ACCEPT_LANGUAGE, "fr-CA");
            } else {
                request.addHeader(ACCEPT_LANGUAGE, "en-CA");
            }
            JSONObject body = new JSONObject(gson.toJson(account));
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Enrollments API success");
                    try {
                        EnrollmentPointsAndHours pointsAndHours = new EnrollmentPointsAndHours();
    //                    int rewardedPoints = wlResponse.getResponseJSON().getInt("enrollmentPoints");
                        pointsAndHours.setEnrollmentsPoints( wlResponse.getResponseJSON().getInt("enrollmentPoints"));
                        pointsAndHours.setValidationHours(wlResponse.getResponseJSON().getInt("hoursToValidate"));
                        result.postValue(Resource.success(pointsAndHours));
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
    public LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions() {
        Timber.d("Retrieve security questions");
        MutableLiveData<Resource<ArrayList<SecurityQuestion>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        URI adapterPath;
        try {
            adapterPath = new URI(GET_SECURITY_QUESTIONS_FOR_ENROLLMENT_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
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
            URI adapterPath = new URI(GET_CARD_STATUS_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.DEFAULT_PROTECTED_SCOPE);
            request.addHeader("x-card-number", cardNumber);
            request.addHeader("x-postal-code", postalCode);
            request.addHeader("x-last-name", lastName);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    String response = wlResponse.getResponseText();
                    Timber.d("card status response:" + response);
                    if (wlResponse.getResponseJSON().keys().hasNext()) {
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
