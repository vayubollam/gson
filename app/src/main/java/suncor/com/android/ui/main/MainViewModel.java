package suncor.com.android.ui.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.redeem.MerchantsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.station.Station;
import suncor.com.android.ui.common.Event;
import suncor.com.android.utilities.Timber;

public class MainViewModel extends ViewModel {
    public MutableLiveData<Event> userLoggedOut = new MutableLiveData<>();
    private boolean isLinkedToAccount = false;
<<<<<<< HEAD
    private String singleTicketNumber;
=======
    private MutableLiveData<Station> nearestStation = new MutableLiveData<>();
>>>>>>> Use mainviewmodel to store staion data.

    @Inject
    public MainViewModel(MerchantsRepository merchantsRepository, SessionManager sessionManager) {
        sessionManager.getLoginState().observeForever((loginState -> {
            if (loginState == SessionManager.LoginState.LOGGED_IN) {
                Timber.d("start retrieving merchants");
                merchantsRepository.getMerchants().observeForever(arrayListResource -> {

                });
            } else if (loginState == SessionManager.LoginState.LOGGED_OUT) {
                userLoggedOut.postValue(Event.newEvent(true));
            }
        }));
    }

    public boolean isLinkedToAccount() {
        return isLinkedToAccount;
    }

    public void setLinkedToAccount(boolean linkedToAccount) {
        isLinkedToAccount = linkedToAccount;
    }

<<<<<<< HEAD
    public String getSingleTicketNumber() {
        return singleTicketNumber;
    }

    public void setSingleTicketNumber(String singleTicketNumber) {
        this.singleTicketNumber = singleTicketNumber;
=======
    public MutableLiveData<Station> getNearestStation() {
        return nearestStation;
    }

    public void setNearestStation(Station nearestStation) {
        this.nearestStation.setValue(nearestStation);
>>>>>>> Use mainviewmodel to store staion data.
    }
}
