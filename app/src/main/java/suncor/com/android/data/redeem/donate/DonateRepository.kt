package suncor.com.android.data.redeem.donate

import androidx.lifecycle.MutableLiveData
import suncor.com.android.model.Resource
import suncor.com.android.model.redeem.request.DonateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonateRepository @Inject constructor(private val donateApi: DonateApi) {

    fun makeDonateCall(
        programId: Long,
        petroPointsId: String,
        pointsToRedeem: Int
    ): MutableLiveData<Resource<Unit>> {
        val request = DonateRequest(programId, petroPointsId, pointsToRedeem)
        return donateApi.donatePoints(request)
    }
}