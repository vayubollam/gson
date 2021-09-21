package suncor.com.android.utilities

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

    fun collect(environment: Int) {
        if (initCompleted.get().not()) {
            initKount(environment)
        }
        collectData()
    }

    fun collectForMerchant(id: Int, environment: Int) {
        if (initCompleted.get().not()) {
            initSDK(id, environment)
        }
        collectData()
    }

    private fun initKount(environment: Int) {
        val merchantId = BuildConfig.KOUNT_MERCHANT_ID
        val merchantIdInt = merchantId.toInt()
        initSDK(merchantIdInt, environment)
    }

    private fun initSDK(merchantId: Int, environment: Int) {
        AnalyticsCollector.setMerchantId(merchantId)
        AnalyticsCollector.collectAnalytics(true)
        AnalyticsCollector.setEnvironment(environment)
        AnalyticsCollector.getInstance().setLocationCollectorConfig(AnalyticsCollector.LocationConfig.COLLECT)
        initCompleted.set(true)
    }

    private fun collectData() {
        val uuid = UUID.randomUUID().toString().replace("-", "")
        DataCollector.getInstance().collectForSession(uuid, object : DataCollector.CompletionHandler {
            override fun completed(p0: String?) {
                Log.d(TAG, " success $p0 - for sessionID($uuid))")
            }

            override fun failed(p0: String?, p1: DataCollector.Error?) {
                Log.d(TAG, " error - (${p0}) - for sessionID($uuid))")
            }

        })
        this.currentSessionId = uuid
    }

}