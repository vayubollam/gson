package suncor.com.android.model.carwash

data class ActivateCarwashResponse(
        val resultCode: String,
        val resultSubcode: String,
        val lastWash: String,
        val goodThru: String,
        val configurationType: String,
        val estimatedWashesRemaining: Int,
        val usedGrace: Boolean
)