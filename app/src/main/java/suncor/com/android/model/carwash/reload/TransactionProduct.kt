package suncor.com.android.model.carwash.reload

import java.util.*

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
                            ){
    fun  getBonusValues(): Int {
           val bonus = unitBonuses.stream().filter{ units -> units.getBonusValue() > 0 }.map { units -> units.getBonusValue() }.findFirst();
            return  if(bonus.isPresent)  bonus.get() else 0
       }

    fun getDiscountPrices(): Double? {
        if(onlineReloadDiscount != null && !onlineReloadDiscount.isBlank()){
            if(onlineReloadDiscountType.equals("%")){
              return  price.toDouble() - ((price.toDouble()/100.0f) * onlineReloadDiscount.toDouble())
            } else {
                return  price.toDouble().minus(onlineReloadDiscount.toDouble())
            }
        }
        return null
    }



}
