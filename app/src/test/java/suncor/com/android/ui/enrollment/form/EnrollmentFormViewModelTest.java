package suncor.com.android.ui.enrollment.form;

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
import suncor.com.android.data.account.EnrollmentsApi;
import suncor.com.android.data.suggestions.CanadaPostAutocompleteProvider;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.utilities.FingerprintManager;

import static org.mockito.Mockito.when;

public class EnrollmentFormViewModelTest {

    private EnrollmentFormViewModel viewModel;
    private EnrollmentsApi api = Mockito.mock(EnrollmentsApi.class);
    private SessionManager sessionManager = Mockito.mock(SessionManager.class);
    private CanadaPostAutocompleteProvider canadaPostAutocompleteProvider = Mockito.mock(CanadaPostAutocompleteProvider.class);
    private FingerprintManager fingerprintManager = Mockito.mock(FingerprintManager.class);


    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        viewModel = new EnrollmentFormViewModel(api, sessionManager, canadaPostAutocompleteProvider, fingerprintManager);
    }

    @Test
    public void testViewModelJoinEmpty() {
        int focusedItem = viewModel.validateAndJoin();

        for (InputField field : viewModel.getRequiredFields()) {
            Assert.assertTrue(field.getShowError());
        }

        Assert.assertEquals(0, focusedItem);
    }

    @Test
    public void testViewModelPasswordFirstEmpty() {
        viewModel.getFirstNameField().setText("name");
        viewModel.getLastNameField().setText("last name");
        int focusedItem = viewModel.validateAndJoin();

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
    public void testPostalCodeFormatError() {
        viewModel.getPostalCodeField().setHasFocus(true);
        viewModel.getPostalCodeField().setText("bad postal code");
        viewModel.getPostalCodeField().setHasFocus(false);

        Assert.assertEquals(R.string.enrollment_postalcode_format_error, viewModel.getPostalCodeField().getError());
    }

    @Test
    public void testPostalCodeFormatValid() {
        viewModel.getPostalCodeField().setHasFocus(true);
        viewModel.getPostalCodeField().setText("L6K 6L8");
        viewModel.getPostalCodeField().setHasFocus(false);

        Assert.assertEquals(-1, viewModel.getPostalCodeField().getError());
    }

    @Test
    public void testPostalCodeMatchingProvinceError() {
        viewModel.getPostalCodeField().setHasFocus(true);
        viewModel.getPostalCodeField().setFirstCharacterValidation("T"); //for Alberta
        viewModel.getPostalCodeField().setText("L6K 6L8");
        viewModel.getPostalCodeField().setHasFocus(false);


        Assert.assertEquals(R.string.enrollment_postalcode_matching_province_error, viewModel.getPostalCodeField().getError());
    }

    @Test
    public void testPostalCodeMatchingProvinceValid() {
        viewModel.getPostalCodeField().setHasFocus(true);
        viewModel.getPostalCodeField().setFirstCharacterValidation("L"); //for Alberta
        viewModel.getPostalCodeField().setText("L6K 6L8");
        viewModel.getPostalCodeField().setHasFocus(false);


        Assert.assertEquals(-1, viewModel.getPostalCodeField().getError());
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
        Assert.assertFalse(viewModel.isOneItemFilled());
    }

    @Test
    public void testOneItemFilledTrue() {
        viewModel.getFirstNameField().setText("name");

        Assert.assertTrue(viewModel.isOneItemFilled());
    }

//    @Test
//    public void testEmailValidation_ValidEmail() {
//        MutableLiveData<Resource<EnrollmentsApi.EmailState>> successValidation = new MutableLiveData<>();
//        successValidation.setValue(Resource.success(EnrollmentsApi.EmailState.VALID));
//        when(api.checkEmail("email@suncor.com", null)).thenReturn(successValidation);
//
//
//        viewModel.getShowDuplicateEmailEvent().observeForever(ignored -> {
//        });
//        viewModel.getIsValidatingEmail().observeForever(ignored -> {
//        });
//
//        viewModel.getEmailInputField().setHasFocus(true);
//
//        viewModel.getEmailInputField().setText("email@suncor.com");
//        viewModel.getEmailInputField().setHasFocus(false);
//
//        Assert.assertEquals(false, viewModel.getIsValidatingEmail().getValue());
//        Assert.assertNull(viewModel.getShowDuplicateEmailEvent().getValue());
//    }
//
//    @Test
//    public void testEmailValidation_OnFocus() {
//        MutableLiveData<Resource<EnrollmentsApi.EmailState>> successValidation = new MutableLiveData<>();
//        successValidation.setValue(Resource.success(EnrollmentsApi.EmailState.VALID));
//        when(api.checkEmail("email@suncor.com", null)).thenReturn(successValidation);
//
//        Observer<Boolean> dummyObserver = emailStateResource -> {
//            //Just to active the livedata
//        };
//
//        viewModel.getIsValidatingEmail().observeForever(dummyObserver);
//
//        viewModel.getEmailInputField().setHasFocus(true);
//
//        viewModel.getEmailInputField().setText("email@suncor.com");
//
//        Assert.assertEquals(false, viewModel.getIsValidatingEmail().getValue());
//    }

//    @Test
//    public void testEmailValidation_FocusOut() {
//        MutableLiveData<Resource<EnrollmentsApi.EmailState>> loading = new MutableLiveData<>();
//        loading.setValue(Resource.loading());
//        when(api.checkEmail("email@suncor.com", null)).thenReturn(loading);
//
//        Observer<Boolean> dummyObserver = emailStateResource -> {
//            //Just to active the livedata
//        };
//
//        viewModel.getIsValidatingEmail().observeForever(dummyObserver);
//
//        viewModel.getEmailInputField().setHasFocus(true);
//        viewModel.getEmailInputField().setText("email@suncor.com");
//        viewModel.getEmailInputField().setHasFocus(false);
//
//
//        Assert.assertEquals(true, viewModel.getIsValidatingEmail().getValue());
//    }
//
//    @Test
//    public void testEmailValidation_InValidEmail() {
//        MutableLiveData<Resource<EnrollmentsApi.EmailState>> successValidation = new MutableLiveData<>();
//        successValidation.setValue(Resource.success(EnrollmentsApi.EmailState.ALREADY_REGISTERED));
//        when(api.checkEmail("email@suncor.com", null)).thenReturn(successValidation);
//
//        Observer<Event<Boolean>> dummyObserver = emailStateResource -> {
//            //Just to active the livedata
//        };
//
//        viewModel.getShowDuplicateEmailEvent().observeForever(dummyObserver);
//
//        viewModel.getEmailInputField().setHasFocus(true);
//
//        viewModel.getEmailInputField().setText("email@suncor.com");
//        viewModel.getEmailInputField().setHasFocus(false);
//
//        Assert.assertNotNull(viewModel.getShowDuplicateEmailEvent().getValue());
//    }
}
