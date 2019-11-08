package suncor.com.android.ui.main.carwash.singleticket;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.singleticket.SingleTicketRedeem;

public class SingleTicketViewModel extends ViewModel {
    private SessionManager sessionManager;
    private ArrayList<SingleTicketRedeem> ticketItems;
    private MutableLiveData<Boolean> isAnyTicketReedeemable = new MutableLiveData<>();

    @Inject
    public SingleTicketViewModel(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        ticketItems = getFakeData();
        updateAnyTicketRedeemable();
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public ArrayList<SingleTicketRedeem> getTicketItems() {
        return ticketItems;
    }

    private ArrayList<SingleTicketRedeem> getFakeData() {
        ArrayList<SingleTicketRedeem> fakeDataSet = new ArrayList<>();
        fakeDataSet.add(new SingleTicketRedeem("1", 1000, 1));
        fakeDataSet.add(new SingleTicketRedeem("2", 2000, 2));
        fakeDataSet.add(new SingleTicketRedeem("3", 3000, 3));
        fakeDataSet.add(new SingleTicketRedeem("4", 4000, 4));
        fakeDataSet.add(new SingleTicketRedeem("5", 5000, 5));
        fakeDataSet.add(new SingleTicketRedeem("6", 6000, 6));
        fakeDataSet.add(new SingleTicketRedeem("7", 7000, 7));
        fakeDataSet.add(new SingleTicketRedeem("8", 8000, 8));
        fakeDataSet.add(new SingleTicketRedeem("9", 9000, 9));
        return fakeDataSet;
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
