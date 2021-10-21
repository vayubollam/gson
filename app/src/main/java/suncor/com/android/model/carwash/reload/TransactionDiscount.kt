package suncor.com.android.model.carwash.reload

data class TransactionDiscount(val cardType: String,
                               val cardSubType: String,
                               val applyToOnlineReloads: String,
                               val sKU: String)
