package suncor.com.android.data.payments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.AddPayment;
import suncor.com.android.model.payments.PaymentDetail;

@Singleton
public class PaymentsRepository {

    private PaymentsApi paymentsApi;
    private ArrayList<PaymentDetail> cachedPayments;
    private Calendar timeOfLastUpdate;

    @Inject
    public PaymentsRepository(PaymentsApi paymentsApi, SessionManager sessionManager) {
        this.paymentsApi = paymentsApi;
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedPayments != null) {
                cachedPayments.clear();
            }
        });
    }

    public LiveData<Resource<ArrayList<PaymentDetail>>> getPayments(boolean forceRefresh) {
        MediatorLiveData<Resource<ArrayList<PaymentDetail>>> result = new MediatorLiveData<>();
        if (!forceRefresh && cachedPayments != null && !cachedPayments.isEmpty()) {
            result.postValue(Resource.success(cachedPayments));
            return result;
        }
        return Transformations.map(paymentsApi.retrievePayments(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                if (cachedPayments == null || cachedPayments.size() == 0) {
                    cachedPayments = resource.data;
                    timeOfLastUpdate = Calendar.getInstance();
                    return resource;
                }

                //clearing old cards
                for (int i = cachedPayments.size() - 1; i >= 0; i--) {
                    if (!findCardIn(resource.data, cachedPayments.get(i))) {
                        cachedPayments.remove(i);
                    }
                }

                timeOfLastUpdate = Calendar.getInstance();
                return Resource.success(cachedPayments);
            } else if (resource.status == Resource.Status.ERROR) {
                if (cachedPayments != null) {
                    cachedPayments.clear();
                }
                return resource;
            } else {
                return resource;
            }
        });
    }

    public LiveData<Resource<AddPayment>> addPayment() {
        return paymentsApi.addPayment();
    }

    public LiveData<Resource<PaymentDetail>> removePayment(PaymentDetail paymentDetail) {
        return Transformations.map(paymentsApi.removePayment(paymentDetail), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                for (int i = cachedPayments.size() - 1; i >= 0; i--) {
                    if (!findCardIn(result.data, cachedPayments.get(i))) {
                        cachedPayments.remove(i);
                    }
                }

                timeOfLastUpdate = Calendar.getInstance();
                return Resource.success(paymentDetail);
            }

            return new Resource<>(result.status, paymentDetail, result.message);
        });
    }

    public Calendar getTimeOfLastUpdate() {
        return timeOfLastUpdate;
    }

    private static boolean findCardIn(ArrayList<PaymentDetail> payments, PaymentDetail otherPayment) {
        for (PaymentDetail payment : payments) {
            if (payment.equals(otherPayment)) {
                return true;
            }
        }

        return false;
    }
}
