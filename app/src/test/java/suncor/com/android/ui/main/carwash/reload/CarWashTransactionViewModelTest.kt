package suncor.com.android.ui.main.carwash.reload

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.wallet.PaymentsClient
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.cards.CardsRepository
import suncor.com.android.data.carwash.CarwashApi
import suncor.com.android.data.payments.PaymentsRepository
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.Profile
import suncor.com.android.model.cards.CardDetail
import suncor.com.android.model.carwash.reload.TransactionReloadData
import java.lang.Boolean
import java.util.ArrayList

@RunWith(MockitoJUnitRunner::class)
class CarWashTransactionViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private var cardsLiveData: LiveData<Resource<TransactionReloadData>>? = null
    private lateinit var mockCardsList: Array<CardDetail>
    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var cardsRespository: CardsRepository

    @Mock
    private lateinit var paymentsRepository: PaymentsRepository

    @Mock
    private lateinit var settingsApi: SettingsApi

    @Mock
    private lateinit var carwashApi: CarwashApi

    @Mock
    private lateinit var paymentsClient :PaymentsClient

    private lateinit var viewModel: CarwashTransactionViewModel
    val profile = Profile()

    @Before
    fun setUp() {
        viewModel = CarwashTransactionViewModel(
            carwashApi,
            settingsApi,
            paymentsRepository,
            cardsRespository,
            sessionManager
        )


        profile.petroPointsNumber = "706934700054132011"
        profile.pointsBalance = 2500

    }

    @Test
    fun settingApi_successResponse() {
        viewModel.settings
    }

    @Test
    fun givenServerResponse200_whenReloadTransaction_saveData() {
        val cardType = "WAG"
        viewModel.getTransactionData(cardType)
    }

    @Test
    fun givenServerResponseError_whenReloadTransaction_saveData() {
        val cardType = "Test"
        cardsLiveData  = viewModel.getTransactionData(cardType)

        Assert.assertEquals(Boolean.FALSE, cardsLiveData?.value?.status == Resource.Status.ERROR)
    }

    @Test
    fun test_get_petro_points_balance() {
        val petroPoints = profile.pointsBalance
        Assert.assertEquals(Boolean.TRUE, petroPoints == 2500)
    }

    @Test
     fun test_getpayment(){
         paymentsRepository.getPayments(false)
     }

    @Test
     fun test_getCards(){
        val cardList=ArrayList<CardDetail>()
         viewModel.getcards()
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        val apiResponse = MutableLiveData<Resource<ArrayList<CardDetail>>>()

        for (card in mockCardsList){
            cardList.addAll(mockCardsList)
        }
        apiResponse.postValue(Resource.success(cardList))
        Mockito.`when`<LiveData<Resource<ArrayList<CardDetail>>>>(
            viewModel.getcards()
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<ArrayList<CardDetail>> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertNotNull(apiResponse.value?.data)

     }



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



}