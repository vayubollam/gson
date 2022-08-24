package suncor.com.android.model.carwash

data class PayByWalletRequest @JvmOverloads constructor (
        val cardType: String,
        val carwashCardNumber: String,
        val addUnitsDays: Int,
        val sku: String ,
        val materialCode: String,
        val province: String,
        val salePrice: Double,
        val pstTaxAmount: Double,
        val gstTaxAmount: Double,
        val qstTaxAmount: Double,
        val hstTaxAmount: Double,
        val saleTotalAmount: Double,
        val petroPointsNumber: String,
        val petroPointsBalance: Double,
        val bonusPoints: Double,
        val paymentProviderName: String = "moneris",
        val userPaymentSourceId: Int,
        val kountSessionId: String
)
