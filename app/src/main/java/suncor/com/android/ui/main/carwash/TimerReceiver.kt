package suncor.com.android.ui.main.carwash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import suncor.com.android.utilities.Timber


open class TimerReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
     Timber.d("ALARM Recieved")
    }

}
