package suncor.com.android.data.redeem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResourceRequest;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

import suncor.com.android.SuncorApplication;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.redeem.request.Order;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.utilities.Timber;

public class OrderApiImpl implements OrderApi {
    private final static String REDEEM_POINTS_ORDER_ADAPTER_PATH = "/adapters/suncorreloadredeem/v2/rfmp-secure/orders";
    private Gson gson;

    public OrderApiImpl(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<OrderResponse>> getRedeemResponse(Order order) {
        Timber.d("Call ORDER API");
        MutableLiveData<Resource<OrderResponse>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        try {
            URI adapterPath = createURI(REDEEM_POINTS_ORDER_ADAPTER_PATH);
            WLResourceRequest request = new WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE);
            JSONObject body = new JSONObject(gson.toJson(order));
            if(order.getShoppingCart().geteGift() == null) request.addHeader("X-Mock-Variant", "/v1/orders:singleticket:success");
            request.send(body, new WLResponseListener() {
                @Override
                public void onSuccess(WLResponse wlResponse) {
                    Timber.d("Order API success");
                    String response = wlResponse.getResponseText();
                    Timber.d("Order API success, response:\n" + response);

                    OrderResponse orderResponse = gson.fromJson(response, OrderResponse.class);
                    result.postValue(Resource.success(orderResponse));
                }

                @Override
                public void onFailure(WLFailResponse wlFailResponse) {
                    Timber.d("Order API failed, " + wlFailResponse.toString());
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
