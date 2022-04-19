package suncor.com.android.ui.main.wallet.cards

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import suncor.com.android.ui.main.wallet.cards.list.CardsViewModel
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CarWashCardViewModelTest {

    private var viewModel: CardsViewModel? = null
    private lateinit var mockCardsList: Array<CardDetail>
    private val repository = Mockito.mock(CardsRepository::class.java)
    private val sessionManager = Mockito.mock(
        SessionManager::class.java
    )

    @Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private var cardsLiveData: MutableLiveData<Resource<ArrayList<CardDetail>>>? = null

    @Before
    fun init() {
        val profile = Profile()
        profile.petroPointsNumber = "706988500050292051"
        profile.pointsBalance = 10000
        Mockito.`when`(sessionManager.profile).thenReturn(profile)
        viewModel = CardsViewModel(repository, sessionManager)
        //Add a dummy observer to viewState
        viewModel!!.viewState.observeForever { state: CardsViewModel.ViewState? -> }
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        cardsLiveData = MutableLiveData()
        cardsLiveData!!.observeForever { arrayListResource: Resource<ArrayList<CardDetail>>? -> }
    }

    @Test
    fun test_user_has_only_ppts() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[0])
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        Assert.assertNotNull(viewModel!!.petroCanadaCards.value)
        Assert.assertTrue(viewModel!!.petroCanadaCards.value!!.isEmpty())
        Assert.assertTrue(viewModel!!.partnerCards.value!!.isEmpty())
    }

    @Test
    fun test_user_has_only_petro_canada_cards() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[0])
        cardsList.add(mockCardsList[1])
        cardsList.add(mockCardsList[2])
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        Assert.assertNotNull(viewModel!!.petroCanadaCards.value)
        Assert.assertFalse(viewModel!!.petroCanadaCards.value!!.isEmpty())
        Assert.assertTrue(viewModel!!.partnerCards.value!!.isEmpty())
    }

    @Test
    fun test_user_has_only_partner_cards() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[6])
        cardsList.add(mockCardsList[7])
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        Assert.assertNotNull(viewModel!!.petroCanadaCards.value)
        Assert.assertTrue(viewModel!!.petroCanadaCards.value!!.isEmpty())
        Assert.assertFalse(viewModel!!.partnerCards.value!!.isEmpty())
    }

    @Test
    fun test_user_refresh_success() {
        val cardsList = ArrayList(Arrays.asList(*mockCardsList))
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        viewModel!!.refreshBalance()
        cardsLiveData!!.value = Resource.loading()
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel!!.viewState.value)
        cardsLiveData!!.value = Resource.success(cardsList)
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        Assert.assertNotNull(viewModel!!.petroCanadaCards.value)
        Assert.assertFalse(viewModel!!.petroCanadaCards.value!!.isEmpty())
        Assert.assertFalse(viewModel!!.partnerCards.value!!.isEmpty())
    }

    @Test
    fun test_user_refresh_balance_failed() {
        val cardsList = ArrayList(Arrays.asList(*mockCardsList))
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        viewModel!!.refreshBalance()
        cardsLiveData!!.value = Resource.loading()
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel!!.viewState.value)
        cardsLiveData!!.value = Resource.error("error", cardsList)
        Assert.assertEquals(CardsViewModel.ViewState.BALANCE_FAILED, viewModel!!.viewState.value)
    }

    @Test
    fun test_user_refresh_failed() {
        val cardsList = ArrayList(Arrays.asList(*mockCardsList))
        cardsLiveData!!.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel!!.viewState.value)
        viewModel!!.refreshBalance()
        cardsLiveData!!.value = Resource.loading()
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel!!.viewState.value)
        cardsLiveData!!.value = Resource.error("error")
        Assert.assertEquals(CardsViewModel.ViewState.FAILED, viewModel!!.viewState.value)
    }

    @Test
    fun test_retrieve_cards_failed() {
        cardsLiveData!!.value = Resource.error("error")
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        viewModel!!.onAttached()
        Assert.assertEquals(CardsViewModel.ViewState.FAILED, viewModel!!.viewState.value)
        Assert.assertEquals(
            sessionManager.profile.petroPointsNumber, viewModel!!.petroPointsCard.value!!
                .cardNumber
        )
        Assert.assertEquals(
            sessionManager.profile.pointsBalance.toLong(), viewModel!!.petroPointsCard.value!!
                .balance.toLong()
        )
        Assert.assertTrue(
            viewModel!!.petroCanadaCards.value == null || viewModel!!.petroCanadaCards.value!!
                .isEmpty()
        )
        Assert.assertTrue(
            viewModel!!.partnerCards.value == null || viewModel!!.partnerCards.value!!
                .isEmpty()
        )
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

}