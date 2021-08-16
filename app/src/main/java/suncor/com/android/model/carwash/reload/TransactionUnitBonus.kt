package suncor.com.android.model.carwash.reload

import suncor.com.android.utilities.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class TransactionUnitBonus(
    val provinces: String, val bonusUnits: String, val applyToAutoReloads: Boolean,
    val endDate: String, val cardSubType: String, val cardType: String, val name: String,
    val message: String, val sKU: String , val startDate: String, val applyToOnlineReloads: Boolean){

    fun getBonusValue(): Int {
        val dateFormat: DateFormat =
            SimpleDateFormat("yyyy-MM-dd", Locale.CANADA)
            val startFormatedDate = dateFormat.parse(startDate)
            val endFormatedDate = dateFormat.parse(endDate)

            val todayDateTimestamp = DateUtils.getTodayTimestamp();

           if(startFormatedDate.time <= todayDateTimestamp && todayDateTimestamp <= endFormatedDate.time){
               return bonusUnits.toInt()
           }
        return 0
    }
}
