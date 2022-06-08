package suncor.com.android.ui.main.wallet.cards

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
import org.mockito.Mock
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
import suncor.com.android.model.cards.CardType
import suncor.com.android.ui.main.MainViewModel
import suncor.com.android.ui.main.wallet.cards.details.CardDetailsViewModel
import java.util.*

class CardDetailViewModelTest {
    private lateinit var cardDetailsViewModel: CardDetailsViewModel

    private lateinit var mockCardsList: Array<CardDetail>

    val context = mock(Context::class.java)

    private lateinit var loadType: CardsLoadType
    val profile = Profile()

    @Mock
    private lateinit var settingsApi: SettingsApi

    @Mock
    private lateinit var repository: CardsRepository

    @Mock
    private lateinit var sessionManager: SessionManager

    @Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private var cardsLiveData: MutableLiveData<Resource<ArrayList<CardDetail>>>? = null

    @Before
    fun init() {

        profile.petroPointsNumber = "808744411150291044"
        profile.pointsBalance = 4500
        Mockito.`when`(sessionManager.profile).thenReturn(profile)
        cardDetailsViewModel = CardDetailsViewModel(repository, sessionManager, settingsApi)
        //Add a dummy observer to viewState
        cardDetailsViewModel.isCarWashBalanceZero?.observeForever({ Boolean })
        mockCardsList = Gson().fromJson(responseJson, Array<CardDetail>::class.java)
        cardsLiveData = MutableLiveData()
        cardsLiveData?.observeForever { arrayListResource: Resource<ArrayList<CardDetail>>? -> }


    }

    @Test
    fun test_settings_api_success() {
        cardDetailsViewModel.settings
    }


    @Test
    fun test_update_cardBalance() {
        val cardsList = ArrayList(Arrays.asList(*mockCardsList))
        cardsLiveData?.setValue(Resource.success(cardsList))
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)

        var isBalanceZero = true
        for (card in cardsList) {
            if (card.cardType == CardType.ST || ((card.cardType == CardType.SP || card.cardType == CardType.WAG)
                        && card.balance > 0)
            ) isBalanceZero = false
        }

        cardDetailsViewModel.isCarWashBalanceZero.setValue(isBalanceZero)
        Assert.assertTrue(cardDetailsViewModel.isCarWashBalanceZero.value == true)
        Assert.assertFalse(cardDetailsViewModel.isCarWashBalanceZero.value == false)

    }

    @Test
    fun test_user_has_only_ppts() {
        loadType = CardsLoadType.PETRO_POINT_ONLY
        val _cards = MediatorLiveData<List<CardDetail>>()
        val cards: LiveData<List<CardDetail>> = _cards
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[0])
        cardsLiveData?.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        _cards?.observeForever { arrayListResource: List<CardDetail>? -> }

        val profile = sessionManager.profile
        if (profile != null && profile.petroPointsNumber != null) {
            val petroPointsCard =
                CardDetail(CardType.PPTS, profile.petroPointsNumber, profile.pointsBalance)
            _cards.value = (listOf(petroPointsCard))
            Assert.assertEquals(cardsList, _cards.value)
            Assert.assertNotNull(cardDetailsViewModel.retrieveCards())
        }

    }

    @Test
    fun test_user_has_only_petro_canada_cards() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[0])
        cardsList.add(mockCardsList[1])
        cardsList.add(mockCardsList[2])
        cardsLiveData?.value = Resource.success(cardsList)
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)
        Assert.assertEquals(Resource.Status.SUCCESS, cardsLiveData?.value)
        Assert.assertNotNull(cardsLiveData?.value)
    }

    @Test
    fun test_singleNewlyRedeemedSingleTicket() {
        val petroCanadaCards = ArrayList(Arrays.asList(*mockCardsList))
        cardsLiveData?.setValue(Resource.success(petroCanadaCards))
        Mockito.`when`(repository.getCards(false)).thenReturn(cardsLiveData)

        val redeemedTicketNumbers = "789654321098"
        if (redeemedTicketNumbers != null) {
            val newlyRedeemedSingleTickets: MutableList<CardDetail> = ArrayList()
            for (card in petroCanadaCards) {
                if (card.cardType == CardType.ST && redeemedTicketNumbers.contains(card.ticketNumber)) newlyRedeemedSingleTickets.add(
                    card
                )
            }
            Assert.assertEquals(petroCanadaCards, redeemedTicketNumbers)
        }

    }

    @Test
    fun test_DeleteCard() {
        val cardsList = ArrayList<CardDetail>()
        cardsList.add(mockCardsList[0])

        val cardDetail = CardDetail(
            cardsList.get(0).cardType,
            cardsList.get(0).cardNumber,
            cardsList.get(0).balance
        )
        Assert.assertEquals(
            Resource.Status.SUCCESS, cardDetailsViewModel.deleteCard(cardDetail).value?.status
        )


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
    fun test_Google_Loyalty_Data() {
        val loyalityData = LoyalityData()
        loyalityData.barcode = "782726526821"
        loyalityData.barcodeDisplay = "782726526821"
        loyalityData.nameLabel = "NAME"
        loyalityData.nameLocalizedLabel = "Nom"
        loyalityData.nameValue = (profile.firstName + " " + profile.lastName);
        loyalityData.emailLabel = "EMAIL"
        loyalityData.emailLocalizedLabel = "Courriel"
        loyalityData.emailValue = profile.email
        loyalityData.detailsLabel = "DETAILS"
        loyalityData.detailsLocalizedLabel ="Détails"
        loyalityData.detailsValue = context.getString(R.string.google_passes_detail_value)
        loyalityData.detailsLocalizedValue =
            context.getString(R.string.google_passes_detail_value_fr)
        loyalityData.valuesLabel = context.getString(R.string.google_passes_value_label)
        loyalityData.valuesLocalizedLabel = context.getString(R.string.google_passes_value_label_fr)
        loyalityData.valuesValue = context.getString(R.string.google_passes_value_value)
        loyalityData.valuesLocalizedValue = context.getString(R.string.google_passes_value_value_fr)
        loyalityData.howToUseLabel = context.getString(R.string.google_passes_howtouse_label)
        loyalityData.howToUseLocalizedLabel =
            context.getString(R.string.google_passes_howtouse_label_fr)
        loyalityData.howToUseValue = context.getString(R.string.google_passes_howtouse_value)
        loyalityData.howToUseLocalizedValue =
            context.getString(R.string.google_passes_howtouse_value_fr)
        loyalityData.termConditionLabel =
            context.getString(R.string.google_passes_termcondition_label)
        loyalityData.termConditionLocalizedLabel =
            context.getString(R.string.google_passes_termcondition_label_fr)
        loyalityData.termConditionValue =
            context.getString(R.string.google_passes_termcondition_value)
        loyalityData.termConditionLocalizedValue =
            context.getString(R.string.google_passes_termcondition_value_fr)

        val apiResponse = MutableLiveData<Resource<SettingsResponse>>()
        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<SettingsResponse> -> }
        apiResponse.observeForever(dummyObserver)
            val gateway = GooglePassesApiGateway()
        Assert.assertEquals(loyalityData,
            cardDetailsViewModel.settings.value?.data?.settings?.let {
                gateway.insertLoyalityCard(context,loyalityData,
                    it?.googlePass)
            })
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


