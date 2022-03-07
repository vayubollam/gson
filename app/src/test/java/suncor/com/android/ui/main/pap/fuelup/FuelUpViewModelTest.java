package suncor.com.android.ui.main.pap.fuelup;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentsClient;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.pap.PayResponse;

public class FuelUpViewModelTest {

    private FuelUpViewModel viewModel;
    private PaymentsClient paymentsClient = Mockito.mock(PaymentsClient.class);
    private SettingsApi settingsApi = Mockito.mock(SettingsApi.class);
    private PapRepository papRepository = Mockito.mock(PapRepository.class);
    private PaymentsRepository paymentsRepository = Mockito.mock(PaymentsRepository.class);
    private SessionManager sessionManager = Mockito.mock(SessionManager.class);

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private MutableLiveData<Resource<ArrayList<CardDetail>>> cardsLiveData;


    @Before
    public void init() {
        Profile profile = new Profile();
        profile.setPetroPointsNumber("706934700054132011");
        profile.setPointsBalance(2500);
        Mockito.when(sessionManager.getProfile()).thenReturn(profile);
        viewModel = new FuelUpViewModel(settingsApi,papRepository,paymentsRepository, sessionManager);
    }

    @Test
    public void test_settings_api_success() {
        viewModel.getSettingResponse();

    }

    @Test
    public void test_is_ready_to_pay_request_for_google_pay_success() {
        IsReadyToPayRequest isReadyToPayRequest = viewModel.IsReadyToPayRequestForGooglePay();
        Assert.assertEquals(Boolean.TRUE, paymentsClient.isReadyToPay(isReadyToPayRequest));
    }

    @Test
    public void test_is_ready_to_pay_request_for_google_pay_fail() {
        IsReadyToPayRequest isReadyToPayRequest = viewModel.IsReadyToPayRequestForGooglePay();
        Assert.assertEquals(Boolean.FALSE, paymentsClient.isReadyToPay(isReadyToPayRequest));
    }

    @Test
    public void test_google_pay_payment_token_from_response_success() {
        PaymentData paymentData = PaymentData.fromJson(googlePayResponse);
        Assert.assertEquals(Boolean.TRUE, !viewModel.handlePaymentSuccess(paymentData).equals(null));
    }

    @Test
    public void test_google_pay_payment_token_from_response_error() {
        PaymentData paymentData = PaymentData.fromJson(googlePayResponse);
        Assert.assertEquals(Boolean.TRUE, viewModel.handlePaymentSuccess(paymentData).equals(null));
    }

