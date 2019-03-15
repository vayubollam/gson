package suncor.com.android.data.repository.account;

import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

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
        MutableLiveData<Resource<EmailState>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = new URI(ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.GET);
            request.addHeader("x-email", email);
            request.send(new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    result.postValue(Resource.success(EmailState.VALID));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    if (ErrorCodes.EXISTING_EMAIL.equals(wlFailResponse.getErrorMsg())) {
                        result.postValue(Resource.success(EmailState.INVALID));
                    } else {
                        result.postValue(Resource.error(wlFailResponse.getErrorMsg()));
                    }
                }
            });
        } catch (URISyntaxException e) {
            result.postValue(Resource.error(e.getMessage()));
        }

        return result;
    }
}
