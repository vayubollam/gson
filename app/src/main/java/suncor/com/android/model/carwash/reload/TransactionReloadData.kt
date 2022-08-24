package suncor.com.android.model.carwash.reload

data class TransactionReloadData(
        val products: List<TransactionProduct>,
        val discounts: List<TransactionDiscount>
) {

        fun getDaysLeft(): Int {
                return 0
        }

        fun getDefaultSelectProduct(cardType: String): TransactionProduct{
                if((cardType == "SP")) {
                        return products.single { it.units == "90" }
                }
                return products.single { it.units == "5" }
        }



}