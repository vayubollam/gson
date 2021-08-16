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
                            ){

    fun  getBonusValues(): Int {
         if(!unitBonuses.isEmpty() && unitBonuses.size > 0){
             return unitBonuses[0].getBonusValue();
         }
           return 0
       }

    fun getDiscountPrices(): Double? {
        if(autoReloadDiscount != null && !autoReloadDiscount.isBlank()){
            if(autoReloadDiscountType.equals("%")){
              return  price.toDouble() - ((price.toDouble()/100.0f) * autoReloadDiscount.toDouble())
            }
        }
        return null
    }



}
