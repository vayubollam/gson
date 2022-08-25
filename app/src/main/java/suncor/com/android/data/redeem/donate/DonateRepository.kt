package suncor.com.android.data.redeem.donate

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonateRepository @Inject constructor(private val donateApi: DonateApi) {

    fun makeDonateCall(programId: Long, petroPointsId: String, pointsFromDollar: Int) {

    }
}