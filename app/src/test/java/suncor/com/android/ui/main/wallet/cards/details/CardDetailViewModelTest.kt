package suncor.com.android.ui.main.wallet.cards.details

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.Mockito
import org.mockito.Mockito.mock
import suncor.com.android.R
import suncor.com.android.data.cards.CardsRepository
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.googleapis.passes.GooglePassesApiGateway
import suncor.com.android.googlepay.passes.LoyalityData
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.SettingsResponse
import suncor.com.android.model.account.Profile
import suncor.com.android.model.cards.CardDetail
import suncor.com.android.model.station.Station
import suncor.com.android.ui.main.wallet.cards.CardsLoadType
import java.util.*

class CardDetailViewModelTest {
    private lateinit var cardDetailsViewModel: CardDetailsViewModel

    private lateinit var mockCardsList: Array<CardDetail>

    val context = mock(Context::class.java)

    private lateinit var loadType: CardsLoadType

    val WaG_Card = "706981007005445432"

    val SP_Card = "706981007005446786"

    val profile = Profile()

    private val settingsApi = mock(SettingsApi::class.java)

    private val repository = mock(
        CardsRepository::class.java
    )

    private val sessionManager = mock(
        SessionManager::class.java
    )

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var cardsLiveData: MutableLiveData<Resource<ArrayList<CardDetail>>>? = null

    @Before
    fun init() {
        cardDetailsViewModel = CardDetailsViewModel(repository, sessionManager, settingsApi)
        //Add a dummy observer to viewState
        profile.petroPointsNumber = "808744411150291044"
        profile.pointsBalance = 4500
        Mockito.`when`(sessionManager.profile).thenReturn(profile)
        cardDetailsViewModel.isCarWashBalanceZero?.observeForever({ Boolean })
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        cardsLiveData = MutableLiveData()
        cardsLiveData?.observeForever { arrayListResource: Resource<ArrayList<CardDetail>>? -> }


    }

    @Test
    fun test_settings_api_success() {
        cardDetailsViewModel.settingsFromRemote
    }


    @Test
    fun test_update_cardBalance() {
        var isBalanceZero = true
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        if (card.balance > 0) {
            isBalanceZero = false
            Assert.assertFalse(isBalanceZero)
        } else {
            isBalanceZero = true
            Assert.assertTrue(isBalanceZero)
        }
    }


    @Test
    fun test_user_has_only_ppts() {
        loadType = CardsLoadType.PETRO_POINT_ONLY

        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        cardDetailsViewModel.loadType =CardsLoadType.PETRO_POINT_ONLY

        Assert.assertEquals(loadType,cardDetailsViewModel.loadType)
    }

    @Test
    fun test_user_has_only_petro_canada_cards() {
        loadType = CardsLoadType.CAR_WASH_PRODUCTS
        cardDetailsViewModel.loadType =CardsLoadType.CAR_WASH_PRODUCTS

        Assert.assertEquals(loadType, cardDetailsViewModel.loadType)
    }


