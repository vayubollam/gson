package suncor.com.android.model.carwash

import java.util.*


data class ActivateCarwashRequest @JvmOverloads constructor (
        val encryptedStoreId: String,
        val posPin: String,
        val carWashCardNumber: String,
        val languageOverride: String = if (Locale.getDefault().language.equals("fr", ignoreCase = true)) "French" else "English"
)