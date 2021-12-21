package suncor.com.android.googlepay;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;

import static suncor.com.android.utilities.Constants.PARAMETERS;

/**
 * Contains helper static methods for dealing with the Payments API.
 *
 * <p>Many of the parameters used in the code are optional and are set here merely to call out their
 * existence. Please consult the documentation to learn more and feel free to remove ones not
 * relevant to your implementation.
 */
public class GooglePayUtils {

    public static final BigDecimal CENTS_IN_A_UNIT = new BigDecimal(100d);

    /**
     * Create a Google Pay API base request object with properties used in all requests.
     *
     * @return Google Pay API base request object.
     * @throws JSONException
     */
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    /**
     * Creates an instance of {@link PaymentsClient} for use in an {@link Activity} using the
     * environment and theme set in {@link GooglePayConstants}.
     *
     * @param activity is the caller's activity.
     */
    public static PaymentsClient createPaymentsClient(Context context) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(GooglePayConstants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(context, walletOptions);
    }

    /**
     * Gateway Integration: Identify your gateway and your app's gateway merchant identifier.
     *
     * <p>The Google Pay API response will return an encrypted payment method capable of being charged
     * by a supported gateway after payer authorization.
     *
     *
     * @return Payment data tokenization for the CARD payment method.
     * @throws JSONException
     * @see <a href=
     * "https://developers.google.com/pay/api/android/reference/object#PaymentMethodTokenizationSpecification">PaymentMethodTokenizationSpecification</a>
     */
    private static JSONObject getGatewayTokenizationSpecification(String gateway, String merchantId) throws JSONException {
        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put(PARAMETERS, new JSONObject() {{
                put("gateway", gateway);
                put("gatewayMerchantId", merchantId);
            }});
        }};
    }


    /**
     * Card networks supported by your app and your gateway.
     *
     * <p>
     *
     * @return Allowed card networks
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(GooglePayConstants.SUPPORTED_NETWORKS);
    }

    /**
     * Card authentication methods supported by your app and your gateway.
     *
     * <p>
     * and make updates in Constants.java.
     *
     * @return Allowed card authentication methods.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardAuthMethodsForIsReadyRequest() {
        return new JSONArray(GooglePayConstants.SUPPORTED_METHODS_CHECK);
    }


    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray(GooglePayConstants.SUPPORTED_METHODS);
    }

    /**
     * Describe your app's support for the CARD payment method.
     *
     * <p>The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest.
     *
     * @return A CARD PaymentMethod object describing accepted cards.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getBaseCardPaymentMethodForIsReadyRequest() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethodsForIsReadyRequest());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put(PARAMETERS, parameters);

        return cardPaymentMethod;
    }

    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        parameters.put("billingAddressRequired", false);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put(PARAMETERS, parameters);

        return cardPaymentMethod;
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return A CARD PaymentMethod describing accepted cards and optional fields.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getCardPaymentMethod(String gateway, String merchantId) throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification(gateway, merchantId));

        return cardPaymentMethod;
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay.
     *
     * @return API version and payment methods supported by the app.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#IsReadyToPayRequest">IsReadyToPayRequest</a>
     */
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethodForIsReadyRequest()));
            return Optional.of(isReadyToPayRequest);

        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status.
     *
     * @return information about the requested payment.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#TransactionInfo">TransactionInfo</a>
     */
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", GooglePayConstants.COUNTRY_CODE);
        transactionInfo.put("currencyCode", GooglePayConstants.CURRENCY_CODE);
        transactionInfo.put("checkoutOption", "COMPLETE_IMMEDIATE_PURCHASE");

        return transactionInfo;
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return Information about the merchant.
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#MerchantInfo">MerchantInfo</a>
     */
    private static JSONObject getMerchantInfo(String merchantName) throws JSONException {
        return new JSONObject().put("merchantName", merchantName);
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return Payment data expected by your app.
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentDataRequest">PaymentDataRequest</a>
     */
    public static Optional<JSONObject> getPaymentDataRequest(double priceCents, String gateway, String merchantId ) {

        final String price = GooglePayUtils.centsToString(priceCents);

        try {
            JSONObject paymentDataRequest = GooglePayUtils.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(GooglePayUtils.getCardPaymentMethod(gateway, merchantId)));
            paymentDataRequest.put("transactionInfo", GooglePayUtils.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", GooglePayUtils.getMerchantInfo(gateway));

      /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
      JSON object. */
      //uncomment only when billing address required
          /*  paymentDataRequest.put("shippingAddressRequired", false);

            JSONObject shippingAddressParameters = new JSONObject();
            shippingAddressParameters.put("phoneNumberRequired", false);

            JSONArray allowedCountryCodes = new JSONArray(GooglePaymentConstants.SHIPPING_SUPPORTED_COUNTRIES);

            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);*/
            return Optional.of(paymentDataRequest);

        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    /**
     * Converts cents to a string format accepted by {@link GooglePayUtils#getPaymentDataRequest}.
     *
     * @param cents value of the price in cents.
     */
    public static String centsToString(Double cents) {
        return new BigDecimal(cents)
                .divide(CENTS_IN_A_UNIT, RoundingMode.HALF_EVEN)
                .setScale(2, RoundingMode.HALF_EVEN)
                .toString();
    }
}