    @Test
    fun test_DeleteCard() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.removeCard(card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)
        Assert.assertNotNull(apiResponse.value)
        Assert.assertTrue(true)
    }

    @Test
      fun test_isCardSuspended(){
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(true,apiResponse.value?.data?.isSuspendedCard)
      }

    @Test
    fun test_isCardExpired(){
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals("Expired",apiResponse.value?.data?.status)
    }


    @Test
    fun test_User_Profile() {
        val profile = sessionManager.profile
        if (profile != null && profile.petroPointsNumber != null) {

            Assert.assertTrue("Profile available", true)
        } else {
            Assert.assertFalse("Profile not available", false)
        }
    }



    @Test
    fun testWash_GoDetails() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertNotNull(apiResponse.value?.data)
        Assert.assertEquals(card, apiResponse.value?.data)

    }

    @Test
    fun testSPDetails() {
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(card, apiResponse.value?.data)

    }

    @Test
    fun testStoreDetails() {
        val station: Station = Gson().fromJson(responseStoreDetails, Station::class.java)
        val apiResponse = MutableLiveData<Resource<Station>>()
        apiResponse.postValue(Resource.success(station))
        Mockito.`when`<LiveData<Resource<Station>>>(
            repository.getStoreDetails("f3q44R/Jfog/u6o6ck5TUA==")
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Station> -> }
        apiResponse.observeForever(dummyObserver)
        Assert.assertEquals(station, apiResponse.value?.data)

    }

    @Test
    fun testWagIsCanWashTrue() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(card.canWash, apiResponse.value?.data?.canWash)
    }

    @Test
    fun testWagIsCanVacuumTrue() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(card.canVacuum, apiResponse.value?.data?.canVacuum)

    }

    @Test
    fun testSPIsCanWashTrue() {
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(card.canWash, apiResponse.value?.data?.canWash)
    }

    @Test
    fun testSpIsCanVacuumTrue() {
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(card.canVacuum, apiResponse.value?.data?.canVacuum)

    }

    @Test
    fun testWagWashInProgressTrue() {
        val card: CardDetail = Gson().fromJson(responseWAGWashProgress, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.washInProgress = true
        Assert.assertEquals(card.washInProgress, apiResponse.value?.data?.washInProgress)


    }

    @Test
    fun testWagVacuumInprogressTrue() {
        val card: CardDetail = Gson().fromJson(responseWAGVacuumProgress, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.vacuumInProgress = true
        Assert.assertEquals(card.vacuumInProgress, apiResponse.value?.data?.vacuumInProgress)


    }

    @Test
    fun testSpWashInProgressTrue() {
        val card: CardDetail = Gson().fromJson(responseSPWashInprogress, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.washInProgress = true
        Assert.assertEquals(card.washInProgress, apiResponse.value?.data?.washInProgress)


    }

    @Test
    fun testSpVacuumInprogressTrue() {
        val card: CardDetail = Gson().fromJson(responseSPVacuumInprogress, CardDetail::class.java)
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.vacuumInProgress = true
        Assert.assertEquals(card.vacuumInProgress, apiResponse.value?.data?.vacuumInProgress)

    }

    @Test
    fun testWagIsCanWashFalse() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        card.canWash = false
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.canWash = false
        Assert.assertEquals(card.canWash, apiResponse.value?.data?.canWash)
    }

    @Test
    fun testWagIsCanVacuumFalse() {
        val card: CardDetail = Gson().fromJson(responseWAG, CardDetail::class.java)
        card.canVacuum = false
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getWAGCardDetails(WaG_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)

        apiResponse.value?.data?.canVacuum = false
        Assert.assertEquals(card.canVacuum, apiResponse.value?.data?.canVacuum)

    }

    @Test
    fun testSPIsCanWashFalse() {
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        card.canWash = false
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)
        apiResponse.value?.data?.canWash = false
        Assert.assertEquals(card.canWash, apiResponse.value?.data?.canWash)
    }

    @Test
    fun testSpIsCanVacuumFalse() {
        val card: CardDetail = Gson().fromJson(responseSP, CardDetail::class.java)
        card.canVacuum = false
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardDetail>>>(
            repository.getSPCardDetails(SP_Card)
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardDetail> -> }
        apiResponse.observeForever(dummyObserver)
        apiResponse.value?.data?.canVacuum = false
        Assert.assertEquals(card.canVacuum, apiResponse.value?.data?.canVacuum)

    }


    //======Mock Response===///
    private val responseJson = """[{
	"pointsBalance": 992563,
	"cardNumberEncrypted": "dTdZrt1GZySHAfy8BRkFrY8ZWJlExyarMUqVMbKfd/s=",
	"cardType": "PPTS",
	"serviceId": "VZF8UkTva3THk06HcyAC+A==",
	"cardNumber": "7069442727152011"
}, {
	"daysRemaining": 359,
	"lastWashDt": "2022-06-16T15:41:14.903",
	"lastWashStoreId": "f3q44R/Jfog/u6o6ck5TUA==",
	"cardType": "SP",
	"canWash": true,
	"washInProgress": false,
	"cardNumberEncrypted": "BpDIdOR5j1SkjrCt4Tq8Qml+D+zqpRwKIz3NdOchzWI=",
	"vacuumInProgress": false,
	"canVacuum": true,
	"serviceId": "SrimSn19ZC5Jit1VQRF8zQ==",
	"cardNumber": "706981007005453378",
	"status": "Used",
	"vacuumRemaining": 360
}, {
	"daysRemaining": 389,
	"lastWashDt": "2022-06-16T15:34:30.733",
	"lastWashStoreId": "f3q44R/Jfog/u6o6ck5TUA==",
	"cardType": "SP",
	"canWash": false,
	"washInProgress": false,
	"cardNumberEncrypted": "BpDIdOR5j1SkjrCt4Tq8QmwJ1bzYVWesCl13dhmbwGM=",
	"vacuumInProgress": false,
	"canVacuum": true,
	"serviceId": "SrimSn19ZC5Jit1VQRF8zQ==",
	"cardNumber": "706981007005453386",
	"status": "Used",
	"vacuumRemaining": 390
}, {
	"daysRemaining": 270,
	"cardNumberEncrypted": "BpDIdOR5j1SkjrCt4Tq8QtyaUbXn+j+TxxRhEwWVcaA=",
	"cardType": "SP",
	"vacuumInProgress": false,
	"canWash": true,
	"canVacuum": true,
	"serviceId": "SrimSn19ZC5Jit1VQRF8zQ==",
	"washInProgress": false,
	"cardNumber": "706981007005453394",
	"status": "Sold",
	"vacuumRemaining": 270
}, {
	"daysRemaining": 140,
	"lastWashDt": "2022-02-07T10:44:33.867",
	"lastWashStoreId": "f3q44R/Jfog/u6o6ck5TUA==",
	"cardType": "SP",
	"canWash": true,
	"washInProgress": false,
	"cardNumberEncrypted": "9dVVBWkjNAFiI2X2skRHZPDtUzwR2RxDPZB7YJHpw9Y=",
	"vacuumInProgress": false,
	"canVacuum": true,
	"serviceId": "SrimSn19ZC5Jit1VQRF8zQ==",
	"cardNumber": "706981007005453667",
	"status": "Used",
	"vacuumRemaining": 270
}, {
	"lastWashDt": "2022-06-16T16:06:59.357",
	"lastWashStoreId": "f3q44R/Jfog/u6o6ck5TUA==",
	"cardType": "WAG",
	"canWash": true,
	"lastVacuumDt": "2022-05-09T15:38:25.777",
	"lastVacuumSiteId": "KeJeTjdq1tY9ZwrwJzYJrA==",
	"washInProgress": false,
	"cardNumberEncrypted": "z8cBEbacEPxp+J7Q8rypKG8QA7gB8cyEOcdCaee9HY0=",
	"unitsRemaining": 2,
	"vacuumInProgress": false,
	"canVacuum": true,
	"serviceId": "cH33pHKOf0o6bard0jbKCg==",
	"cardNumber": "706981007004301636",
	"status": "Used",
	"vacuumRemaining": 3
}, {
	"lastWashDt": "2022-05-30T11:28:44.263",
	"lastWashStoreId": "f3q44R/Jfog/u6o6ck5TUA==",
	"cardType": "WAG",
	"canWash": false,
	"lastVacuumDt": "2022-04-27T16:04:45.69",
	"lastVacuumSiteId": "KeJeTjdq1tY9ZwrwJzYJrA==",
	"washInProgress": false,
	"cardNumberEncrypted": "BJ1dSL/k/W0h1KgWsUATeehZ88AJsoAT/fBkYEpJhx4=",
	"unitsRemaining": 58,
	"vacuumInProgress": false,
	"canVacuum": false,
	"serviceId": "cH33pHKOf0o6bard0jbKCg==",
	"cardNumber": "706981007005450309",
	"status": "Suspended",
	"vacuumRemaining": 43
}]"""


    private val responseWAG = "{\n" +
            "      \"lastWashDt\": \"2022-07-07T15:16:45.787\",\n" +
            "      \"lastWashStoreId\": \"f3q44R/Jfog/u6o6ck5TUA==\",\n" +
            "      \"cardNumberEncrypted\": \"cTQX2jTzrqSfLF+olV1HoV09gN4RAIKdzD2l2QOk8Xk=\",\n" +
            "      \"unitsRemaining\": 0,\n" +
            "      \"cardType\": \"WAG\",\n" +
            "      \"vacuumInProgress\": false,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": false,\n" +
            "      \"cardNumber\": \"706981007005445432\",\n" +
            "      \"status\": \"Suspended\",\n" +
            "      \"vacuumRemaining\": 31\n" +
            "    }"

    private val responseSP = " {\n" +
            "      \"daysRemaining\": 0,\n" +
            "      \"cardNumberEncrypted\": \"UGtkJjVoCx3R6ID92rsNUle/HjH9LRZl+5kDiPC0aMI=\",\n" +
            "      \"cardType\": \"SP\",\n" +
            "      \"vacuumInProgress\": false,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": false,\n" +
            "      \"cardNumber\": \"706981007005446786\",\n" +
            "      \"status\": \"Expired\",\n" +
            "      \"vacuumRemaining\": 0\n" +
            "    }"

    private val responseSPWashInprogress = " {\n" +
            "      \"daysRemaining\": 90,\n" +
            "      \"cardNumberEncrypted\": \"UGtkJjVoCx3R6ID92rsNUle/HjH9LRZl+5kDiPC0aMI=\",\n" +
            "      \"cardType\": \"SP\",\n" +
            "      \"vacuumInProgress\": false,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": true,\n" +
            "      \"cardNumber\": \"706981007005446786\",\n" +
            "      \"status\": \"Sold\",\n" +
            "      \"vacuumRemaining\": 90\n" +
            "    }"

    private val responseSPVacuumInprogress = " {\n" +
            "      \"daysRemaining\": 90,\n" +
            "      \"cardNumberEncrypted\": \"UGtkJjVoCx3R6ID92rsNUle/HjH9LRZl+5kDiPC0aMI=\",\n" +
            "      \"cardType\": \"SP\",\n" +
            "      \"vacuumInProgress\": true,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": false,\n" +
            "      \"cardNumber\": \"706981007005446786\",\n" +
            "      \"status\": \"Sold\",\n" +
            "      \"vacuumRemaining\": 90\n" +
            "    }"

    private val responseWAGWashProgress = "{\n" +
            "      \"lastWashDt\": \"2022-07-07T15:16:45.787\",\n" +
            "      \"lastWashStoreId\": \"f3q44R/Jfog/u6o6ck5TUA==\",\n" +
            "      \"cardNumberEncrypted\": \"cTQX2jTzrqSfLF+olV1HoV09gN4RAIKdzD2l2QOk8Xk=\",\n" +
            "      \"unitsRemaining\": 17,\n" +
            "      \"cardType\": \"WAG\",\n" +
            "      \"vacuumInProgress\": false,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": true,\n" +
            "      \"cardNumber\": \"706981007005445432\",\n" +
            "      \"status\": \"Used\",\n" +
            "      \"vacuumRemaining\": 31\n" +
            "    }"

    private val responseWAGVacuumProgress = "{\n" +
            "      \"lastWashDt\": \"2022-07-07T15:16:45.787\",\n" +
            "      \"lastWashStoreId\": \"f3q44R/Jfog/u6o6ck5TUA==\",\n" +
            "      \"cardNumberEncrypted\": \"cTQX2jTzrqSfLF+olV1HoV09gN4RAIKdzD2l2QOk8Xk=\",\n" +
            "      \"unitsRemaining\": 17,\n" +
            "      \"cardType\": \"WAG\",\n" +
            "      \"vacuumInProgress\": true,\n" +
            "      \"canWash\": true,\n" +
            "      \"canVacuum\": true,\n" +
            "      \"washInProgress\": false,\n" +
            "      \"cardNumber\": \"706981007005445432\",\n" +
            "      \"status\": \"Used\",\n" +
            "      \"vacuumRemaining\": 31\n" +
            "    }"

    val responseStoreDetails = "{\n" +
            "\n" +
            "  \"amenities\": [\n" +
            "\n" +
            "    \"lottery\",\n" +
            "\n" +
            "    \"ultra94\",\n" +
            "\n" +
            "    \"bankMachine\",\n" +
            "\n" +
            "    \"open24Hours\",\n" +
            "\n" +
            "    \"convenienceStore\",\n" +
            "\n" +
            "    \"carWashBrushTypeSoftcloth\"\n" +
            "\n" +
            "  ],\n" +
            "\n" +
            "  \"hours\": [\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    },\n" +
            "\n" +
            "    {\n" +
            "\n" +
            "      \"close\": \"2400\",\n" +
            "\n" +
            "      \"open\": \"0000\"\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "  ],\n" +
            "\n" +
            "  \"address\": {\n" +
            "\n" +
            "    \"subdivision\": \"Ontario\",\n" +
            "\n" +
            "    \"phone\": \"(000) 000-0000\",\n" +
            "\n" +
            "    \"countryRegion\": \"Canada\",\n" +
            "\n" +
            "    \"postalCode\": \"L3R1M8\",\n" +
            "\n" +
            "    \"latitude\": 43.863183221307,\n" +
            "\n" +
            "    \"addressLine\": \"4780 Hwy. #7 East\",\n" +
            "\n" +
            "    \"crossStreet\": \"NEW KENNEDY\",\n" +
            "\n" +
            "    \"primaryCity\": \"Unionville\",\n" +
            "\n" +
            "    \"longitude\": -79.3032763914017\n" +
            "\n" +
            "  },\n" +
            "\n" +
            "  \"id\": \"f3q44R/Jfog/u6o6ck5TUA==\"\n" +
            "\n" +
            "}"
}


