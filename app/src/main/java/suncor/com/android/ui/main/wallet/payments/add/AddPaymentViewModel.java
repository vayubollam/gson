package suncor.com.android.ui.main.wallet.payments.add;

import android.net.Uri;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.kount.api.analytics.AnalyticsCollector;

import javax.inject.Inject;

import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.utilities.KountManager;

public class AddPaymentViewModel extends ViewModel {

    private final PaymentsRepository repository;
    private Profile profile;
    public MutableLiveData<Boolean> locationServiceLiveData = new MutableLiveData<>();
    public ObservableBoolean locationServiceEnabled = new ObservableBoolean();
    public ObservableField<String> locationServiceEnableTitle = new ObservableField<>();
    public ObservableField<String> locationServiceEnableMessage = new ObservableField<>();
    private LatLng userLocation;

    String redirectUrl;

    public MutableLiveData<Resource.Status> viewState = new MutableLiveData<>();

    @Inject
    AddPaymentViewModel(PaymentsRepository repository, SessionManager sessionManager) {
        this.repository = repository;
        this.profile = sessionManager.getProfile();
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    LiveData<Resource<Uri>> getAddPaymentEndpoint(boolean inTransaction, String kountSessionId) {
        return Transformations.switchMap(repository.addPayment(), result -> {
            viewState.setValue(result.status);

            redirectUrl = result.data != null ? result.data.getRedirectUrl() : null;

            MutableLiveData<Resource<Uri>> data = new MutableLiveData<>();
            data.setValue(new Resource<>(result.status, result.data != null ?
                    result.data.getP97Url()
                            .buildUpon()
                            .appendQueryParameter("lat", Double.toString(userLocation.latitude))
                            .appendQueryParameter("lon", Double.toString(userLocation.longitude))
                            .appendQueryParameter("isWallet", inTransaction ? "N" : "Y")
                            .appendQueryParameter("streetAddress", profile.getStreetAddress())
                            .appendQueryParameter("city", profile.getCity())
                            .appendQueryParameter("province", profile.getProvince())
                            .appendQueryParameter("zipCode", profile.getPostalCode())
                            .appendQueryParameter("kountSessionId", kountSessionId)
                            .build()
                    : null, result.message));
            return data;
        });
    }

    public void setLocationServiceEnabled(boolean enabled) {
        locationServiceEnabled.set(enabled);
        locationServiceLiveData.setValue(enabled);
    }

    public void setLocationServiceTitle(String title) {
        locationServiceEnableTitle.set(title);
    }

    public void setLocationServiceMessage(String message) {
        locationServiceEnableMessage.set(message);
    }
}
