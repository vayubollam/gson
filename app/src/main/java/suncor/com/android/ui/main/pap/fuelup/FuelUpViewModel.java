package suncor.com.android.ui.main.pap.fuelup;

import android.content.Context;
import android.se.omapi.Session;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import suncor.com.android.LocationLiveData;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.transaction.Transaction;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.utilities.Timber;
import suncor.com.android.utilities.UserLocalSettings;

public class FuelUpViewModel extends ViewModel {

    private final SettingsApi settingsApi;
    private final PapRepository papRepository;
    private final PaymentsRepository paymentsRepository;
    private LatLng userLocation;
    private Profile profile;
    private SessionManager sessionManager;


    @Inject
    public FuelUpViewModel(SettingsApi settingsApi, PapRepository papRepository,
                           PaymentsRepository paymentsRepository, SessionManager sessionManager) {
        this.settingsApi = settingsApi;
        this.papRepository = papRepository;
        this.paymentsRepository = paymentsRepository;
        this.profile = sessionManager.getProfile();
        this.sessionManager = sessionManager;
    }


    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
    }


    public LiveData<Resource<ActiveSession>> getActiveSession() {
        return papRepository.getActiveSession();
    }

    public void saveLastStatusUpdate(ActiveSession activeSession) {
        if (activeSession.lastStatus != null &&
                activeSession.lastStatus.equals("Finished")
                && sessionManager.getUserLocalSettings().getLong(UserLocalSettings.LAST_SUCCESSFUL_PAP_DATE)
                != activeSession.getLastStatusUpdated()) {
            sessionManager.getUserLocalSettings().setLong(UserLocalSettings.LAST_SUCCESSFUL_PAP_DATE,
                    activeSession.getLastStatusUpdated());
        }
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
            Timber.e(FuelUpViewModel.class.getSimpleName(), "Payment data cannot be parsed");
        }
        return null;
    }

    /**
     * Payment initiate with google pay
     */
    LiveData<Resource<PayResponse>> payByGooglePayRequest(String storeId, int pumpNumber, double preAuthAmount, String paymentToken) {
        PayByGooglePayRequest request = new PayByGooglePayRequest(storeId, pumpNumber,preAuthAmount,
                new PayByGooglePayRequest.FundingPayload(paymentToken), profile.getPetroPointsNumber(),
                profile.isRbcLinked());
        return papRepository.authorizePaymentByGooglePay(request, userLocation);
    }

    /**
     * Payment initiate with wallet
     */
    LiveData<Resource<PayResponse>> payByWalletRequest(String storeId, int pumpNumber, double preAuthAmount, int userPaymentSourceId) {
        PayByWalletRequest request = new PayByWalletRequest(storeId, pumpNumber, preAuthAmount,
                userPaymentSourceId, profile.getPetroPointsNumber(), profile.isRbcLinked());
        return papRepository.authorizePaymentByWallet(request, userLocation);
    }

    public LiveData<Resource<Boolean>> cancelTransaction(String transactionId) {
        return papRepository.cancelTransaction(transactionId);
    }

    public String getPetroPointsBalance() {
        if (sessionManager.getProfile() != null) {
            return CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance());
        }
        return "";
    }

}
