package suncor.com.android.ui.main.carwash.singleticket;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.singleticket.SingleTicketRedeem;

public class SingleTicketViewModel extends ViewModel {
    private SessionManager sessionManager;
    private List<SingleTicketRedeem> ticketItems;
    private MutableLiveData<Boolean> isAnyTicketReedeemable = new MutableLiveData<>();

    @Inject
    public SingleTicketViewModel(SessionManager sessionManager, SingleTicketRedeemReader singleTicketRedeemReader) {
        SingleTicketRedeem[] singleTicketRedeemsList = singleTicketRedeemReader.getSingleTicketRedeemsList();
        this.sessionManager = sessionManager;
        this.ticketItems = Arrays.asList(singleTicketRedeemsList);
        updateAnyTicketRedeemable();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public List<SingleTicketRedeem> getTicketItems() {
        return ticketItems;
    }

    public MutableLiveData<Boolean> getIsAnyTicketReedeemable() {
        return isAnyTicketReedeemable;
    }

    private void updateAnyTicketRedeemable() {
        boolean isAnyRedeemable = false;
        for (SingleTicketRedeem ticketItem : ticketItems) {
            if (ticketItem.getPetroPointsRequired() <= sessionManager.getProfile().getPointsBalance()) {
                isAnyRedeemable = true;
                break;
            }
        }
        isAnyTicketReedeemable.setValue(isAnyRedeemable);
    }
}
