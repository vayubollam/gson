package suncor.com.android.data.carwash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.worklight.wlclient.api.WLFailResponse
import com.worklight.wlclient.api.WLResourceRequest
import com.worklight.wlclient.api.WLResponse
import com.worklight.wlclient.api.WLResponseListener
import suncor.com.android.SuncorApplication
import suncor.com.android.mfp.ErrorCodes
import suncor.com.android.model.Resource
import suncor.com.android.model.carwash.ActivateCarwashRequest
import suncor.com.android.model.carwash.ActivateCarwashResponse
import suncor.com.android.model.carwash.reload.TransactionReloadData
import suncor.com.android.model.carwash.reload.TransactionReloadTaxes
import suncor.com.android.utilities.Timber
import java.net.URI
import java.net.URISyntaxException

class CarwashApiImpl(val gson: Gson = GsonBuilder().disableHtmlEscaping().create()): CarwashApi {
    companion object {
        private const val ADAPTER_PATH = "/adapters/suncorcarwash/v1/rfmp-secure"
    }

    override fun activateCarwash(activateCarwashRequest: ActivateCarwashRequest): LiveData<Resource<ActivateCarwashResponse>> {
        Timber.d("request initiate for activate car wash ")
        val result = MutableLiveData<Resource<ActivateCarwashResponse>>()
        result.postValue(Resource.loading())

        try {
            val adapterPath = URI("$ADAPTER_PATH/ActivateCarWash")
            val request = WLResourceRequest(adapterPath, WLResourceRequest.POST, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE)
            val body = gson.toJson(activateCarwashRequest)

            Timber.i("Send Activate Carwash, string: $activateCarwashRequest\nbody: $body")

            request.send(body, object : WLResponseListener {
                override fun onSuccess(wlResponse: WLResponse) {
                    val jsonText = wlResponse.responseText
                    Timber.d("Activate Carwash success, response:\n$jsonText")
                    val activateCarwashResponse = gson.fromJson(jsonText, ActivateCarwashResponse::class.java)
                    result.postValue(Resource.success(activateCarwashResponse))
                }

                override fun onFailure(wlFailResponse: WLFailResponse) {
                    Timber.d("Activate Carwash API failed, $wlFailResponse")
                    Timber.e(wlFailResponse.toString())
                    result.postValue(Resource.error(
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

    override fun reloadTransactionCarwash(cardType: String): LiveData<Resource<TransactionReloadData>> {
        Timber.d("request initiate for relaod transaction form data ")
        val result = MutableLiveData<Resource<TransactionReloadData>>()
        result.postValue(Resource.loading())

        try {
            val adapterPath = URI("$ADAPTER_PATH/ReloadTransactionForm/" + cardType)
            val request = WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE)

            request.send( object : WLResponseListener {
                override fun onSuccess(wlResponse: WLResponse) {
                    val jsonText = wlResponse.responseText
                    Timber.d("Reload Transaction Form Data success, response:\n$jsonText")
                    val transactionReloadData = gson.fromJson(jsonText, TransactionReloadData::class.java)
                    result.postValue(Resource.success(transactionReloadData))
                }

                override fun onFailure(wlFailResponse: WLFailResponse) {
                    Timber.d("Reload Transaction Form Data API failed, $wlFailResponse")
                    Timber.e(wlFailResponse.toString())
                    result.postValue(Resource.error(
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

    override fun taxCalculationTransactionCarwash(
        rewardId: String,
        province: String
    ): LiveData<Resource<TransactionReloadTaxes>> {
        Timber.d("request initiate for relaod transaction tax data ")
        val result = MutableLiveData<Resource<TransactionReloadTaxes>>()
        result.postValue(Resource.loading())

        try {
            val adapterPath = URI("$ADAPTER_PATH/ReloadTransactionForm/Tax?rewardId=$rewardId&province=$province")
            val request = WLResourceRequest(adapterPath, WLResourceRequest.GET, SuncorApplication.DEFAULT_TIMEOUT, SuncorApplication.PROTECTED_SCOPE)

            request.send( object : WLResponseListener {
                override fun onSuccess(wlResponse: WLResponse) {
                    val jsonText = wlResponse.responseText
                    Timber.d("Reload Transaction Form tax success, response:\n$jsonText")
                    val transactionReloadData = gson.fromJson(jsonText, TransactionReloadTaxes::class.java)
                    result.postValue(Resource.success(transactionReloadData))
                }

                override fun onFailure(wlFailResponse: WLFailResponse) {
                    Timber.d("Reload Transaction Form tax API failed, $wlFailResponse")
                    Timber.e(wlFailResponse.toString())
                    result.postValue(Resource.error(
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