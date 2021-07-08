package suncor.com.android.ui.main.carwash.reload

import android.content.Context
import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import org.json.JSONException
import org.json.JSONObject
import suncor.com.android.data.pap.PapRepository
import suncor.com.android.data.payments.PaymentsRepository
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.googlepay.GooglePayUtils
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.SettingsResponse
import suncor.com.android.model.account.Profile
import suncor.com.android.model.pap.ActiveSession
import suncor.com.android.model.pap.PayByGooglePayRequest
import suncor.com.android.model.pap.PayByGooglePayRequest.FundingPayload
import suncor.com.android.model.pap.PayByWalletRequest
import suncor.com.android.model.pap.PayResponse
import suncor.com.android.model.payments.PaymentDetail
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem
import suncor.com.android.utilities.Timber
import java.util.*
import javax.inject.Inject


class CarwashReloadTransactionViewModel : ViewModel() {

    private val settingsApi: SettingsApi? = null
    private val papRepository: PapRepository? = null
    private val paymentsRepository: PaymentsRepository? = null
    private var userLocation: LatLng? = null
    private var profile: Profile? = null


    @Inject
    fun CarwashTransactionViewModel(settingsApi: SettingsApi?, papRepository: PapRepository?,
                                    paymentsRepository: PaymentsRepository?, sessionManager: SessionManager) {
        this.settingsApi = settingsApi
        this.papRepository = papRepository
        this.paymentsRepository = paymentsRepository
        profile = sessionManager.profile
    }


    fun getSettingResponse(): LiveData<Resource<SettingsResponse?>?>? {
        return settingsApi!!.retrieveSettings()
    }


    fun getActiveSession(): LiveData<Resource<ActiveSession?>?>? {
        return papRepository!!.activeSession
    }

    fun getPayments(context: Context?): LiveData<Resource<ArrayList<PaymentListItem>?>>? {
        return Transformations.map(paymentsRepository!!.getPayments(true), Function { result: Resource<ArrayList<PaymentDetail?>?> ->
            val payments = ArrayList<PaymentListItem>()
            if (result.data != null) {
                for (paymentDetail in result.data) {
                    payments.add(PaymentListItem(context, paymentDetail))
                }
            }
            Resource<Any?>(result.status, payments, result.message)
        })
    }

    fun setUserLocation(userLocation: LatLng?) {
        this.userLocation = userLocation
    }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button.
     *
     * @see [](https://developers.google.com/android/reference/com/google/android/gms/wallet/
    PaymentsClient.html.isReadyToPay
    ) */
    fun IsReadyToPayRequestForGooglePay(): IsReadyToPayRequest? {
        val isReadyToPayJson = GooglePayUtils.getIsReadyToPayRequest()
        return isReadyToPayJson.map { jsonObject: JSONObject -> IsReadyToPayRequest.fromJson(jsonObject.toString()) }.orElse(null)
    }


    fun createGooglePayInitiationRequest(prices: Double?, gateway: String?, merchantId: String?): PaymentDataRequest? {
        val paymentDataRequestJson = GooglePayUtils.getPaymentDataRequest(prices!!, gateway, merchantId)
        return paymentDataRequestJson.map { jsonObject: JSONObject -> PaymentDataRequest.fromJson(jsonObject.toString()) }.orElse(null)
    }


    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [PaymentData](https://developers.google.com/pay/api/android/reference/
    object.PaymentData)
     */
    fun handlePaymentSuccess(paymentData: PaymentData): String? {
        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        val paymentInfo = paymentData.toJson() ?: return null
        try {
            val paymentMethodData = JSONObject(paymentInfo).getJSONObject("paymentMethodData")
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            val tokenizationData = paymentMethodData.getJSONObject("tokenizationData")
            return tokenizationData.getString("token")
        } catch (e: JSONException) {
            Timber.e(CarwashTransactionViewModel::class.java.getSimpleName(), "Payment data cannot be parsed")
        }
        return null
    }

    /**
     * Payment initiate with google pay
     */
    fun payByGooglePayRequest(storeId: String?, pumpNumber: Int, preAuthAmount: Double, paymentToken: String?): LiveData<Resource<PayResponse?>?>? {
        val request = PayByGooglePayRequest(storeId, pumpNumber, preAuthAmount,
                FundingPayload(paymentToken), profile!!.petroPointsNumber,
                profile!!.isRbcLinked)
        return papRepository!!.authorizePaymentByGooglePay(request, userLocation)
    }

    /**
     * Payment initiate with wallet
     */
    fun payByWalletRequest(storeId: String?, pumpNumber: Int, preAuthAmount: Double, userPaymentSourceId: Int): LiveData<Resource<PayResponse?>?>? {
        val request = PayByWalletRequest(storeId, pumpNumber, preAuthAmount,
                userPaymentSourceId, profile!!.petroPointsNumber, profile!!.isRbcLinked)
        return papRepository!!.authorizePaymentByWallet(request, userLocation)
    }

    fun cancelTransaction(transactionId: String?): LiveData<Resource<Boolean?>?>? {
        return papRepository!!.cancelTransaction(transactionId)
    }


}