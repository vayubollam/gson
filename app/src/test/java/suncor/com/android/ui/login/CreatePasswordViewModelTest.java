package suncor.com.android.ui.login;

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
import suncor.com.android.data.users.UsersApi;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;

import static org.mockito.Mockito.when;

public class CreatePasswordViewModelTest {
    private CreatePasswordViewModel viewModel;
    private SessionManager sessionManager = Mockito.mock(SessionManager.class);
    private UsersApi api = Mockito.mock(UsersApi.class);


    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        viewModel = new CreatePasswordViewModel(sessionManager, api);
    }

    @Test
    public void testPasswordInputFieldIsEmpty() {
        viewModel.getPasswordField().setText("");
        viewModel.validateAndContinue();
        Assert.assertEquals(R.string.login_create_password_empty_error, viewModel.getPasswordField().getError());
    }

    @Test
    public void testSamePassword() {
        MutableLiveData<Resource<Boolean>> apiResponse = new MutableLiveData<>();
        apiResponse.setValue(Resource.error(ErrorCodes.ERR_PASSWORD_DUPLICATED));
        when(api.createPassword("email@suncor.com", "Pass123!", "encrypted")).thenReturn(apiResponse);
        Observer<Resource<Boolean>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.api.observeForever(dummyObserver);
        viewModel.getPasswordField().setText("Pass123!");
        viewModel.setEmail("email@suncor.com");
        viewModel.setEmailEncrypted("encrypted");
        viewModel.validateAndContinue();

        Assert.assertEquals(ErrorCodes.ERR_PASSWORD_DUPLICATED, viewModel.api.getValue().message);
    }

    @Test
    public void testCreatePassword() {
        MutableLiveData<Resource<Boolean>> apiResponse = new MutableLiveData<>();
        apiResponse.postValue(Resource.success(true));
        when(api.createPassword("email@suncor.com", "Pass123!", "encrypted")).thenReturn(apiResponse);
        MutableLiveData<Resource<SigninResponse>> loginResponse = new MutableLiveData<>();
        loginResponse.setValue(Resource.success(SigninResponse.success()));
        when(sessionManager.login("email@suncor.com", "Pass123!")).thenReturn(loginResponse);

        Observer<Resource<Boolean>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.api.observeForever(dummyObserver);
        viewModel.getPasswordField().setText("Pass123!");
        viewModel.setEmail("email@suncor.com");
        viewModel.setEmailEncrypted("encrypted");
        viewModel.validateAndContinue();

        Assert.assertEquals(Resource.Status.SUCCESS, viewModel.api.getValue().status);
    }

}
