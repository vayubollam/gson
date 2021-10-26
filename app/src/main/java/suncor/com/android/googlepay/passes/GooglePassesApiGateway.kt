package suncor.com.android.googleapis.passes

import android.content.Context
import com.google.api.client.json.GenericJson
import com.google.api.services.walletobjects.model.LoyaltyObject
import com.google.gson.JsonObject
import suncor.com.android.BuildConfig
import suncor.com.android.R
import suncor.com.android.googlepay.passes.LoyalityData
import suncor.com.android.model.SettingsResponse
import suncor.com.android.utilities.Timber
import java.math.BigDecimal
import java.util.*

class GooglePassesApiGateway {
    enum class VerticalType {
        LOYALTY
    }

    /**
     *
     * Generates a signed "skinny" JWT.
     * 2 REST calls are made:
     * x1 pre-insert one classes
     * x1 pre-insert one object which uses previously inserted class
     * This is the shortest type of JWT; recommended for Android intents/redirects.
     *
     * See https://developers.google.com/pay/passes/reference/v1/
     */
    fun makeSkinnyJwt(context: Context, verticalType: VerticalType?, passesConfig: SettingsResponse.GooglePassConfig, objectId: String?, loyalityData: LoyalityData): String? {
        var signedJwt: String? = null
        val resourceDefinitions = GooglePassesResourceDefination
        val restMethods: GooglePassesRestClient = GooglePassesRestClient.instance!!
        var objectResourcePayload: GenericJson? = null
        var objectResponse: GenericJson? = null
        try {
            // get class, object definitions, insert class and object (check class in Merchant center GUI: https://pay.google.com/gp/m/issuer/list)
            when (verticalType) {
                VerticalType.LOYALTY -> {
                    objectResourcePayload = resourceDefinitions.makeLoyaltyObjectResource(passesConfig.googlePassesClassId, objectId, loyalityData)
                    objectResponse = restMethods.insertLoyaltyObject(objectResourcePayload as LoyaltyObject?, context)
                }
            }
            if (objectResponse!!["code"]!!.equals("200") && objectResponse["code"]!!.equals("409")) {
                return null
            }
            Timber.e("GOOGLE PASSES: insert loyality Object")
            // put into JSON Web Token (JWT) format for Google Pay API for Passes
            val googlePassJwt = GooglePassesJwtAuthentication()
            // only need to add objectId in JWT because class and object definitions were pre-inserted via REST call
            val jwtPayload = JsonObject()
            when (verticalType) {
                VerticalType.LOYALTY -> {
                    jwtPayload.addProperty("id", objectId)
                    googlePassJwt.addLoyaltyObject(jwtPayload)
                }
            }
            Timber.e("GOOGLE PASSES: Add loyality Object")
            // sign JSON to make signed JWT
            signedJwt = googlePassJwt.generateSignedJwt(context, passesConfig.googlePassesAccountEmailAddress)
        } catch (e: Exception) {
            e?.let {
                Timber.e("Error on creating the loyality token" + e.message)
            }
        }
        // return "skinny" JWT. Try putting it into save link.
        // See https://developers.google.com/pay/passes/guides/get-started/implementing-the-api/save-to-google-pay#add-link-to-email
        return signedJwt
    }

    fun demoSkinnyJwt(context: Context, passesConfig: SettingsResponse.GooglePassConfig , objectId: String?, loyalityData: LoyalityData): String? {
        Timber.d("Generates a signed")
        val skinnyJwt = makeSkinnyJwt(context, VerticalType.LOYALTY, passesConfig, objectId, loyalityData)
        if (skinnyJwt != null) {
            return GooglePassesConfig.SAVE_LINK + skinnyJwt
        }
        return null
    }

    fun insertLoyalityCard(context: Context, loyalityData: LoyalityData, passesConfig: SettingsResponse.GooglePassConfig): String? {
        val verticalType = VerticalType.LOYALTY
        val uuidString = "petro_card_loyality_$loyalityData.barcode"


        // your objectUid should be a hash based off of pass metadata, for the demo we will use pass-type_object_uniqueid
        val objectUid = String.format("%s_OBJECT_%s", verticalType.toString(), UUID.nameUUIDFromBytes(uuidString?.toByteArray()).toString())

        // check Reference API for format of "id", for example offer:(https://developers.google.com/pay/passes/reference/v1/offerobject/insert).
        // Must be alphanumeric characters, ".", "_", or "-".
        val objectId = String.format("%s.%s", passesConfig.googlePassesIssuerId, objectUid)
        return demoSkinnyJwt(context, passesConfig, objectId, loyalityData )
    }
}