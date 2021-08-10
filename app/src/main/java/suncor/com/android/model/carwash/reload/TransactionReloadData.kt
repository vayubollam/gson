package suncor.com.android.model.carwash.reload

import android.os.Parcel
import android.os.Parcelable
import suncor.com.android.utilities.DateUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class TransactionReloadData(
        val products: List<TransactionProduct>,
        val unitBonuses: List<TransactionUnitBonus>,
        val pointsBonuses: List<TransactionPointBonus>,
        val discounts: List<TransactionDiscount>
) {

        fun getDaysLeft(): Int {
                return 0
        }


}