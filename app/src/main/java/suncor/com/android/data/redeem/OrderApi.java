package suncor.com.android.data.redeem;

import androidx.lifecycle.LiveData;

import suncor.com.android.model.redeem.request.Order;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.model.Resource;

public interface OrderApi {
    LiveData<Resource<OrderResponse>> getRedeemResponse(Order order);
}
