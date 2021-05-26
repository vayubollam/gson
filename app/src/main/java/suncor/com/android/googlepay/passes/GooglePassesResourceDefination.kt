package suncor.com.android.googleapis.passes

import com.google.api.services.walletobjects.model.*
import java.util.*

object GooglePassesResourceDefination {
    /******************************
     *
     * Define a Loyalty Object
     *
     * See https://developers.google.com/pay/passes/reference/v1/loyaltyobject
     *
     * @param String classId - The unique identifier for a class
     * @param String objectId - The unique identifier for an object
     * @return LoyaltyObject payload - represents Loyalty object resource
     */
    fun makeLoyaltyObjectResource(classId: String?, objectId: String?, barcodeValue: String?, description: String?): LoyaltyObject {
        // Define the resource representation of the Class
        // values should be from your DB/services; here we hardcode information
        // below defines an loyalty class. For more properties, check:
        //// https://developers.google.com/pay/passes/reference/v1/loyaltyobject/insert
        //// https://developers.google.com/pay/passes/guides/pass-verticals/loyalty/design

        // There is a client lib to help make the data structure. Newest client is on
        // devsite:
        //// https://developers.google.com/pay/passes/support/libraries#libraries
        return LoyaltyObject()
                .setId(objectId).setClassId(classId).setState("active")
                .setAccountId(barcodeValue)
                .setBarcode(Barcode().setType("pdf417").setValue(barcodeValue))
                .setLoyaltyPoints(LoyaltyPoints().setBalance(LoyaltyPointsBalance().setString("105,154 " +
                        "\n\n DOLLARS OFF  \n  $105 ")))
                .setInfoModuleData(InfoModuleData().setLabelValueRows(object : ArrayList<LabelValueRow?>() {
                    init {
                        add(LabelValueRow().setColumns(object : ArrayList<LabelValue?>() {
                            init {
                                add(LabelValue().setLabel("NAME").setValue("Asma"))
                                add(LabelValue().setLabel("EMAIL").setValue("developer@suncor.com"))
                                add(LabelValue().setLabel("DETAILS").setValue("Redeem your Petro-Points for free gas, eGift" +
                                        ", cards and more."))
                                add(LabelValue().setLabel("VALUE").setValue("Earn: \n 1L = 10 points \n $1 = 10 points \n\n Redeem: \n 1000 = \$1 off\""))
                                add(LabelValue().setLabel("How to use").setValue("Scan your card in store to earn or redeem points. Or, download our app to earn points directly at the pump."))
                                add(LabelValue().setLabel("TERMS AND CONDITIONS").setValue("By using this pass, you agree to the Petro Points terms and conditions. Read them on our website."))
                            }
                        }))
                    }
                })
                )
    }
}