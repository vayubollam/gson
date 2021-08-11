package suncor.com.android.model.carwash.reload

data class TransactionProduct(
                               val applyToOnlineReloads: String,
                               val sKU: String,
                               val pointsPrice: String,
                               val autoReloadDiscount: String,val cardSubType: String,
                               val cardType: String,val materialCode: String,
                               val units: String , val shortTitle: String,
                               val title: String, val onlineReloadDiscount: String,
                               val onlineReloadDiscountType: String,
                               val rewardId: String, val price: String, val autoReloadDiscountType: String,
                               val unitBonuses: List<TransactionUnitBonus>,
                               val pointsBonuses: List<TransactionPointBonus>,
                            )
