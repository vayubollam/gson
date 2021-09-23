package suncor.com.android.utilities

import android.content.Context
import android.util.Log
import com.kount.api.DataCollector
import com.kount.api.analytics.AnalyticsCollector
import suncor.com.android.BuildConfig
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

object KountManager {

    const val TAG = "KountManager"

    var currentSessionId: String = ""
    private var initCompleted: AtomicBoolean = AtomicBoolean(false)
    private var dataCollector: DataCollector? = null

    fun collect(environment: Int, context: Context) {
        val merchantId = BuildConfig.KOUNT_MERCHANT_ID
        val merchantIdInt = merchantId.toInt()
        if (initCompleted.get().not()) {
            initKount(environment, merchantIdInt, context)
        }
        collectData()
    }

    fun collectForMerchant(id: Int, environment: Int, context: Context) {
        if (initCompleted.get().not()) {
            initSDK(id, environment, context)
        }
        collectData()
    }

    private fun initKount(environment: Int, merchantId: Int, context: Context) {
        initSDK(merchantId, environment, context)
    }

    private fun initSDK(merchantId: Int, environment: Int, context: Context) {
        AnalyticsCollector.setMerchantId(merchantId)
        AnalyticsCollector.collectAnalytics(true)
        AnalyticsCollector.setEnvironment(environment)
        initCompleted.set(true)
        dataCollector = DataCollector.getInstance()
        dataCollector?.setContext(context)
        dataCollector?.setEnvironment(environment);
        dataCollector?.setMerchantID(merchantId);
        dataCollector?.setLocationCollectorConfig(DataCollector.LocationConfig.COLLECT);
    }

    private fun collectData() {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        Log.d(TAG, "sessionID($uuid))")
        dataCollector?.collectForSession(uuid, object : DataCollector.CompletionHandler {
            override fun completed(p0: String?) {
                Log.d(TAG, " success $p0 - for sessionID($uuid))")
            }

            override fun failed(p0: String?, p1: DataCollector.Error?) {
                Log.d(TAG, " error - (${p0}) - for sessionID($uuid))")
                Log.d(TAG, " error - (${p1!!.code}) Des : (${p1!!.description}) - for sessionID($uuid))")
            }
        })
        this.currentSessionId = uuid
    }

}