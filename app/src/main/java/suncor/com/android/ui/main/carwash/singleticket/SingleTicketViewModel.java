package suncor.com.android.ui.main.carwash.singleticket;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.mfp.SessionManager;

public class SingleTicketViewModel extends ViewModel {
    private SessionManager sessionManager;
    private ArrayList<MockSingleTicket> ticketItems;
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

    public ArrayList<MockSingleTicket> getTicketItems() {
        return ticketItems;
    }

    private ArrayList<MockSingleTicket> getFakeData() {
        ArrayList<MockSingleTicket> fakeDataSet = new ArrayList<>();
        fakeDataSet.add(new MockSingleTicket("1", 1000, 1));
        fakeDataSet.add(new MockSingleTicket("2", 2000, 2));
        fakeDataSet.add(new MockSingleTicket("3", 3000, 3));
        fakeDataSet.add(new MockSingleTicket("4", 4000, 4));
        fakeDataSet.add(new MockSingleTicket("5", 5000, 5));
        fakeDataSet.add(new MockSingleTicket("6", 6000, 6));
        fakeDataSet.add(new MockSingleTicket("7", 7000, 7));
        fakeDataSet.add(new MockSingleTicket("8", 8000, 8));
        fakeDataSet.add(new MockSingleTicket("9", 9000, 9));
        return fakeDataSet;
    }

    public MutableLiveData<Boolean> getIsAnyTicketReedeemable() {
        return isAnyTicketReedeemable;
    }

    private void updateAnyTicketRedeemable() {
        boolean isAnyRedeemable = false;
        for (MockSingleTicket ticketItem : ticketItems) {
            if (ticketItem.getPetroPointsRequired() <= sessionManager.getProfile().getPointsBalance()) {
                isAnyRedeemable = true;
                break;
            }
        }
        isAnyTicketReedeemable.setValue(isAnyRedeemable);
    }
}
