package suncor.com.android.ui.enrollment.form

import androidx.lifecycle.MutableLiveData
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import suncor.com.android.R
import suncor.com.android.data.account.EnrollmentsApi
import suncor.com.android.model.Resource
import suncor.com.android.ui.enrollment.cardform.CardFormViewModel

class CardFormViewModelTest {
    private lateinit var viemodel: CardFormViewModel
    private val api = Mockito.mock(EnrollmentsApi::class.java)

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
        val apiResponse = MutableLiveData<Resource<Boolean>>()
        val dummyObserver =
            androidx.lifecycle.Observer { event: Resource<Boolean> -> }
        apiResponse.observeForever(dummyObserver)

        Assert.assertEquals(true,api.checkCardStatus("7069 43443 4324 434","B1C 2B3","bob").value?.status)

    }

}