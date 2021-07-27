package suncor.com.android.data.carwash

import androidx.lifecycle.LiveData
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashRequest
import suncor.com.android.model.carwash.ActivateCarwashResponse

interface CarwashApi {
    fun activateCarwash(activateCarwashRequest: ActivateCarwashRequest): LiveData<Resource<ActivateCarwashResponse>>
}