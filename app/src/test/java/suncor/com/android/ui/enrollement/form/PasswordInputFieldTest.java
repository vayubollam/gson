package suncor.com.android.ui.enrollement.form;

import org.junit.Assert;
import org.junit.Test;

import suncor.com.android.R;

public class PasswordInputFieldTest {
    @Test
    public void testPasswordInitialState() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);

        Assert.assertFalse(inputField.isValid());
        Assert.assertFalse(inputField.isHasRightLength());
        Assert.assertFalse(inputField.isHasLowerCase());
        Assert.assertFalse(inputField.isHasUpperCase());
        Assert.assertFalse(inputField.isHasNumber());
        Assert.assertFalse(inputField.isHasSpecialChar());
        Assert.assertFalse(inputField.isShowValidationHint());
    }

    @Test
    public void testPasswordOnFocus() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setHasFocus(true);

        Assert.assertTrue(inputField.isShowValidationHint());
    }

    @Test
    public void testPasswordValidationLowerChars() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setText("aa");
        Assert.assertTrue(inputField.isHasLowerCase());
    }

    @Test
    public void testPasswordValidationUpperChars() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setText("AA");
        Assert.assertTrue(inputField.isHasUpperCase());
    }

    @Test
    public void testPasswordValidationNumbers() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setText("11");
        Assert.assertTrue(inputField.isHasNumber());
    }

    @Test
    public void testPasswordValidationSpecialChars() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setText("$");
        Assert.assertTrue(inputField.isHasSpecialChar());
    }

    @Test
    public void testPasswordErrorOnChriteriaNotMet() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        //focus
        inputField.setHasFocus(true);
        //type
        inputField.setText("aaaaaaaa");

        //Unfocus
        inputField.setHasFocus(false);

        Assert.assertTrue(inputField.getShowError());
        Assert.assertEquals(-1, inputField.getError());
    }

    @Test
    public void testPasswordErrorOnEmpty() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setShowError(true);

        Assert.assertEquals(R.string.enrollment_password_empty_error, inputField.getError());
    }

    @Test
    public void testPasswordChriteriaMetFocused() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setHasFocus(true);
        inputField.setText("ABcd1234!");

        Assert.assertTrue(inputField.isValid());
        Assert.assertTrue(inputField.isHasRightLength());
        Assert.assertTrue(inputField.isHasLowerCase());
        Assert.assertTrue(inputField.isHasUpperCase());
        Assert.assertTrue(inputField.isHasNumber());
        Assert.assertTrue(inputField.isHasSpecialChar());
        Assert.assertFalse(inputField.isShowValidationHint());
    }

    @Test
    public void testPasswordChriteriaMetUnfocused() {
        PasswordInputField inputField = new PasswordInputField(R.string.enrollment_password_empty_error);
        inputField.setHasFocus(true);
        inputField.setText("ABcd1234!");
        inputField.setHasFocus(false);

        Assert.assertTrue(inputField.isValid());
        Assert.assertTrue(inputField.isHasRightLength());
        Assert.assertTrue(inputField.isHasLowerCase());
        Assert.assertTrue(inputField.isHasUpperCase());
        Assert.assertTrue(inputField.isHasNumber());
        Assert.assertTrue(inputField.isHasSpecialChar());
        Assert.assertFalse(inputField.isShowValidationHint());
    }
}
