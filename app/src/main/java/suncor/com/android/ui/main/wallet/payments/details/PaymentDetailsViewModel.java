package suncor.com.android.ui.main.wallet.payments.details;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.PaymentDetail;

public class PaymentDetailsViewModel extends ViewModel {

    private final PaymentsRepository paymentsRepository;
    private MediatorLiveData<List<PaymentDetail>> _payments = new MediatorLiveData<>();
    LiveData<List<PaymentDetail>> payments = _payments;

    @Inject
    public PaymentDetailsViewModel(PaymentsRepository paymentsRepository) {
        this.paymentsRepository = paymentsRepository;
    }

    void retrieveCards() {
        _payments.addSource(paymentsRepository.getPayments(false), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                _payments.setValue(result.data);
            }
        });
    }

    LiveData<Resource<PaymentDetail>> deletePayment(PaymentDetail paymentDetail) {
        return paymentsRepository.removePayment(paymentDetail);
    }
}
