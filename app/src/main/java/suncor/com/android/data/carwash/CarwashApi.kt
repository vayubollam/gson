package suncor.com.android.data.carwash

import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashRequest
import suncor.com.android.model.carwash.ActivateCarwashResponse
import suncor.com.android.model.carwash.PayByGooglePayRequest
import suncor.com.android.model.carwash.PayByWalletRequest
import suncor.com.android.model.carwash.reload.TransactionReloadData
import suncor.com.android.model.carwash.reload.TransactionReloadTaxes
import suncor.com.android.model.pap.PayResponse

interface CarwashApi {
    fun activateCarwash(activateCarwashRequest: ActivateCarwashRequest): LiveData<Resource<ActivateCarwashResponse>>

    fun reloadTransactionCarwash(cardType: String): LiveData<Resource<TransactionReloadData>>

    fun taxCalculationTransactionCarwash(rewardId: String,province: String): LiveData<Resource<TransactionReloadTaxes>>

    fun authorizePaymentByWallet(payByWalletRequest: PayByWalletRequest, userLocation: LatLng): LiveData<Resource<PayResponse>>

    fun authorizePaymentByGooglePay(payByWalletRequest: PayByGooglePayRequest, userLocation: LatLng): LiveData<Resource<PayResponse>>
}