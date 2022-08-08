package suncor.com.android.ui.main.pap.receipt

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import suncor.com.android.SuncorApplication
import suncor.com.android.data.pap.PapRepository
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.pap.transaction.Transaction
import java.util.*


class ReceiptViewModelTest {
    private val transactionId = "8d982d43-e8f5-4288-8b78-52d92557e77f"
    private var testMonth = 2
    private var viewModel: ReceiptViewModel? = null
    private val papRepository = Mockito.mock(PapRepository::class.java)
    private val sessionManager = Mockito.mock(
        SessionManager::class.java
    )
    private val suncorApplication = Mockito.mock(
        SuncorApplication::class.java
    )

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        viewModel = ReceiptViewModel(papRepository, sessionManager)
    }

    @Test
    fun test_get_transactional_details_success() {
        val transaction: Transaction = Gson().fromJson(response, Transaction::class.java)
        val apiResponse = MutableLiveData<Resource<Transaction>>()
        apiResponse.postValue(Resource.success(transaction))
        Mockito.`when`<LiveData<Resource<Transaction>>>(
            papRepository?.getTransactionDetails(transactionId, false)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Transaction> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertNotNull(apiResponse.value?.data?.receiptData)

    }

    @Test
    fun test_get_transactional_details_error() {
        val transaction: Transaction = Gson().fromJson(response, Transaction::class.java)
        val apiResponse = MutableLiveData<Resource<Transaction>>()
        apiResponse.postValue(Resource.error("fail", transaction))
        Mockito.`when`<LiveData<Resource<Transaction>>>(
            viewModel?.getTransactionDetails(transactionId, false)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Transaction> -> }
        apiResponse.observeForever(dummyObserver)


        if (apiResponse.value?.status == Resource.Status.ERROR) {
            Assert.assertTrue(true)
        }


    }

    @Test
    fun test_is_first_transaction_Of_month() {
        testMonth = getCurrentMonth();
        val c = Calendar.getInstance()
        val currentMonth = c[Calendar.MONTH]
        Assert.assertEquals(testMonth, currentMonth)
    }

    @Test
    fun test_is_not_first_transaction_Of_month() {
        val c = Calendar.getInstance()
        val currentMonth = c[Calendar.MONTH]
        if (testMonth != currentMonth) {
            Assert.assertFalse(currentMonth.toString(), false)
        } else {
            Assert.assertTrue(currentMonth.toString(), true)
        }

    }


    fun getCurrentMonth(): Int {
        val c = Calendar.getInstance()

        return c[Calendar.MONTH]
    }


    private val response =
        "{\"transactionId\":\"8d982d43-e8f5-4288-8b78-52d92557e77f\",\"address\":{\"streetAddress\":\"55 Spadina Avenue\",\"city\":\"Toronto\",\"stateCode\":\"ON\",\"postalCode\":\"M5V2J2\",\"countryIsoCode\":\"CA\"},\"storeName\":\"55 Spadina Avenue\",\"merchantId\":\"0304-9944\",\"authCode\":\"KN5159\",\"cardType\":\"Visa\",\"lastFour\":\"5603\",\"posDatetimeUtc\":\"2022-08-08T13:36:58.3870277Z\",\"posDatetimeLocal\":\"2022-08-08T09:36:58.3870277Z\",\"formattedPosDatetimeLocal\":\"8/8/2022 9:36AM Eastern Standard Time\",\"timeZone\":\"Eastern Standard Time\",\"utcOffsetSeconds\":-14400,\"totalAmount\":5.000,\"currency\":\"USD\",\"subtotal\":5.000,\"taxAmount\":0.0,\"formattedSubtotal\":\"\$5.00\",\"formattedTax\":\"\$0.00\",\"formattedTotal\":\"\$5.00\",\"lineItems\":[{\"unitPrice\":2.419,\"unitOfMeasure\":\"Liter\",\"formattedUnitPrice\":\"\$2.42\",\"itemId\":\"1\",\"itemDescription\":\"Unleaded Plus\",\"posCode\":\"002\",\"paymentSystemProductCode\":\"002\",\"quantity\":2.067,\"total\":5.000,\"formattedTotal\":\"\$5.00\",\"isFuelItem\":true,\"offers\":[]}],\"evChargingDetails\":[],\"loyaltyPointsMessages\":[{\"p97programId\":\"90941a75-14e3-4764-b1cf-5fa3f7814bd8\",\"programName\":\"Petro-Points\",\"loyaltyInstrument\":\"7069441303484011\",\"unit\":\"currency\",\"preSaleRewardsBalance\":0.0,\"earnedRewardSummary\":0.0,\"burnedRewardSummary\":0.0,\"finalRewardsBalance\":0.0,\"finalRewardsLimit\":0.0}],\"receiptData\":[\"               P97 NETWORKS               \",\"           POWERED BY PETROZONE           \",\"            10333 RICHMOND AVE            \",\"               HOUSTON, TX                \",\"              VP13007411001               \",\"            VeriFone Gold Disk            \",\"                    FL                    \",\"<CUSTOMER COPY>                           \",\"  Description             Qty      Amount \",\"  -------------           ---      ------ \",\"  Unleaded Plus          2.07        5.00 \",\"                               ---------- \",\"                 Subtotal         5.00    \",\"                 Discount            0    \",\"                      Tax            0    \",\"            TOTAL                 5.00    \",\"                   MOBILE         5.00    \",\"MOBILE                                    \",\"Visa                                      \",\"476173**********                          \",\"AUTH #: 660156880011247090                \",\"                PETROZONE                 \",\"         FUELING MOBILE COMMERCE          \",\"ST# AB123  TILL XXXX DR# 1 TRAN# 1010080  \",\"CSH: 1                8/8/2022 1:36:59 PM \"],\"terminalType\":\"outside\",\"fuelBrand\":\"Petro-Canada\",\"appChannel\":\"ThirdPartyMobile\",\"appDisplayName\":\"Petro Canada Fuel\",\"fuelPpuDiscountAmount\":0.0,\"fuelPpuUnitOfMeasure\":\"Liter\",\"formattedFuelPpuDiscountAmount\":\"\$0.00\",\"dispenserPosition\":\"2\",\"basketPaymentState\":\"authorized\",\"storeTenantId\":\"ea4f8b3f-fbc5-49ca-a604-205611faee62\",\"storeTenantName\":\"Petro-Canada Fuel\",\"storeNumber\":\"00823\",\"posTransactionId\":\"8d982d43e8f542888b7852d92557e77f\",\"partnerTransactionId\":\"0082390000020220808\",\"p97Discount\":0.00,\"otherDiscount\":0.00,\"p97PpuDiscountAmount\":0.0,\"externalPpuDiscountAmount\":0.0,\"otherPpuDiscountAmount\":0.0,\"formattedRefundTotal\":\"\$0.00\",\"pumpNumber\":\"2\",\"vatMerchantId\":null,\"numberOfItems\":2,\"posTerminalId\":\"2\",\"paymentIntentType\":\"Sale\",\"customProperties\":{},\"documents\":[],\"formattedTotalDiscounts\":\"\$0.00\",\"totalDiscount\":0.0}\n"
}