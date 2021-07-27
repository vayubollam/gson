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

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.googlepay.GooglePayUtils;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.pap.PayByGooglePayRequest;
import suncor.com.android.model.pap.PayByWalletRequest;
import suncor.com.android.model.pap.PayResponse;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;
import suncor.com.android.utilities.Timber;

public class CarwashTransactionViewModel extends ViewModel {

    private final SettingsApi settingsApi;
    private final PaymentsRepository paymentsRepository;
    private LatLng userLocation;
    private Profile profile;


    @Inject
    public CarwashTransactionViewModel(SettingsApi settingsApi,
                                       PaymentsRepository paymentsRepository, SessionManager sessionManager) {
        this.settingsApi = settingsApi;
        this.paymentsRepository = paymentsRepository;
        this.profile = sessionManager.getProfile();
    }


    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
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
}