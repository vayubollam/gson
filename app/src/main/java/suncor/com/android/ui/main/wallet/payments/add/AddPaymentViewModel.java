package suncor.com.android.ui.main.wallet.payments.add;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;

public class AddPaymentViewModel extends ViewModel {

    private final PaymentsRepository repository;
    private Profile profile;

    String redirectUrl;

    public MutableLiveData<Resource.Status> viewState = new MutableLiveData<>();

    @Inject
    AddPaymentViewModel(PaymentsRepository repository, SessionManager sessionManager) {
        this.repository = repository;
        this.profile = sessionManager.getProfile();
    }

    LiveData<Resource<Uri>> getAddPaymentEndpoint(boolean inTransaction) {
        return Transformations.switchMap(repository.addPayment(), result -> {
            viewState.setValue(result.status);

            redirectUrl = result.data != null ? result.data.getRedirectUrl() : null;

            MutableLiveData<Resource<Uri>> data = new MutableLiveData<>();
            data.setValue(new Resource<>(result.status, result.data != null ?
                    result.data.getP97Url()
                            .buildUpon()
                            .appendQueryParameter("isWallet", inTransaction ? "N" : "Y")
                            .appendQueryParameter("streetAddress", profile.getStreetAddress())
                            .appendQueryParameter("city", profile.getCity())
                            .appendQueryParameter("province", profile.getProvince())
                            .appendQueryParameter("zipCode", profile.getPostalCode())
                            .build()
                    : null, result.message));
            return data;
        });
    }
}
