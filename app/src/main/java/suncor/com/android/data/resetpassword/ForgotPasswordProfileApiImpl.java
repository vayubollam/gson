package suncor.com.android.data.resetpassword;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.utilities.Timber;

public class ForgotPasswordProfileApiImpl implements ForgotPasswordProfileApi {
    public ForgotPasswordProfileApiImpl() {
    }

    private final static String SEND_EMAIL_ADAPTER_PATH = "adapters/suncor/v2/profiles/forgot-password";

    @Override
    public LiveData<Resource<Boolean>> generateResetPasswordEmail(String email) {
        Timber.d("Call PROFILE FORGOT PASSWORD API");
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = createURI(SEND_EMAIL_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT);
            request.addHeader("X-Email",email);
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                request.addHeader("Accept-Language", "fr-CA");
            } else {
                request.addHeader("Accept-Language", "en-CA");
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

    private URI createURI(String uri) throws URISyntaxException {
        return new URI(uri);
    }
}
