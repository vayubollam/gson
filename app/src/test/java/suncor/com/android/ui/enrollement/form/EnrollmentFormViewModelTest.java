package suncor.com.android.ui.enrollement.form;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mockito;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import suncor.com.android.R;
import suncor.com.android.data.repository.account.EmailCheckApi;
import suncor.com.android.model.Resource;

import static org.mockito.Mockito.when;

public class EnrollmentFormViewModelTest {

    private EnrollmentFormViewModel viewModel;

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        viewModel = new EnrollmentFormViewModel(null);
    }

    @Test
    public void testViewModelJoinEmpty() {
        int focusedItem = viewModel.canJoin();

        for (InputField field : viewModel.getRequiredFields()) {
            Assert.assertTrue(field.getShowError());
        }

        Assert.assertEquals(0, focusedItem);
    }

    @Test
    public void testViewModelPasswordFirstEmpty() {
        viewModel.getFirstNameField().setText("name");
        viewModel.getLastNameField().setText("last name");
        int focusedItem = viewModel.canJoin();

        for (InputField field : viewModel.getRequiredFields()) {
            if (field == viewModel.getFirstNameField() || field == viewModel.getLastNameField()) {
                Assert.assertFalse(field.getShowError());
            } else
                Assert.assertTrue(field.getShowError());
        }

        Assert.assertEquals(2, focusedItem);
    }

    @Test
    public void testFirstNameError() {
        viewModel.getFirstNameField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_first_name_error, viewModel.getFirstNameField().getError());
    }

    @Test
    public void testLastNameError() {
        viewModel.getLastNameField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_last_name_error, viewModel.getLastNameField().getError());
    }

    @Test
    public void testStreetAddressNameError() {
        viewModel.getStreetAddressField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_street_address_error, viewModel.getStreetAddressField().getError());
    }

    @Test
    public void testCityError() {
        viewModel.getCityField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_city_error, viewModel.getCityField().getError());
    }

    @Test
    public void testProvinceError() {
        viewModel.getProvinceField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_province_error, viewModel.getProvinceField().getError());
    }

    @Test
    public void testPostalCodeError() {
        viewModel.getPostalCodeField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_postalcode_error, viewModel.getPostalCodeField().getError());
    }

    @Test
    public void testEmailEmptyError() {
        viewModel.getEmailInputField().setText("");
        viewModel.getEmailInputField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_email_empty_error, viewModel.getEmailInputField().getError());
    }

    @Test
    public void testEmailFormatError() {
        viewModel.getEmailInputField().setText("bad_email");
        viewModel.getEmailInputField().setShowError(true);

        Assert.assertEquals(R.string.enrollment_email_format_error, viewModel.getEmailInputField().getError());
    }

    @Test
    public void testOneItemFilledFalse() {
        Assert.assertFalse(viewModel.oneItemFilled());
    }

    @Test
    public void testOneItemFilledTrue() {
        viewModel.getFirstNameField().setText("name");

        Assert.assertTrue(viewModel.oneItemFilled());
    }

    @Test
    public void testEmailValidation_ValidEmail() {
        EmailCheckApi api = Mockito.mock(EmailCheckApi.class);
        MutableLiveData<Resource<EmailCheckApi.EmailState>> successValidation = new MutableLiveData<>();
        successValidation.setValue(Resource.success(EmailCheckApi.EmailState.VALID));
        when(api.checkEmail("email@suncor.com")).thenReturn(successValidation);

        viewModel = new EnrollmentFormViewModel(api);
        Observer<Resource<EmailCheckApi.EmailState>> dummyObserver = emailStateResource -> {
            //Just to active the livedata
        };

        viewModel.emailCheckLiveData.observeForever(dummyObserver);

        viewModel.getEmailInputField().setHasFocus(true);

        viewModel.getEmailInputField().setText("email@suncor.com");
        viewModel.getEmailInputField().setHasFocus(false);

        Assert.assertEquals(EmailCheckApi.EmailState.VALID, viewModel.emailCheckLiveData.getValue().data);
    }

    @Test
    public void testEmailValidation_OnFocus() {
        EmailCheckApi api = Mockito.mock(EmailCheckApi.class);
        MutableLiveData<Resource<EmailCheckApi.EmailState>> successValidation = new MutableLiveData<>();
        successValidation.setValue(Resource.success(EmailCheckApi.EmailState.VALID));
        when(api.checkEmail("email@suncor.com")).thenReturn(successValidation);

        viewModel = new EnrollmentFormViewModel(api);
        Observer<Resource<EmailCheckApi.EmailState>> dummyObserver = emailStateResource -> {
            //Just to active the livedata
        };

        viewModel.emailCheckLiveData.observeForever(dummyObserver);

        viewModel.getEmailInputField().setHasFocus(true);

        viewModel.getEmailInputField().setText("email@suncor.com");

        Assert.assertEquals(EmailCheckApi.EmailState.UNCHECKED, viewModel.emailCheckLiveData.getValue().data);
    }

    @Test
    public void testEmailValidation_InValidEmail() {
        EmailCheckApi api = Mockito.mock(EmailCheckApi.class);
        MutableLiveData<Resource<EmailCheckApi.EmailState>> successValidation = new MutableLiveData<>();
        successValidation.setValue(Resource.success(EmailCheckApi.EmailState.INVALID));
        when(api.checkEmail("email@suncor.com")).thenReturn(successValidation);

        viewModel = new EnrollmentFormViewModel(api);
        Observer<Resource<EmailCheckApi.EmailState>> dummyObserver = emailStateResource -> {
            //Just to active the livedata
        };

        viewModel.emailCheckLiveData.observeForever(dummyObserver);

        viewModel.getEmailInputField().setHasFocus(true);

        viewModel.getEmailInputField().setText("email@suncor.com");
        viewModel.getEmailInputField().setHasFocus(false);

        Assert.assertEquals(EmailCheckApi.EmailState.INVALID, viewModel.emailCheckLiveData.getValue().data);
    }
}
