package suncor.com.android.googleapis.passes

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.GenericJson
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.walletobjects.Walletobjects
import com.google.api.services.walletobjects.model.LoyaltyClass
import com.google.api.services.walletobjects.model.LoyaltyObject
import suncor.com.android.utilities.Timber

class GooglePassesRestClient private constructor() {
    /*******************************
     *
     * Preparing server-to-server authorized API call with OAuth 2.0
     *
     * Use Google API client library to prepare credentials used to authorize a http client
     * See https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#service_accounts
     *
     * @return GoogleCredential credentials - Service Account credential for OAuth 2.0 signed JWT grants.
     */
    private fun makeOauthCredential(context: Context?): GoogleCredential? {
        val config = GooglePassesConfig()
        var credentials: GoogleCredential? = null
        // Create a JsonFactory to be used for the walletobjects client
        val jsonFactory: JsonFactory = GsonFactory()
        // the variables are in config file
        try {
            credentials = GoogleCredential
                    .fromStream(context!!.assets.open(config.serviceAccountFile), httpTransport, jsonFactory)
                    .createScoped(config.scopes)
        } catch (ex: Exception) {
            Timber.e(ex.message!!)
        }
        return credentials
    }

    /*******************************
     *
     * Insert defined class with Google Pay API for Passes REST API
     *
     * See https://developers.google.com/pay/passes/reference/v1/loyaltyclass/insert
     *
     * @param LoyaltyClass loyaltyClass - represents loyalty class resource.
     * @return GenericJson response - response from REST call
     */
    fun insertLoyaltyClass(loyaltyClass: LoyaltyClass?, context: Context?): GenericJson? {
        var response: GenericJson? = null
        val credentials = makeOauthCredential(context)
        if (httpTransport == null) {
            httpTransport = NetHttpTransport()
        }

        // Use the Google Pay API for Passes Java client lib to insert the Loyalty class
        //// check the devsite for newest client lib: https://developers.google.com/pay/passes/support/libraries#libraries
        //// check reference API to see the underlying REST call:
        //// https://developers.google.com/pay/passes/reference/v1/loyaltyclass/insert
        //// The methods to call these from client library are in Class com.google.api.services.walletobjects.Walletobjects
        val jsonFactory: JsonFactory = GsonFactory()
        val client = Walletobjects.Builder(httpTransport, jsonFactory, credentials)
                .build()
        try {
            response = client.loyaltyclass().insert(loyaltyClass).execute()
            response.put("code", 200)
            // System.out.println(response);
        } catch (gException: GoogleJsonResponseException) {
            Timber.e(">>>> [START] Google Server Error response: {} >>>> [END] Google Server Error response\n" + gException.details)
            response = gException.details
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
        return response
    }

    /*******************************
     *
     * Get defined class with Google Pay API for Passes REST API
     *
     * See https://developers.google.com/pay/passes/reference/v1/loyaltyclass/get
     *
     * @param String classId - The unique identifier for a class.
     * @return GenericJson response - response from REST call
     */
    fun getLoyaltyClass(classId: String?): GenericJson? {
        var response: GenericJson? = null
        val credentials = makeOauthCredential(null)

        // Use the Google Pay API for Passes Java client lib to get an Loyalty class
        //// check the devsite for newest client lib: https://developers.google.com/pay/passes/support/libraries#libraries
        //// check reference API to see the underlying REST call:
        //// https://developers.google.com/pay/passes/reference/v1/loyaltyclass/get
        //// The methods to call these from client library are in Class com.google.api.services.walletobjects.Walletobjects
        val jsonFactory: JsonFactory = GsonFactory()
        val client = Walletobjects.Builder(httpTransport, jsonFactory, credentials).build()
        try {
            response = client.loyaltyclass()[classId].execute()
            response.put("code", 200)
        } catch (gException: GoogleJsonResponseException) {
            Timber.e(">>>> [START] Google Server Error response: {} >>>> [END] Google Server Error response " + gException.details)
            response = gException.details
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
        return response
    }

    /*******************************
     *
     * Insert defined object with Google Pay API for Passes REST API
     *
     * See https://developers.google.com/pay/passes/reference/v1/loyaltyobject/insert
     *
     * @param LoyaltyObject loyaltyObject - represents loyalty class resource.
     * @return GenericJson response - response from REST call
     */
    fun insertLoyaltyObject(loyaltyObject: LoyaltyObject?, context: Context?): GenericJson? {
        var response: GenericJson? = null
        val credentials = makeOauthCredential(context)

        // Use the Google Pay API for Passes Java client lib to insert an Loyalty object
        //// check the devsite for newest client lib: https://developers.google.com/pay/passes/support/libraries#libraries
        //// check reference API to see the underlying REST call:
        //// https://developers.google.com/pay/passes/reference/v1/loyaltyobject/insert
        //// The methods to call these from client library are in Class com.google.api.services.walletobjects.Walletobjects
        val jsonFactory: JsonFactory = GsonFactory()
        val client = Walletobjects.Builder(httpTransport, jsonFactory, credentials)
                .build()
        try {
            response = client.loyaltyobject().insert(loyaltyObject).execute()
            response.put("code", 200)
            // System.out.println(response);
        } catch (gException: GoogleJsonResponseException) {
            Timber.e(">>>> [START] Google Server Error response: {} >>>> [END] Google Server Error response" + gException.details)
            response = gException.details
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    /*******************************
     *
     * Get defined object with Google Pay API for Passes REST API
     *
     * See https://developers.google.com/pay/passes/reference/v1/loyaltyobject/get
     *
     * @param String objectId - The unique identifier for an object.
     * @return GenericJson response - response from REST call
     */
    fun getLoyaltyObject(objectId: String?): GenericJson? {
        var response: GenericJson? = null
        val credentials = makeOauthCredential(null)

        // Use the Google Pay API for Passes Java client lib to get a loyalty object
        //// check the devsite for newest client lib: https://developers.google.com/pay/passes/support/libraries#libraries
        //// check reference API to see the underlying REST call:
        //// https://developers.google.com/pay/passes/reference/v1/loyaltyobject/get
        //// The methods to call these from client library are in Class com.google.api.services.walletobjects.Walletobjects
        val jsonFactory: JsonFactory = GsonFactory()
        val client = Walletobjects.Builder(httpTransport, jsonFactory, credentials)
                .build()
        try {
            response = client.loyaltyobject()[objectId].execute()
            response.put("code", 200)
        } catch (gException: GoogleJsonResponseException) {
            Timber.e(">>>> [START] Google Server Error response: {} >>>> [END] Google Server Error response " + gException.details)
            response = gException.details
        } catch (e: Exception) {
            Timber.e(e.message!!)
        }
        return response
    }

    companion object {
        private var httpTransport: HttpTransport? = null
        private var restMethods: GooglePassesRestClient? = GooglePassesRestClient()
        val instance: GooglePassesRestClient?
            get() {
                if (restMethods == null) {
                    restMethods = GooglePassesRestClient()
                }
                return restMethods
            }
    }

    init {
        // Create an httpTransport which will be used for the REST call
        httpTransport = NetHttpTransport()
    }
}