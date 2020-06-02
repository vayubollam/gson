package suncor.com.android.data.payments;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.PaymentDetail;

public interface PaymentsApi {
    LiveData<Resource<ArrayList<PaymentDetail>>> retrievePayments();

    LiveData<Resource<PaymentDetail>> addPayment();

    LiveData<Resource<PaymentDetail>> removePayment(PaymentDetail paymentDetail);
}
