package suncor.com.android.model.carwash

import android.os.Parcel
import android.os.Parcelable
import suncor.com.android.utilities.DateUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

data class ActivateCarwashResponse(
        val resultCode: String,
        val resultSubcode: String,
        val goodThru: String,
        val configurationType: CarwashConfigurationType,
        val estimatedWashesRemaining: Int,
        val usedGrace: Boolean,
        val lastWash: String
): Parcelable {

        fun getDaysLeft(): Int {
                val dateFormat: DateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA)
                return try {
                        val date = dateFormat.parse(goodThru)

                        val todayTimestamp = DateUtils.getTodayTimestamp();
                        val millSeconds = date.time - todayTimestamp
                        val days = Math.ceil((millSeconds.div((1000 * 60f * 60f * 24f)) + 1).toDouble())

                        var estimatedDaysRemaining = Math.max(0, days.toInt())
                        if (lastWash != null && !lastWash.isEmpty()) {
                                val lastWashDate = dateFormat.parse(lastWash)
                                val lastDateCalender  =  Calendar.getInstance();
                                lastDateCalender.timeInMillis = lastWashDate.time;
                                if (estimatedDaysRemaining > 0 && DateUtils.getTodayDate() == lastDateCalender.get(Calendar.DATE)) {
                                         estimatedDaysRemaining -= 1
                                }
                        }
                        return estimatedDaysRemaining
                } catch (e: ParseException) {
                        e.printStackTrace()
                        0
                }
        }


        constructor(parcel: Parcel) : this(
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                CarwashConfigurationType.valueOf(parcel.readString() ?: ""),
                parcel.readInt() ?: 0,
                parcel.readByte() != 0.toByte(),
                parcel.readString() ?: "",
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(resultCode)
                parcel.writeString(resultSubcode)
                parcel.writeString(goodThru)
                parcel.writeString(configurationType.toString())
                parcel.writeInt(estimatedWashesRemaining)
                parcel.writeByte(if (usedGrace) 1 else 0)
                parcel.writeString(lastWash)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<ActivateCarwashResponse> {
                override fun createFromParcel(parcel: Parcel): ActivateCarwashResponse {
                        return ActivateCarwashResponse(parcel)
                }

                override fun newArray(size: Int): Array<ActivateCarwashResponse?> {
                        return arrayOfNulls(size)
                }
        }
}


enum class CarwashConfigurationType {
        TBO, UBO, TUB
}