package suncor.com.android.ui.main.carwash.singleticket

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.redeem.OrderApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.account.Profile
import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct
import suncor.com.android.ui.common.Event
import java.util.*


@RunWith(MockitoJUnitRunner::class)
class SingleTicketViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var sessionManager: SessionManager

    @Mock
    private lateinit var singleTicketViewModel: SingleTicketViewModel

    @Mock
    private lateinit var singleTicketRedeemReader: SingleTicketRedeemReader

    private lateinit var mockCardsList: Array<PetroCanadaProduct>

    @Mock
    private lateinit var orderApi: OrderApi

    private val CARD_TYPE = "petropoints"

    val REDEEM_TRANSACTION_QUANTITY = 1

    private var ticketItems: List<PetroCanadaProduct>? = null
    private val isAnyTicketReedeemable = MutableLiveData<Boolean>()
    private val redeem = MutableLiveData<Event<Boolean>>()

    @Before
    fun setUp() {
        val profile = Profile()
        profile.petroPointsNumber = "706988500050292051"
        profile.pointsBalance = 10000
        Mockito.`when`(sessionManager.profile).thenReturn(profile)

        singleTicketViewModel =
            SingleTicketViewModel(sessionManager, singleTicketRedeemReader, orderApi)

        mockCardsList = Gson().fromJson(
            responseJson,
            Array<PetroCanadaProduct>::class.java
        )
        val singleTicketRedeemsList = singleTicketRedeemReader.singleTicketRedeemsList
        ticketItems = Arrays.asList(*singleTicketRedeemsList)
    }


    @Test
    fun testupdateAnyTicketRedeemable() {
        var isAnyRedeemable = false
        for (ticketItem in mockCardsList) {
            if (ticketItem.pointsPrice <= 10000) {
                isAnyRedeemable = true
                break
            }
        }

        Assert.assertTrue(isAnyRedeemable)
        Assert.assertFalse(!isAnyRedeemable)
    }

    @Test
    fun testIsLinkedToAccount() {
        var isChecked = true

        if (singleTicketViewModel.isLinkedToAccount) {
            Assert.assertTrue("isLinkedAccount", singleTicketViewModel.isLinkedToAccount)
        } else {
            Assert.assertFalse("isLinkedAccount", singleTicketViewModel.isLinkedToAccount)
        }
    }

    @Test
    fun testSendRedeemData() {
        redeem.observeForever {

        }
        Assert.assertEquals(true,singleTicketViewModel.sendRedeemData())

    }

    private val responseJson = """[
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 10000,
    "rewardId": 1,
    "quantity": 1,
    "units": 1
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 20000,
    "rewardId": 1,
    "quantity": 1,
    "units": 2
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 30000,
    "rewardId": 1,
    "quantity": 1,
    "units": 3
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 40000,
    "rewardId": 1,
    "quantity": 1,
    "units": 4
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 50000,
    "rewardId": 1,
    "quantity": 1,
    "units": 5
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 60000,
    "rewardId": 1,
    "quantity": 1,
    "units": 6
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 70000,
    "rewardId": 1,
    "quantity": 1,
    "units": 7
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 80000,
    "rewardId": 1,
    "quantity": 1,
    "units": 8
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 90000,
    "rewardId": 1,
    "quantity": 1,
    "units": 9
  },
  {
    "category": "ticket",
    "subtype": "one",
    "sku": "1234567890",
    "title": "single ticket",
    "pointsPrice": 100000,
    "rewardId": 1,
    "quantity": 1,
    "units": 10
  }
]"""
}
