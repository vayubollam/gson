package suncor.com.android.ui.main.pap.fuelup

import suncor.com.android.ui.main.pap.fuelup.FuelUpViewModel
import com.google.android.gms.wallet.PaymentsClient
import org.mockito.Mockito
import suncor.com.android.data.pap.PapRepository
import suncor.com.android.data.payments.PaymentsRepository
import org.junit.rules.TestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import suncor.com.android.model.cards.CardDetail
import org.junit.Before
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import suncor.com.android.data.settings.SettingsApi
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.Resource
import suncor.com.android.model.account.Profile
import suncor.com.android.model.pap.PayResponse
import suncor.com.android.ui.common.cards.CardFormatUtils
import java.lang.Boolean
import java.util.ArrayList

class FuelUpViewModelTest {
    private var viewModel: FuelUpViewModel? = null
    private val paymentsClient = Mockito.mock(PaymentsClient::class.java)
    private val settingsApi = Mockito.mock(
        SettingsApi::class.java
    )
    private val papRepository = Mockito.mock(PapRepository::class.java)
    private val paymentsRepository = Mockito.mock(
        PaymentsRepository::class.java
    )
    private val sessionManager = Mockito.mock(
        SessionManager::class.java
    )
    val profile = Profile()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val cardsLiveData: MutableLiveData<Resource<ArrayList<CardDetail>>>? = null

    @Before
    fun init() {

        profile.petroPointsNumber = "706934700054132011"
        profile.pointsBalance = 2500
        Mockito.`when`(sessionManager.profile).thenReturn(profile)
        viewModel = FuelUpViewModel(settingsApi, papRepository, paymentsRepository, sessionManager)
    }

    @Test
    fun test_settings_api_success() {
        viewModel?.settingResponse
    }


    @Test
    fun test_google_pay_payment_token_from_response_success() {
        val paymentData = PaymentData.fromJson(googlePayResponse)
        Assert.assertNotNull(paymentData)
        Assert.assertTrue(paymentData.toJson().toString(), true)
    }

    @Test
    fun test_google_pay_payment_token_from_response_error() {
        val paymentData = PaymentData.fromJson(googlePayResponse)
        Assert.assertNotNull(paymentData)
        Assert.assertFalse(paymentData.toJson().toString(), false)
    }

    @Test
    fun test_google_pay_server_request_success() {
        val response = viewModel?.payByGooglePayRequest(
            "00823",
            2,
            1.0,
            "\"{\\\"signature\\\":\\\"MEQCIDqFBoyAVVUztonjXoE8AVkvLqC80OeE2pwHJ0tHHCY8AiBMgz2Vu2KjugzilpRSyKfoZHNpYle6tbkCZRZRv6sYWw\\\\u003d\\\\u003d\\\",\\\"intermediateSigningKey\\\":{\\\"signedKey\\\":\\\"{\\\\\\\"keyValue\\\\\\\":\\\\\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEugtGsZ9nV3dXvcURJc\\/jzwRMUtslhbEK+epIibmzBtPMxxafS\\/ju6TQhLmm+cnE4+Tg10Sf2devCVuPvzzjbTg\\\\\\\\u003d\\\\\\\\u003d\\\\\\\",\\\\\\\"keyExpiration\\\\\\\":\\\\\\\"1640650908813\\\\\\\"}\\\\",
            "kountSessionID"
        )
        Assert.assertTrue(response?.value?.toString(), true)
    }

    @Test
    fun test_google_pay_server_request_error() {
        val response = viewModel?.payByGooglePayRequest(
            "00823", 2, 1.0,
            "paymentToken", "685b8b0feb524f7989e1954c71ee1068"
        )
        Assert.assertFalse(response?.value?.toString(), false)
    }

    @Test
    fun test_pay_by_wallet_server_request_success() {
        val response = viewModel?.payByWalletRequest(
            "storeId", 2, 1.0,
            1, "kountSessionID"
        )
        Assert.assertTrue(response?.value?.toString(), true)
    }

    @Test
    fun test_pay_by_wallet_server_request_error() {
        val response = viewModel?.payByGooglePayRequest(
            "storeId", 2, 1.0,
            "paymentToken", "kountSessionID"
        )

        Assert.assertFalse(response?.value?.toString(), false)

    }

