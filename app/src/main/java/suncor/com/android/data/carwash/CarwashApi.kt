package suncor.com.android.data.carwash

import androidx.lifecycle.LiveData
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashRequest
import suncor.com.android.model.carwash.ActivateCarwashResponse
import suncor.com.android.model.carwash.reload.TransactionReloadData
import suncor.com.android.model.carwash.reload.TransactionReloadTaxes

interface CarwashApi {
    fun activateCarwash(activateCarwashRequest: ActivateCarwashRequest): LiveData<Resource<ActivateCarwashResponse>>

    fun reloadTransactionCarwash(cardType: String): LiveData<Resource<TransactionReloadData>>

    fun taxCalculationTransactionCarwash(rewardId: String,province: String): LiveData<Resource<TransactionReloadTaxes>>
}