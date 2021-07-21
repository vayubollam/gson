package suncor.com.android.data.carwash

import androidx.lifecycle.LiveData
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashRequest
import suncor.com.android.model.carwash.ActivateCarwashResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarwashRepository @Inject constructor(private val carwashApi: CarwashApi) {
    fun activateCarwash(request: ActivateCarwashRequest): LiveData<Resource<ActivateCarwashResponse>> {
        return carwashApi.activateCarwash(request)
    }
}