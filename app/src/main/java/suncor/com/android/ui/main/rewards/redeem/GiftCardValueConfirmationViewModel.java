package suncor.com.android.ui.main.rewards.redeem;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.redeem.OrderApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.EGift;
import suncor.com.android.model.redeem.request.Order;
import suncor.com.android.model.redeem.request.PetroPoints;
import suncor.com.android.model.redeem.request.RedeemCard;
import suncor.com.android.model.redeem.request.RedeemTransaction;
import suncor.com.android.model.redeem.request.RedeemTransactionAmount;
import suncor.com.android.model.redeem.request.ShoppingCart;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.Event;
import suncor.com.android.utilities.Timber;

public class GiftCardValueConfirmationViewModel extends ViewModel {
    public static final int REDEEM_TRANSACTION_QUANTITY = 1;
    private GenericEGiftCard genericCardItem;
    private SessionManager sessionManager;
    private MutableLiveData<Event<Boolean>> redeem = new MutableLiveData<>();
    private static final String CARD_TYPE = "petropoints";

    public LiveData<Resource<OrderResponse>> orderApiData;
    private EGift eGift;

    @Inject
    public GiftCardValueConfirmationViewModel(OrderApi orderApi, SessionManager sessionManager) {
        this.sessionManager = sessionManager;

        orderApiData = Transformations.switchMap(redeem, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                Timber.d("Order APi Redeeming");
                if (getEGift() != null) {
                    Order order = buildOrderObject(sessionManager);
                    return orderApi.getRedeemResponse(order);
                }
            }
            return new MutableLiveData<>();
        });
    }

    private Order buildOrderObject(SessionManager sessionManager) {
        return new Order(buildRedeemTransaction(sessionManager), new ShoppingCart(getEGift())
        );
    }

    private RedeemTransaction buildRedeemTransaction(SessionManager sessionManager) {
        String petroPointsNumber = sessionManager.getProfile().getPetroPointsNumber();
        RedeemCard redeemCard = new RedeemCard(CARD_TYPE, petroPointsNumber);
        PetroPoints petroPoints = new PetroPoints(petroPointsNumber, eGift.getPetroPointsRequired());
        RedeemTransactionAmount redeemTransactionAmount = new RedeemTransactionAmount(REDEEM_TRANSACTION_QUANTITY, petroPoints);
        return new RedeemTransaction(RedeemTransaction.TransactionType.REDEMPTION, redeemTransactionAmount, redeemCard);
    }

    public GenericEGiftCard getGiftCardItem() {
        return genericCardItem;
    }

    public void setGenericCardItem(GenericEGiftCard genericCardItem) {
        this.genericCardItem = genericCardItem;
    }

    public EGift getEGift() {
        return eGift;
    }

    public void setEGift(EGift eGift) {
        this.eGift = eGift;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void sendRedeemData() {
        redeem.postValue(Event.newEvent(true));
    }
}