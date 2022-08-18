package suncor.com.android.ui.main.wallet.cards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import suncor.com.android.data.cards.CardsRepository
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.Profile
import suncor.com.android.model.cards.CardDetail
import suncor.com.android.model.cards.CardType
import suncor.com.android.model.redeem.response.Card
import suncor.com.android.ui.main.wallet.cards.CardsLoadType
import suncor.com.android.ui.main.wallet.cards.list.CardsViewModel
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CardsViewModelTest {

    private var viewModel: CardsViewModel? = null
    private lateinit var mockCardsList: Array<CardDetail>
    private val repository = Mockito.mock(CardsRepository::class.java)
    private val sessionManager = Mockito.mock(
        SessionManager::class.java
    )
    var profile = Profile()
    private var cardsLiveData: MutableLiveData<Resource<ArrayList<CardDetail>>>? = null

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    @Before
    fun init() {
        viewModel = CardsViewModel(repository, sessionManager)
        profile.petroPointsNumber = "706988500050292051"
        profile.pointsBalance = 10000

        //Add a dummy observer to viewState
        viewModel!!.viewState.observeForever { state: CardsViewModel.ViewState? -> }
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        cardsLiveData = MutableLiveData()
        cardsLiveData!!.observeForever { arrayListResource: Resource<ArrayList<CardDetail>>? -> }
    }


    @Test
    fun test_profile_availabl_for_load() {
        Assert.assertNotNull(profile.petroPointsNumber)
        Assert.assertNotNull(profile.pointsBalance)

    }

    @Test
    fun test_zero_cardBalance() {
        var isBalanceZero = true
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        val card: CardDetail = mockCardsList[0]
        val apiResponse = MutableLiveData<Resource<CardDetail>>()
        apiResponse.postValue(Resource.success(card))

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
        var loadType = CardType.PPTS
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        val card: CardDetail = mockCardsList[0]
        Assert.assertNotNull(card)
        Assert.assertEquals(loadType, card.cardType)
    }

    @Test
    fun test_user_has_only_petro_canada_cards() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[4])
        cardsList.add(mockCardsList[5])
        for (card in cardsList) {
            if (card != null) Assert.assertNotNull(card)
            if (isPetroCanadaCard(card.cardType)) {
                Assert.assertTrue("Is petro canada card", true)
            } else {
                Assert.assertFalse("Is petro canada card", false)
            }
        }
    }

    @Test
    fun test_user_has_only_partner_cards() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[6])
        cardsList.add(mockCardsList[7])
        for (card in cardsList) {
            if (card != null) Assert.assertNotNull(card)
            if (isPartnerCard(card.cardType)) {
                Assert.assertTrue("Is Partner Card", true)
            } else {
                Assert.assertFalse("Not a Partner Card", false)
            }
        }
    }

    @Test
    fun test_getTimeUpdate() {
       viewModel?.dateOfUpdate
    }


    private val responseJson = """[{
                        "cardType": "PPTS",
                        "cardNumber": "706988500050292051",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "11LawF1v1Or4s1D4s0kJEA==",
                        "pointsBalance": 12345
                    },
                    {
                        "cardType": "FSR",
                        "cardNumber": "706988500050292051",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "25LawF1v1Or4s1D4s0kJEA==",
                        "litresRemaining": 12,
                        "cpl": 0.05
                    },
                    {
                        "cardType": "FSR",
                        "cardNumber": "706988500050292052",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "25LawF1v1Or4s1D4s0kJEA==",
                        "litresRemaining": 12,
                        "cpl": 0.1
                    },
                    {
                        "cardType": "PPC",
                        "cardNumber": "706999700401020280",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "56PawF1v1Or4s1D4s0kJEA==",
                        "litresRemaining": 12,
                        "cpl": 0.10
                    },
                    {
                        "cardType": "WAG",
                        "cardNumber": "706981001000120561",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "11LawF1v1Or4s1D4s0kJEA==",
                        "unitsRemaining": 12
                    },
                    {
                        "cardType": "SP",
                        "cardNumber": "706981003009633263",
                        "cardNumberEncrypted": "25AbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "25LawF1v1er4s1D4s0kJEA==",
                        "daysRemaining": 12
                    },
                    {
                        "cardType": "MORE",
                        "cardNumber": "60476748007905331",
                        "cardNumberEncrypted": "198GbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "25LawF1v1er4s1D4s0kJEA=="
                    },
                    {
                        "cardType": "CAA",
                        "cardNumber": "6202909505788005",
                        "cardNumberEncrypted": "94iIbCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "25LawF1v1er4s1D4s0kJEA=="
                    },
                    {
                        "cardType": "HBC",
                        "cardNumber": "600294472306670",
                        "cardNumberEncrypted": "33oPCF1v1Or4s1D4s0kJEA==",
                        "serviceId": "24HuwF1v1er4s1D4s0kJEA=="
                    },
                    {
                        "cardType": "RBC"
                    }]"""

    fun isPetroCanadaCard(cardType: CardType): Boolean {
        return if (cardType.equals(CardType.WAG) || cardType.equals(CardType.SP)) {
            true
        } else {
            false
        }
    }
    fun isPartnerCard(cardType: CardType): Boolean {
        return if (cardType.equals(CardType.RBC) || cardType.equals(CardType.HBC)) {
            true
        } else {
            false
        }
    }
}