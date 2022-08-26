package suncor.com.android.data.redeem.donate

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.worklight.wlclient.api.WLFailResponse
import com.worklight.wlclient.api.WLResourceRequest
import com.worklight.wlclient.api.WLResponse
import com.worklight.wlclient.api.WLResponseListener
import org.json.JSONObject
import suncor.com.android.SuncorApplication
import suncor.com.android.data.carwash.CarwashApiImpl
import suncor.com.android.mfp.ErrorCodes
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashResponse
import suncor.com.android.model.redeem.request.DonateRequest
import suncor.com.android.utilities.Timber
import java.net.URI
import java.net.URISyntaxException

class DonateApiImpl(val gson: Gson = GsonBuilder().disableHtmlEscaping().create()) : DonateApi {

    companion object{
        private const val DONATE_URI = "/adapters/suncor/v1/rfmp-secure/donatepoints"
    }

    override fun donatePoints(donateRequest: DonateRequest): MutableLiveData<Resource<Unit>> {
        Timber.d("Request to donate points made")
        val result = MutableLiveData<Resource<Unit>>()
        result.postValue(Resource.loading())

        try {
            val adapterPath= URI(DONATE_URI)
            val request = WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE)
            val body = JSONObject(gson.toJson(donateRequest))

            Timber.i("Send donate Request, string: $donateRequest\nbody: $body, \n $adapterPath")

            request.send(body, object : WLResponseListener {
                override fun onSuccess(wlResponse: WLResponse) {
                    val jsonText = wlResponse.responseText
                    Timber.d("Donation success, response:\n$jsonText")
                    result.postValue(Resource.success(Unit))
                }

                override fun onFailure(wlFailResponse: WLFailResponse) {
                    Timber.d("Donate API failed, $wlFailResponse")
                    Timber.e(wlFailResponse.toString())
                    result.postValue(
                        Resource.error(
                        if (wlFailResponse.responseJSON != null && wlFailResponse.responseJSON.has("resultSubcode"))
                            wlFailResponse.responseJSON.getString("resultSubcode") else ""))
                }
            })
        } catch (e: URISyntaxException) {
            Timber.e(e.toString())
            result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR))
        }
        return result
    }
}