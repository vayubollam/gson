package suncor.com.android.ui.main.pap.fuelup;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Optional;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.main.pap.fuelup.googlepay.GooglePaymentUtils;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;

public class FuelUpViewModel extends ViewModel {

    private final SettingsApi settingsApi;
    private final PapRepository papRepository;
    private final PaymentsRepository paymentsRepository;


    @Inject
    FuelUpViewModel(SettingsApi settingsApi, PapRepository papRepository, PaymentsRepository paymentsRepository) {
        this.settingsApi = settingsApi;
        this.papRepository = papRepository;
        this.paymentsRepository = paymentsRepository;
    }


    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
    }


    LiveData<Resource<ActiveSession>> getActiveSession() {
        return papRepository.getActiveSession();
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

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see <a href="https://developers.google.com/android/reference/com/google/android/gms/wallet/
     * PaymentsClient.html#isReadyToPay(com.google.android.gms.wallet.
     * IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    public IsReadyToPayRequest getGooglePayRequest() {
        final Optional<JSONObject> isReadyToPayJson = GooglePaymentUtils.getIsReadyToPayRequest();
        return isReadyToPayJson.map(jsonObject -> IsReadyToPayRequest.fromJson(jsonObject.toString())).orElse(null);
    }

    public PaymentDataRequest createGooglePaymentRequest(Double prices, String gateway, String merchantId) {
        Optional<JSONObject> paymentDataRequestJson = GooglePaymentUtils.getPaymentDataRequest(prices,gateway, merchantId );
        if (!paymentDataRequestJson.isPresent()) {
            return null;
        }
        return PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
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
            final String token = tokenizationData.getString("token");

            // Logging token string.
            Log.d("Google Pay token: ", token);
            return token;

        } catch (JSONException e) {
            Log.e(FuelUpViewModel.class.getSimpleName(), "The selected garment cannot be parsed from the list of elements");
        }
        return null;
    }

}
