package suncor.com.android.model.carwash

data class ActivateCarwashRequest(
        val encryptedStoreId: String,
        val posPin: String,
        val carWashCardNumber: String
)