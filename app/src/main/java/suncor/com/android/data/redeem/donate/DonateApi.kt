package suncor.com.android.data.redeem.donate

import androidx.lifecycle.MutableLiveData
import suncor.com.android.model.Resource
import suncor.com.android.model.redeem.request.DonateRequest

interface DonateApi {
    fun donatePoints(donateRequest: DonateRequest): MutableLiveData<Resource<Unit>>
}