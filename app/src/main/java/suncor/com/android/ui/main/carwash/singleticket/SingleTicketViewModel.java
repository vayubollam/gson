package suncor.com.android.ui.main.carwash.singleticket;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.redeem.OrderApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.model.redeem.request.Order;
import suncor.com.android.model.redeem.request.PetroPoints;
import suncor.com.android.model.redeem.request.RedeemCard;
import suncor.com.android.model.redeem.request.RedeemTransaction;
import suncor.com.android.model.redeem.request.RedeemTransactionAmount;
import suncor.com.android.model.redeem.request.ShoppingCart;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.Event;
import suncor.com.android.utilities.Timber;


public class SingleTicketViewModel extends ViewModel {
    private SessionManager sessionManager;
    private List<PetroCanadaProduct> ticketItems;
    private MutableLiveData<Boolean> isAnyTicketReedeemable = new MutableLiveData<>();
    private PetroCanadaProduct selectedSingleTicketRedeem;
    private boolean isLinkedToAccount = true;

    public LiveData<Resource<OrderResponse>> orderApiData;
    private MutableLiveData<Event<Boolean>> redeem = new MutableLiveData<>();
    private static final String CARD_TYPE = "petropoints";
    public static final int REDEEM_TRANSACTION_QUANTITY = 1;


    @Inject
    public SingleTicketViewModel(SessionManager sessionManager, SingleTicketRedeemReader singleTicketRedeemReader, OrderApi orderApi) {
        PetroCanadaProduct[] singleTicketRedeemsList = singleTicketRedeemReader.getSingleTicketRedeemsList();
        this.sessionManager = sessionManager;
        this.ticketItems = Arrays.asList(singleTicketRedeemsList);
        updateAnyTicketRedeemable();

        orderApiData = Transformations.switchMap(redeem, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                Timber.d("Order APi Redeeming");
                if (selectedSingleTicketRedeem != null) {
                    Order order = buildOrderObject(sessionManager);
                    order.setLinkProductsToAccount(isLinkedToAccount);
                    return orderApi.getRedeemResponse(order);
                }
            }
            return new MutableLiveData<>();
        });

    }

    private Order buildOrderObject(SessionManager sessionManager) {
        return new Order(buildRedeemTransaction(sessionManager), new ShoppingCart(selectedSingleTicketRedeem)
        );
    }

    private RedeemTransaction buildRedeemTransaction(SessionManager sessionManager) {
        String petroPointsNumber = sessionManager.getProfile().getPetroPointsNumber();
        RedeemCard redeemCard = new RedeemCard(CARD_TYPE, petroPointsNumber);
        PetroPoints petroPoints = new PetroPoints(petroPointsNumber, selectedSingleTicketRedeem.getPointsPrice());
        RedeemTransactionAmount redeemTransactionAmount = new RedeemTransactionAmount(REDEEM_TRANSACTION_QUANTITY, petroPoints);
        return new RedeemTransaction(RedeemTransaction.TransactionType.REDEMPTION, redeemTransactionAmount, redeemCard);
    }


    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public List<PetroCanadaProduct> getTicketItems() {
        return ticketItems;
    }

    public MutableLiveData<Boolean> getIsAnyTicketReedeemable() {
        return isAnyTicketReedeemable;
    }

    private void updateAnyTicketRedeemable() {
        boolean isAnyRedeemable = false;
        for (PetroCanadaProduct ticketItem : ticketItems) {
            if (ticketItem.getPointsPrice() <= sessionManager.getProfile().getPointsBalance()) {
                isAnyRedeemable = true;
                break;
            }
        }
        isAnyTicketReedeemable.setValue(isAnyRedeemable);
    }

    public PetroCanadaProduct getSelectedSingleTicketRedeem() {
        return selectedSingleTicketRedeem;
    }

    public void setSelectedSingleTicketRedeem(PetroCanadaProduct selectedSingleTicketRedeem) {
        this.selectedSingleTicketRedeem = selectedSingleTicketRedeem;
    }

    public void sendRedeemData() {
        redeem.postValue(Event.newEvent(true));
    }

    public boolean isLinkedToAccount() {
        return isLinkedToAccount;
    }

    public void setLinkedToAccount(boolean linkedToAccount) {
        isLinkedToAccount = linkedToAccount;
    }
}
