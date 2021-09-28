package suncor.com.android.googleapis.passes

import android.apache.commons.codec.binary.Base64
import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.oauth.jsontoken.JsonToken
import net.oauth.jsontoken.crypto.AsciiStringSigner
import net.oauth.jsontoken.crypto.RsaSHA256Signer
import org.joda.time.Instant
import suncor.com.android.googleapis.passes.JsonTokenUtils.toBase64
import suncor.com.android.googleapis.passes.JsonTokenUtils.toDotFormat
import suncor.com.android.utilities.Timber
import java.security.InvalidKeyException
import java.security.SignatureException
import java.util.*

/*******************************
 *
 * class that defines JWT format for a Google Pay Pass.
 *
 * to check the JWT protocol for Google Pay Passes, check:
 * https://developers.google.com/pay/passes/reference/s2w-reference#google-pay-api-for-passes-jwt
 *
 * also demonstrates RSA-SHA256 signing implementation to make the signed JWT used
 * in links and buttons. Learn more:
 * https://developers.google.com/pay/passes/guides/get-started/implementing-the-api/save-to-google-pay
 *
 */
class GooglePassesJwtAuthentication {
    private val audience: String
    private val type: String
  //  private val iss: String
    private val iat: Instant
    private val payload: JsonObject
    private var signer: RsaSHA256Signer? = null
    fun addLoyaltyClass(resourcePayload: JsonElement?) {
        if (payload["loyaltyClasses"] == null) {
            val loyaltyObjects = JsonArray()
            payload.add("loyaltyClasses", loyaltyObjects)
        }
        val newPayload = payload["loyaltyClasses"] as JsonArray
        newPayload.add(resourcePayload)
        payload.add("loyaltyClasses", newPayload)
        return
    }

    fun addLoyaltyObject(resourcePayload: JsonElement?) {
        if (payload["loyaltyObjects"] == null) {
            val loyaltyObjects = JsonArray()
            payload.add("loyaltyObjects", loyaltyObjects)
        }
        val newPayload = payload["loyaltyObjects"] as JsonArray
        newPayload.add(resourcePayload)
        payload.add("loyaltyObjects", newPayload)
        return
    }

    fun generateUnsignedJwt(context: Context, serviceAccountEmailAddress: String): JsonToken {
        try {
            signer = RsaSHA256Signer(serviceAccountEmailAddress,
                    null, GooglePassesConfig().getServiceAccountPrivateKey(context))
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        }
        val token = JsonToken(signer)
        token.audience = audience
        token.setParam("typ", type)
        token.issuedAt = iat
        token.payloadAsJsonObject.add("payload", payload)
        Timber.e("generateUnsignedJwt")
        return token
    }

    fun generateSignedJwt(context: Context, serviceAccountEmailAddress: String): String? {
        val jwtToSign = generateUnsignedJwt(context, serviceAccountEmailAddress)
        val signedJwt: String? = null
        try {
            val baseString = computeSignatureBaseString(null, jwtToSign)
            Timber.e("computeSignatureBaseString")
            val sig = getSignature(baseString)
            Timber.e("getSignature")
            return toDotFormat(baseString, sig)
        } catch (e: SignatureException) {
            Timber.e("Error on generate Signed JWT" , e.message!!)
        }
        return signedJwt
    }

    protected fun computeSignatureBaseString(baseString: String?, jwtToSign: JsonToken): String {
        var baseString = baseString
        if (baseString != null && !baseString.isEmpty()) {
            return baseString
        }
        baseString = toDotFormat(
                toBase64(jwtToSign.header),
                toBase64(jwtToSign.payloadAsJsonObject)
        )
        return baseString
    }

    @Throws(SignatureException::class)
    private fun getSignature(baseString: String): String {
        if (signer == null) {
            throw SignatureException("can't sign JsonToken with signer.")
        }
        val signature: String
        // now, generate the signature
        val asciiSigner = AsciiStringSigner(signer)
        signature = Base64.encodeBase64URLSafeString(asciiSigner.sign(baseString))
        return signature
    }

    init {
        val config = GooglePassesConfig()
        audience = config.audience
        type = config.jwtType
       // iss = config.serviceAccountEmailAddress
        iat = Instant(
                Calendar.getInstance().timeInMillis - 5000L)
        payload = JsonObject()
    }
}