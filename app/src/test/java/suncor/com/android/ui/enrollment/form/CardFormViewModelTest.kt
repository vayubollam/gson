package suncor.com.android.ui.enrollment.form

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
import suncor.com.android.R
import suncor.com.android.data.account.EnrollmentsApi
import suncor.com.android.model.Resource
import suncor.com.android.model.account.CardStatus
import suncor.com.android.model.cards.CardDetail
import suncor.com.android.ui.enrollment.cardform.CardFormViewModel

class CardFormViewModelTest {
    private lateinit var viemodel: CardFormViewModel
    private val api = Mockito.mock(EnrollmentsApi::class.java)

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        viemodel = CardFormViewModel(api)
    }



    @Test
    fun testCardFormJoinEmpty() {
        viemodel.validateAndContinue()
        Assert.assertTrue(viemodel.cardNumberField.showError)
        Assert.assertEquals(
            R.string.enrollment_cardform_card_error,
            viemodel.cardNumberField.error
        )
        Assert.assertTrue(viemodel.postalCodeField.showError)
        Assert.assertEquals(
            R.string.enrollment_cardform_postalcode_error,
            viemodel.postalCodeField.error
        )
        Assert.assertTrue(viemodel.lastNameField.showError)
        Assert.assertEquals(
            R.string.enrollment_cardform_lastname_error,
            viemodel.lastNameField.error
        )
    }

    @Test
    fun testCardFormInvalidCardNumber() {
        viemodel.cardNumberField.setHasFocus(true)
        viemodel.cardNumberField.text = "1234 43443 4324 434"
        viemodel.cardNumberField.setHasFocus(false)
        Assert.assertTrue(viemodel.cardNumberField.showError)
        Assert.assertEquals(
            R.string.enrollment_cardform_card_format_error,
            viemodel.cardNumberField.error
        )
    }

    @Test
    fun testCardFormValidCardNumber() {
        viemodel.cardNumberField.setHasFocus(true)
        viemodel.cardNumberField.text = "7069 43443 4324 434"
        viemodel.cardNumberField.setHasFocus(false)
        Assert.assertFalse(viemodel.cardNumberField.showError)
    }

    @Test
    fun testCardFormInvalidPostalCode() {
        viemodel.postalCodeField.setHasFocus(true)
        viemodel.postalCodeField.text = "ABCDEF"
        viemodel.postalCodeField.setHasFocus(false)
        Assert.assertTrue(viemodel.postalCodeField.showError)
        Assert.assertEquals(
            R.string.enrollment_cardform_postalcode_format_error,
            viemodel.postalCodeField.error
        )
    }

    @Test
    fun testCardFormValidPostalNumber() {
        viemodel.postalCodeField.setHasFocus(true)
        viemodel.postalCodeField.text = "B1C 2B3"
        viemodel.postalCodeField.setHasFocus(false)
        Assert.assertFalse(viemodel.cardNumberField.showError)
    }

    @Test
    fun testVerifyCardApi(){
        val card: CardStatus= Gson().fromJson(cardStatusResponse, CardStatus::class.java)
        val apiResponse = MutableLiveData<Resource<CardStatus>>()
        apiResponse.postValue(Resource.success(card))
        Mockito.`when`<LiveData<Resource<CardStatus>>>(
            api.checkCardStatus("7069198032781011","R0L1P0","LNAME")
        ).thenReturn(apiResponse)

        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<CardStatus> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertNotNull(apiResponse.value?.data)

    }


    private val cardStatusResponse="{\"userInfo\":{\"firstName\":\"FNAME\",\"lastName\":\"LNAME\",\"email\":\"\"},\"address\":{\"province\":\"MB\",\"streetAddress\":\"999, ADDRESS 1\",\"city\":\"HALIFAX\",\"phone\":\"\",\"postalCode\":\"R0L1P0\"},\"cardType\":\"existing\",\"status\":1}"

}