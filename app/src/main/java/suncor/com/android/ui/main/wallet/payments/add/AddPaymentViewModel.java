package suncor.com.android.ui.main.wallet.payments.add;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.model.Resource;

public class AddPaymentViewModel extends ViewModel {

    private final PaymentsRepository repository;

    String redirectUrl;

    @Inject
    AddPaymentViewModel(PaymentsRepository repository) {
        this.repository = repository;
    }

    LiveData<Resource<Uri>> getAddPaymentEndpoint() {
        return Transformations.switchMap(repository.addPayment(), result -> {
            redirectUrl = result.data != null ? result.data.getRedirectUrl() : null;

            MutableLiveData<Resource<Uri>> data = new MutableLiveData<>();
            data.setValue(new Resource<>(result.status, result.data != null ? result.data.getP97Url() : null, result.message));
            return data;
        });
    }
}