    @Test
    public void test_google_pay_server_request_success() {
        LiveData<Resource<PayResponse>> response = viewModel.payByGooglePayRequest("00823", 2, 1,
                2000,"\"{\\\"signature\\\":\\\"MEQCIDqFBoyAVVUztonjXoE8AVkvLqC80OeE2pwHJ0tHHCY8AiBMgz2Vu2KjugzilpRSyKfoZHNpYle6tbkCZRZRv6sYWw\\\\u003d\\\\u003d\\\",\\\"intermediateSigningKey\\\":{\\\"signedKey\\\":\\\"{\\\\\\\"keyValue\\\\\\\":\\\\\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEugtGsZ9nV3dXvcURJc\\/jzwRMUtslhbEK+epIibmzBtPMxxafS\\/ju6TQhLmm+cnE4+Tg10Sf2devCVuPvzzjbTg\\\\\\\\u003d\\\\\\\\u003d\\\\\\\",\\\\\\\"keyExpiration\\\\\\\":\\\\\\\"1640650908813\\\\\\\"}\\\\", "kountSessionID");
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.SUCCESS);
    }

    @Test
    public void test_google_pay_server_request_error() {
        LiveData<Resource<PayResponse>> response = viewModel.payByGooglePayRequest("00823", 2, 1,
                2000,"paymentToken", "685b8b0feb524f7989e1954c71ee1068");
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.ERROR);
    }

    @Test
    public void test_pay_by_wallet_server_request_success() {
        LiveData<Resource<PayResponse>> response = viewModel.payByWalletRequest("storeId", 2, 1,
                1, 7,"kountSessionID");
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.SUCCESS);
    }

    @Test
    public void test_pay_by_wallet_server_request_error() {
        LiveData<Resource<PayResponse>> response = viewModel.payByGooglePayRequest("storeId", 2, 1,
                2000, "paymentToken","kountSessionID");
        Assert.assertEquals(Boolean.TRUE, response.getValue().status == Resource.Status.ERROR);
    }

    private String googlePayResponse = "{\"apiVersionMinor\":0,\"apiVersion\":2,\"paymentMethodData\":{\"description\":\"Visa •••• 7847\",\"tokenizationData\":{\"type\":\"PAYMENT_GATEWAY\",\"token\":\"{\\\"signature\\\":\\\"MEQCIDqFBoyAVVUztonjXoE8AVkvLqC80OeE2pwHJ0tHHCY8AiBMgz2Vu2KjugzilpRSyKfoZHNpYle6tbkCZRZRv6sYWw\\\\u003d\\\\u003d\\\",\\\"intermediateSigningKey\\\":{\\\"signedKey\\\":\\\"{\\\\\\\"keyValue\\\\\\\":\\\\\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEugtGsZ9nV3dXvcURJc\\/jzwRMUtslhbEK+epIibmzBtPMxxafS\\/ju6TQhLmm+cnE4+Tg10Sf2devCVuPvzzjbTg\\\\\\\\u003d\\\\\\\\u003d\\\\\\\",\\\\\\\"keyExpiration\\\\\\\":\\\\\\\"1640650908813\\\\\\\"}\\\",\\\"signatures\\\":[\\\"MEYCIQDYVDEn7b1LGeL4GrTkF03ONMRlV2HY+Z1NewooI0f2jwIhALFnh2qiL6GGkl00mGewvHD65hYxG3Dg9WfaeBfZxcuZ\\\"]},\\\"protocolVersion\\\":\\\"ECv2\\\",\\\"signedMessage\\\":\\\"{\\\\\\\"encryptedMessage\\\\\\\":\\\\\\\"DJrwURfwAmEubrdrD9rrt2SZkU69Z2waMkr8YTnm+VEZdmw6Th2+56Ahu+jYtvmWIuoI4USULO48M60koindzBX8LfJ0+rwdxGaleLR1XJGKABao2uRYOKw0uRPLQB5HVXLii79A82cC0FyxVBi\\/l8Scheq03dlwRWRKRhshwzDYc6iiG2skuOQ5u3UOopJAFmXJkBcqHp3312F2knowTfbvVAjfnuC6C5P4Q70ke2JLiNN\\/ipilKaKDp8qHvCeb+sHlulzo2gEnbwwMwr4ZWf3MopxCOUlSkDx\\/oI+LH6wSKQLA52yHZ4PaAHAtI7YVRdslDhnWLHSrjOpaU6DGdc8EDW3Yc\\/ET3uo5akNf69tij3vZj4FpzV31chQgThvGUFacnbPX+Lmw197ocVt7mkBGEUkVO+zHE3FqWLgyuGFTJ+STWJWekHmIIfq2XHr+05eb+ioC03vI4E6dKzPs8sMNIXOhcNrJfEHkGCCoaxef8BcXw59jabFlN9g\\/3q87rmJJxd27bPZcXQukvHvKE1CSM8StW8kmjdsz6pbctTA+8qGje\\/3rRda1XB1R6A6uCgHathpJJp\\/IAgl2eJ6vnRsfup1JMYETW+s7RDE\\\\\\\\u003d\\\\\\\",\\\\\\\"ephemeralPublicKey\\\\\\\":\\\\\\\"BI6R4XLk0o6KwV3KmsNlFTEE\\/bbnTBkN1KoOH8IwoVggtusIZ5Sz5xLRCO0NTEe0yC0hY4XWzUn+ANdoUV7H0Z0\\\\\\\\u003d\\\\\\\",\\\\\\\"tag\\\\\\\":\\\\\\\"SPO8lGM3b2o27bMCSWxxGi5HWGb0P8wp45EjcexFUQY\\\\\\\\u003d\\\\\\\"}\\\"}\"},\"type\":\"CARD\",\"info\":{\"cardNetwork\":\"VISA\",\"cardDetails\":\"7847\"}}}";

}
