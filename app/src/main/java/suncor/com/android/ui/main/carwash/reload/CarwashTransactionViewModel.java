package suncor.com.android.ui.main.carwash.reload;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.data.carwash.CarwashApi;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.carwash.reload.TransactionProduct;
import suncor.com.android.model.carwash.reload.TransactionReloadData;
import suncor.com.android.model.carwash.reload.TransactionReloadTaxes;
import suncor.com.android.model.carwash.PayByWalletRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.utilities.Timber;

public class CarwashTransactionViewModel extends ViewModel {

    private final CarwashApi carwashApi;
    private final SettingsApi settingsApi;
    private final PaymentsRepository paymentsRepository;
    private final CardsRepository cardsRepository;
    private LatLng userLocation;
    private final Profile profile;
    public String cardNumber;
    public String cardName;
    private TransactionReloadData transactionReloadData;
    private TransactionProduct selectedProduct;
    private TransactionReloadTaxes transactionReloadTax;
    private Double selectedValuesAmount = 0.0;
    private String lastSelectedValue;
    private Double totalAmount;


    @Inject
    public CarwashTransactionViewModel(CarwashApi carwashApi, SettingsApi settingsApi,
                                       PaymentsRepository paymentsRepository, CardsRepository cardsRepository,
                                       SessionManager sessionManager) {
        this.carwashApi = carwashApi;
        this.settingsApi = settingsApi;
        this.paymentsRepository = paymentsRepository;
        this.cardsRepository = cardsRepository;
        this.profile = sessionManager.getProfile();
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public TransactionReloadData getTransactionReloadData() {
        return transactionReloadData;
    }

    public void setTransactionReloadData(TransactionReloadData transactionReloadData) {
        this.transactionReloadData = transactionReloadData;
    }

    public TransactionProduct getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(TransactionProduct selectedProduct) {
        this.selectedProduct = selectedProduct;
    }

    public TransactionReloadTaxes getTransactionReloadTax() {
        return transactionReloadTax;
    }

    public void setTransactionReloadTax(TransactionReloadTaxes transactionReloadTax) {
        this.transactionReloadTax = transactionReloadTax;
    }

    public Double getSelectedValuesAmount() {
        return selectedValuesAmount;
    }

    public void setSelectedValuesAmount(Double selectedValuesAmount) {
        this.selectedValuesAmount = selectedValuesAmount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return profile.getFirstName();
    }

    LiveData<Resource<TransactionReloadData>> getTransactionData(String cardType) {
        return carwashApi.reloadTransactionCarwash(cardType);
    }

    LiveData<Resource<TransactionReloadTaxes>> fetchTaxValues(String rewardId , String provience) {
        return carwashApi.taxCalculationTransactionCarwash(rewardId, provience);
    }

    LiveData<Resource<ArrayList<PaymentListItem>>> getPayments(Context context) {
        return Transformations.map(paymentsRepository.getPayments(true), result -> {
            ArrayList<PaymentListItem> payments = new ArrayList<>();

            if (result.data != null) {
                for (PaymentDetail paymentDetail : result.data) {
                    payments.add(new PaymentListItem(context, paymentDetail));
                }
            }

            return new Resource(result.status, payments, result.message);
        });
    }

    public void setUserLocation(LatLng userLocation) {
        this.userLocation = userLocation;
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.
     * IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    public IsReadyToPayRequest IsReadyToPayRequestForGooglePay() {
        final Optional<JSONObject> isReadyToPayJson = GooglePayUtils.getIsReadyToPayRequest();
        return isReadyToPayJson.map(jsonObject -> IsReadyToPayRequest.fromJson(jsonObject.toString())).orElse(null);
    }


    public PaymentDataRequest createGooglePayInitiationRequest(Double prices, String gateway, String merchantId) {
        Optional<JSONObject> paymentDataRequestJson = GooglePayUtils.getPaymentDataRequest(prices,gateway, merchantId );
        return paymentDataRequestJson.map(jsonObject -> PaymentDataRequest.fromJson(jsonObject.toString())).orElse(null);
    }


    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see <a href="https://developers.google.com/pay/api/android/reference/
     * object#PaymentData">PaymentData</a>
     */
    public String handlePaymentSuccess(PaymentData paymentData) {
        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return null;
        }
        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            return tokenizationData.getString("token");

        } catch (JSONException e) {
            Timber.e(CarwashTransactionViewModel.class.getSimpleName(), "Payment data cannot be parsed");
        }
        return null;
    }

    public String getPetroPointsBalance() {
        return CardFormatUtils.formatBalance(profile.getPointsBalance());
    }

    public String getPetroPointsNumber() {
        return profile.getPetroPointsNumber();
    }

    public String getUserProvince() {
        return profile.getProvince();
    }

    public LiveData<Resource<SettingsResponse>> getSettings(){
        return settingsApi.retrieveSettings();
    }

    public LiveData<Resource<ArrayList<CardDetail>>> getCards(){
       return cardsRepository.getCards(false);
    }

    /**
     * Payment initiate with wallet
     */
    LiveData<Resource<PayResponse>> payByWalletRequest(String cardType,double totalAmount, String kountSessionId, int userPaymentSourceId) {
        PayByWalletRequest request = new PayByWalletRequest(cardType, cardNumber.replace(" ", ""), selectedProduct.getBonusValues(),
                selectedProduct.getSKU(), selectedProduct.getMaterialCode(), getUserProvince(), selectedValuesAmount,transactionReloadTax.getPst(),
                transactionReloadTax.getGst(), transactionReloadTax.getQst(), transactionReloadTax.getHst(), totalAmount, profile.getPetroPointsNumber(), profile.getPointsBalance(),
                selectedProduct.getBonusValues(),"moneris",userPaymentSourceId,
                kountSessionId);
        return carwashApi.authorizePaymentByWallet(request, userLocation);
    }

    public LiveData<Resource<ArrayList<CardDetail>>>  refreshCards(){
       return cardsRepository.getCards(true);
    }

}
