package suncor.com.android.model.carwash.reload

data class TransactionReceipt(val basePoints: String,
                              val bonusPoints: String,
                              val newBalance: String,
                              val transactionDate: String,
                              val paymentType: String,
val formattedTotal: String)
