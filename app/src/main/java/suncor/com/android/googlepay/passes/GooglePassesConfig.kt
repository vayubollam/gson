package suncor.com.android.googleapis.passes

import android.content.Context
import com.google.api.client.util.PemReader
import com.google.api.client.util.SecurityUtils
import org.json.JSONObject
import suncor.com.android.BuildConfig
import suncor.com.android.utilities.Timber
import java.io.Reader
import java.io.StringReader
import java.security.interfaces.RSAPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.*

/******************************
 *
 * Config
 *
 * Define constants used to:
 * a) authorize REST calls
 * b) sign JSON Web Token (JWT)
 *
 */
class GooglePassesConfig {
    val scopes: ArrayList<String>
    private var SERVICE_ACCOUNT_PRIVATE_KEY: RSAPrivateKey? = null
    fun loadPrivateKey(context: Context) {
        // Load the private key as a java RSAPrivateKey object from service account file
        var content: String? = null
        try {
            val inputStream = context.assets.open(serviceAccountFile)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            content = String(buffer)
            val privateKeyJson = JSONObject(content)
            val privateKeyPkcs8 = privateKeyJson["private_key"] as String
            val reader: Reader = StringReader(privateKeyPkcs8)
            val section = PemReader.readFirstSectionAndClose(reader, "PRIVATE KEY")
            val bytes = section.base64DecodedBytes
            val keySpec = PKCS8EncodedKeySpec(bytes)
            val keyFactory = SecurityUtils.getRsaKeyFactory()
            SERVICE_ACCOUNT_PRIVATE_KEY = keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
    }

    fun getServiceAccountPrivateKey(context: Context): RSAPrivateKey? {
        if (SERVICE_ACCOUNT_PRIVATE_KEY == null) {
            loadPrivateKey(context)
        }
        return SERVICE_ACCOUNT_PRIVATE_KEY
    }

    val serviceAccountFile: String
        get() = BuildConfig.PASSES_SERVICE_ACCOUNT_FILE
    val serviceAccountEmailAddress: String
        get() = BuildConfig.PASSES_SERVICE_ACCOUNT_EMAIL_ADDRESS
    val issuerId: String
        get() = BuildConfig.PASSES_ISSUER_ID
    val audience: String
        get() = BuildConfig.PASSES_AUDIENCE
    val jwtType: String
        get() = BuildConfig.PASSES_JWT_TYPE

    companion object {
        var SAVE_LINK = "https://pay.google.com/gp/v/save/" // Save link that uses JWT. See https://developers.google.com/pay/passes/guides/get-started/implementing-the-api/save-to-google-pay#add-link-to-email
    }

    init {
        scopes = object : ArrayList<String>() {
            val serialVersionUID = 1L

            init {
                add("https://www.googleapis.com/auth/wallet_object.issuer")
            }
        }
    }
}