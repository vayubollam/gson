package suncor.com.android.model.carwash.reload

import java.math.RoundingMode


data class TransactionReloadTaxes(
        val pst: Double,
        val qst: Double,
        val hst: Double,
        val gst: Double

) {

        fun getTotalTax(reloadTotalAmount: Double): Double {
                if(reloadTotalAmount.equals(0.00)){
                       return 0.00;
                }
                val totalTaxPercentage = (pst + qst + hst + gst)*100
                return ((reloadTotalAmount * totalTaxPercentage)/100).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toDouble()
        }


}