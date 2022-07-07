package suncor.com.android.extensions

import android.view.Window

fun Window.getSoftInputMode() : Int {
    return attributes.softInputMode
}