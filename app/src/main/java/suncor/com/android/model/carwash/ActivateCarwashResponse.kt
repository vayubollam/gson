package suncor.com.android.model.carwash

import android.os.Parcel
import android.os.Parcelable
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class ActivateCarwashResponse(
        val resultCode: String,
        val resultSubcode: String,
        val goodThru: String,
        val configurationType: CarwashConfigurationType,
        val estimatedWashesRemaining: Int,
        val usedGrace: Boolean
): Parcelable {

        fun getDaysLeft(): Int {
                val dateFormat: DateFormat =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.CANADA)
                return try {
                        val date = dateFormat.parse(goodThru)
                        val today = Calendar.getInstance()
                        today.set(Calendar.HOUR_OF_DAY, 0)
                        today.set(Calendar.MINUTE, 0)
                        today.set(Calendar.SECOND, 0)
                        today.set(Calendar.MILLISECOND, 0)

                        val millionSeconds = date.time - today.timeInMillis
                        return java.util.concurrent.TimeUnit.MILLISECONDS.toDays(millionSeconds).toInt() + 1 // Count the end date
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
                parcel.readByte() != 0.toByte()
        ) {
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(resultCode)
                parcel.writeString(resultSubcode)
                parcel.writeString(goodThru)
                parcel.writeString(configurationType.toString())
                parcel.writeInt(estimatedWashesRemaining)
                parcel.writeByte(if (usedGrace) 1 else 0)
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