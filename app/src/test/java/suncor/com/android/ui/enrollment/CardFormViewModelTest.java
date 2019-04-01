package suncor.com.android.ui.enrollment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import suncor.com.android.R;
import suncor.com.android.ui.enrollment.cardform.CardFormViewModel;

public class CardFormViewModelTest {

    private CardFormViewModel viemodel;

    @Before
    public void init() {
        viemodel = new CardFormViewModel();
    }

    @Test
    public void testCardFormJoinEmpty() {
        viemodel.validateAndContinue();

        Assert.assertTrue(viemodel.getCardNumberField().getShowError());
        Assert.assertEquals(R.string.enrollment_cardform_card_error, viemodel.getCardNumberField().getError());
        Assert.assertTrue(viemodel.getPostalCodeField().getShowError());
        Assert.assertEquals(R.string.enrollment_cardform_postalcode_error, viemodel.getPostalCodeField().getError());
        Assert.assertTrue(viemodel.getLastNameField().getShowError());
        Assert.assertEquals(R.string.enrollment_cardform_lastname_error, viemodel.getLastNameField().getError());
    }

    @Test
    public void testCardFormInvalidCardNumber() {
        viemodel.getCardNumberField().setHasFocus(true);
        viemodel.getCardNumberField().setText("1234 43443 4324 434");
        viemodel.getCardNumberField().setHasFocus(false);

        Assert.assertTrue(viemodel.getCardNumberField().getShowError());
        Assert.assertEquals(R.string.enrollment_cardform_card_format_error, viemodel.getCardNumberField().getError());
    }

    @Test
    public void testCardFormValidCardNumber() {
        viemodel.getCardNumberField().setHasFocus(true);
        viemodel.getCardNumberField().setText("7069 43443 4324 434");
        viemodel.getCardNumberField().setHasFocus(false);

        Assert.assertFalse(viemodel.getCardNumberField().getShowError());
    }

    @Test
    public void testCardFormInvalidPostalCode() {
        viemodel.getPostalCodeField().setHasFocus(true);
        viemodel.getPostalCodeField().setText("ABCDEF");
        viemodel.getPostalCodeField().setHasFocus(false);

        Assert.assertTrue(viemodel.getPostalCodeField().getShowError());
        Assert.assertEquals(R.string.enrollment_cardform_postalcode_format_error, viemodel.getPostalCodeField().getError());
    }

    @Test
    public void testCardFormValidPostalNumber() {
        viemodel.getPostalCodeField().setHasFocus(true);
        viemodel.getPostalCodeField().setText("B1C 2B3");
        viemodel.getPostalCodeField().setHasFocus(false);

        Assert.assertFalse(viemodel.getCardNumberField().getShowError());
    }
}
