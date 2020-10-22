package suncor.com.android.ui.main.wallet.cards;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.main.wallet.cards.list.CardsViewModel;

public class CardsViewModelTest {

    private CardsViewModel viewModel;
    private CardDetail[] mockCardsList;
    private CardsRepository repository = Mockito.mock(CardsRepository.class);
    private SessionManager sessionManager = Mockito.mock(SessionManager.class);

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private MutableLiveData<Resource<ArrayList<CardDetail>>> cardsLiveData;

    @Before
    public void init() {
        Profile profile = new Profile();
        profile.setPetroPointsNumber("706988500050292051");
        profile.setPointsBalance(2500);
        Mockito.when(sessionManager.getProfile()).thenReturn(profile);
        viewModel = new CardsViewModel(repository, sessionManager);
        //Add a dummy observer to viewState
        viewModel.viewState.observeForever(state -> {
        });
        mockCardsList = new Gson().fromJson(responseJson, CardDetail[].class);

        cardsLiveData = new MutableLiveData<>();
        cardsLiveData.observeForever(arrayListResource -> {
            //A dummy observer
        });
    }

    @Test
    public void test_user_has_only_ppts() {
        ArrayList<CardDetail> cardsList = new ArrayList<>();
        cardsList.add(mockCardsList[0]);
        cardsLiveData.setValue(Resource.success(cardsList));

        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();

        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());
        Assert.assertNotNull(viewModel.getPetroCanadaCards().getValue());
        Assert.assertTrue(viewModel.getPetroCanadaCards().getValue().isEmpty());
        Assert.assertTrue(viewModel.getPartnerCards().getValue().isEmpty());
    }

    @Test
    public void test_user_has_only_petro_canada_cards() {
        ArrayList<CardDetail> cardsList = new ArrayList<>();
        cardsList.add(mockCardsList[0]);
        cardsList.add(mockCardsList[1]);
        cardsList.add(mockCardsList[2]);
        cardsLiveData.setValue(Resource.success(cardsList));

        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();

        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());
        Assert.assertNotNull(viewModel.getPetroCanadaCards().getValue());
        Assert.assertFalse(viewModel.getPetroCanadaCards().getValue().isEmpty());
        Assert.assertTrue(viewModel.getPartnerCards().getValue().isEmpty());
    }

    @Test
    public void test_user_has_only_partner_cards() {

        ArrayList<CardDetail> cardsList = new ArrayList<>();
        cardsList.add(mockCardsList[6]);
        cardsList.add(mockCardsList[7]);
        cardsLiveData.setValue(Resource.success(cardsList));

        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();

        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());
        Assert.assertNotNull(viewModel.getPetroCanadaCards().getValue());
        Assert.assertTrue(viewModel.getPetroCanadaCards().getValue().isEmpty());
        Assert.assertFalse(viewModel.getPartnerCards().getValue().isEmpty());
    }

    @Test
    public void test_user_refresh_success() {
        ArrayList<CardDetail> cardsList = new ArrayList<>(Arrays.asList(mockCardsList));
        cardsLiveData.setValue(Resource.success(cardsList));
        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());

        viewModel.refreshBalance();
        cardsLiveData.setValue(Resource.loading());
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel.viewState.getValue());

        cardsLiveData.setValue(Resource.success(cardsList));
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());
        Assert.assertNotNull(viewModel.getPetroCanadaCards().getValue());
        Assert.assertFalse(viewModel.getPetroCanadaCards().getValue().isEmpty());
        Assert.assertFalse(viewModel.getPartnerCards().getValue().isEmpty());
    }

    @Test
    public void test_user_refresh_balance_failed() {
        ArrayList<CardDetail> cardsList = new ArrayList<>(Arrays.asList(mockCardsList));
        cardsLiveData.setValue(Resource.success(cardsList));
        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());

        viewModel.refreshBalance();
        cardsLiveData.setValue(Resource.loading());
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel.viewState.getValue());

        cardsLiveData.setValue(Resource.error("error", cardsList));
        Assert.assertEquals(CardsViewModel.ViewState.BALANCE_FAILED, viewModel.viewState.getValue());
    }

    @Test
    public void test_user_refresh_failed() {
        ArrayList<CardDetail> cardsList = new ArrayList<>(Arrays.asList(mockCardsList));
        cardsLiveData.setValue(Resource.success(cardsList));
        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();
        Assert.assertEquals(CardsViewModel.ViewState.SUCCESS, viewModel.viewState.getValue());

        viewModel.refreshBalance();
        cardsLiveData.setValue(Resource.loading());
        Assert.assertEquals(CardsViewModel.ViewState.REFRESHING, viewModel.viewState.getValue());

        cardsLiveData.setValue(Resource.error("error"));
        Assert.assertEquals(CardsViewModel.ViewState.FAILED, viewModel.viewState.getValue());
    }

    @Test
    public void test_retrieve_cards_failed() {
        cardsLiveData.setValue(Resource.error("error"));
        Mockito.when(repository.getCards(false)).thenReturn(cardsLiveData);

        viewModel.onAttached();
        Assert.assertEquals(CardsViewModel.ViewState.FAILED, viewModel.viewState.getValue());
        Assert.assertEquals(sessionManager.getProfile().getPetroPointsNumber(), viewModel.getPetroPointsCard().getValue().getCardNumber());
        Assert.assertEquals(sessionManager.getProfile().getPointsBalance(), viewModel.getPetroPointsCard().getValue().getBalance());
        Assert.assertTrue(viewModel.getPetroCanadaCards().getValue() == null || viewModel.getPetroCanadaCards().getValue().isEmpty());
        Assert.assertTrue(viewModel.getPartnerCards().getValue() == null || viewModel.getPartnerCards().getValue().isEmpty());
    }

    private String responseJson = "[{\n" +
            "                        \"cardType\": \"PPTS\",\n" +
            "                        \"cardNumber\": \"706988500050292051\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"11LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"pointsBalance\": 12345\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"FSR\",\n" +
            "                        \"cardNumber\": \"706988500050292051\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"litresRemaining\": 12,\n" +
            "                        \"cpl\": 0.05\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"FSR\",\n" +
            "                        \"cardNumber\": \"706988500050292052\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"litresRemaining\": 12,\n" +
            "                        \"cpl\": 0.1\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"PPC\",\n" +
            "                        \"cardNumber\": \"706999700401020280\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"56PawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"litresRemaining\": 12,\n" +
            "                        \"cpl\": 0.10\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"WAG\",\n" +
            "                        \"cardNumber\": \"706981001000120561\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"11LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"unitsRemaining\": 12\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"SP\",\n" +
            "                        \"cardNumber\": \"706981003009633263\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\",\n" +
            "                        \"daysRemaining\": 12\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"MORE\",\n" +
            "                        \"cardNumber\": \"60476748007905331\",\n" +
            "                        \"cardNumberEncrypted\": \"198GbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"CAA\",\n" +
            "                        \"cardNumber\": \"6202909505788005\",\n" +
            "                        \"cardNumberEncrypted\": \"94iIbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"HBC\",\n" +
            "                        \"cardNumber\": \"600294472306670\",\n" +
            "                        \"cardNumberEncrypted\": \"33oPCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"24HuwF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"RBC\"\n" +
            "                    }]";
}
