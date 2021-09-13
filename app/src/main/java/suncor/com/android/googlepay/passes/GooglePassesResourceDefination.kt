package suncor.com.android.googleapis.passes

import com.google.api.services.walletobjects.model.*
import suncor.com.android.googlepay.passes.LoyalityData
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
    fun makeLoyaltyObjectResource(classId: String?, objectId: String?, loyalityData: LoyalityData): LoyaltyObject {
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
               // .setAccountId(loyalityData.barcodeDisplay)
                .setBarcode(Barcode().setType(loyalityData.barcodeType).setValue(loyalityData.barcodeDisplay))
                //todo uncomment for update the balance
              //  .setLoyaltyPoints(LoyaltyPoints().setBalance(LoyaltyPointsBalance().setString("105,154 " +
              //          "\n\n DOLLARS OFF  \n  $105 ")))
                .setInfoModuleData(InfoModuleData().setLabelValueRows(object : ArrayList<LabelValueRow?>() {
                    init {
                        add(LabelValueRow().setColumns(object : ArrayList<LabelValue?>() {
                            init {
                             //   add(LabelValue().setLabel(loyalityData.nameLabel).setValue(loyalityData.nameValue))
                             //   add(LabelValue().setLabel(loyalityData.emailLabel).setValue(loyalityData.emailValue))
                                add(LabelValue().setLabel(loyalityData.detailsLabel).setValue(loyalityData.detailsValue))
                                add(LabelValue().setLabel(loyalityData.valuesLabel).setValue(loyalityData.valuesValue))
                                add(LabelValue().setLabel(loyalityData.howToUseLabel).setValue(loyalityData.howToUseValue))
                                add(LabelValue().setLabel(loyalityData.termConditionLabel).setValue(loyalityData.termConditionValue))
                            }
                        }))
                    }
                })
                )
               // .setLinksModuleData(LinksModuleData().set("Nearby Locations", "https://maps.google.com/maps/search/petro-canada/@46.504933,-89.9771504,3z/data=!3m1!4b1"))
    }
}