    @Test
    fun test_get_petro_points_balance() {
        Assert.assertNotNull(CardFormatUtils.formatBalance(profile.pointsBalance))
    }

    private val googlePayResponse =
        "{\"apiVersionMinor\":0,\"apiVersion\":2,\"paymentMethodData\":{\"description\":\"Visa •••• 7847\",\"tokenizationData\":{\"type\":\"PAYMENT_GATEWAY\",\"token\":\"{\\\"signature\\\":\\\"MEQCIDqFBoyAVVUztonjXoE8AVkvLqC80OeE2pwHJ0tHHCY8AiBMgz2Vu2KjugzilpRSyKfoZHNpYle6tbkCZRZRv6sYWw\\\\u003d\\\\u003d\\\",\\\"intermediateSigningKey\\\":{\\\"signedKey\\\":\\\"{\\\\\\\"keyValue\\\\\\\":\\\\\\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEugtGsZ9nV3dXvcURJc\\/jzwRMUtslhbEK+epIibmzBtPMxxafS\\/ju6TQhLmm+cnE4+Tg10Sf2devCVuPvzzjbTg\\\\\\\\u003d\\\\\\\\u003d\\\\\\\",\\\\\\\"keyExpiration\\\\\\\":\\\\\\\"1640650908813\\\\\\\"}\\\",\\\"signatures\\\":[\\\"MEYCIQDYVDEn7b1LGeL4GrTkF03ONMRlV2HY+Z1NewooI0f2jwIhALFnh2qiL6GGkl00mGewvHD65hYxG3Dg9WfaeBfZxcuZ\\\"]},\\\"protocolVersion\\\":\\\"ECv2\\\",\\\"signedMessage\\\":\\\"{\\\\\\\"encryptedMessage\\\\\\\":\\\\\\\"DJrwURfwAmEubrdrD9rrt2SZkU69Z2waMkr8YTnm+VEZdmw6Th2+56Ahu+jYtvmWIuoI4USULO48M60koindzBX8LfJ0+rwdxGaleLR1XJGKABao2uRYOKw0uRPLQB5HVXLii79A82cC0FyxVBi\\/l8Scheq03dlwRWRKRhshwzDYc6iiG2skuOQ5u3UOopJAFmXJkBcqHp3312F2knowTfbvVAjfnuC6C5P4Q70ke2JLiNN\\/ipilKaKDp8qHvCeb+sHlulzo2gEnbwwMwr4ZWf3MopxCOUlSkDx\\/oI+LH6wSKQLA52yHZ4PaAHAtI7YVRdslDhnWLHSrjOpaU6DGdc8EDW3Yc\\/ET3uo5akNf69tij3vZj4FpzV31chQgThvGUFacnbPX+Lmw197ocVt7mkBGEUkVO+zHE3FqWLgyuGFTJ+STWJWekHmIIfq2XHr+05eb+ioC03vI4E6dKzPs8sMNIXOhcNrJfEHkGCCoaxef8BcXw59jabFlN9g\\/3q87rmJJxd27bPZcXQukvHvKE1CSM8StW8kmjdsz6pbctTA+8qGje\\/3rRda1XB1R6A6uCgHathpJJp\\/IAgl2eJ6vnRsfup1JMYETW+s7RDE\\\\\\\\u003d\\\\\\\",\\\\\\\"ephemeralPublicKey\\\\\\\":\\\\\\\"BI6R4XLk0o6KwV3KmsNlFTEE\\/bbnTBkN1KoOH8IwoVggtusIZ5Sz5xLRCO0NTEe0yC0hY4XWzUn+ANdoUV7H0Z0\\\\\\\\u003d\\\\\\\",\\\\\\\"tag\\\\\\\":\\\\\\\"SPO8lGM3b2o27bMCSWxxGi5HWGb0P8wp45EjcexFUQY\\\\\\\\u003d\\\\\\\"}\\\"}\"},\"type\":\"CARD\",\"info\":{\"cardNetwork\":\"VISA\",\"cardDetails\":\"7847\"}}}"
}