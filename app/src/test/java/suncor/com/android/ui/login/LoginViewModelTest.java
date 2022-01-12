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
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.mfp.SigninResponse;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Event;

import static org.mockito.Mockito.when;

public class LoginViewModelTest {
    private LoginViewModel viewModel;
    private SessionManager sessionManager = Mockito.mock(SessionManager.class);

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();

    @Before
    public void init() {
        viewModel = new LoginViewModel(sessionManager,null);
    }

    @Test
    public void testPasswordInputFieldIsEmpty() {
        viewModel.getPasswordInputField().setText("");
        viewModel.getPasswordInputField().setShowError(true);
        Assert.assertEquals(R.string.login_password_field_error, viewModel.getPasswordInputField().getError());
    }


    @Test
    public void testEmailInputFieldIsEmpty() {
        viewModel.getEmailInputField().setText("");
        viewModel.getEmailInputField().setShowError(true);
        Assert.assertEquals(R.string.login_email_field_error, viewModel.getEmailInputField().getError());
    }

    @Test
    public void testSignIn_HardLock() {
        MutableLiveData<Resource<SigninResponse>> hardLockStatus = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.hardLocked();
        hardLockStatus.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(hardLockStatus);
        Observer<Event<Boolean>> anotherDummyObserver = event -> {
            //Just to active the livedata
        };
        Observer<Event<LoginViewModel.LoginFailResponse>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.getCallCustomerService().observeForever(anotherDummyObserver);
        viewModel.getLoginFailedEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();

        LoginViewModel.LoginFailResponse loginFailResponse = viewModel.getLoginFailedEvent().getValue().getContentIfNotHandled();
        loginFailResponse.negativeButtonCallBack.call();
        Assert.assertEquals(R.string.login_hard_lock_alert_title, loginFailResponse.title);
        Assert.assertEquals(R.string.login_hard_lock_alert_message, loginFailResponse.message.content);
        Assert.assertEquals(R.string.login_hard_lock_alert_call_button, loginFailResponse.negativeButtonTitle);
        Assert.assertTrue(viewModel.getCallCustomerService().getValue().getContentIfNotHandled().booleanValue());
    }

    @Test
    public void testSignIn_Success() {
        MutableLiveData<Resource<SigninResponse>> successValidation = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.success();
        successValidation.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(successValidation);
        Observer<Event<Boolean>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.getLoginSuccessEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();
        Assert.assertTrue(viewModel.getLoginSuccessEvent().getValue().getContentIfNotHandled().booleanValue());

    }

    @Test
    public void testSignIn_SoftLock() {
        MutableLiveData<Resource<SigninResponse>> softLockValidation = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.softLocked(30);
        softLockValidation.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(softLockValidation);
        Observer<Event<LoginViewModel.LoginFailResponse>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.getLoginFailedEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();
        LoginViewModel.LoginFailResponse loginFailResponse = viewModel.getLoginFailedEvent().getValue().getContentIfNotHandled();
        Assert.assertEquals(R.string.login_soft_lock_alert_title, loginFailResponse.title);
        Assert.assertEquals(R.string.login_soft_lock_alert_message, loginFailResponse.message.content);
        Assert.assertEquals(signinResponse.getTimeOut(), loginFailResponse.message.args[1]);
    }

    @Test
    public void testSignIn_OtherFailure() {
        MutableLiveData<Resource<SigninResponse>> otherFailureValidation = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.generalFailure();
        otherFailureValidation.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(otherFailureValidation);
        Observer<Event<LoginViewModel.LoginFailResponse>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.getLoginFailedEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();
        LoginViewModel.LoginFailResponse loginFailResponse = viewModel.getLoginFailedEvent().getValue().getContentIfNotHandled();
        Assert.assertEquals(R.string.msg_e001_title, loginFailResponse.title);
        Assert.assertEquals(R.string.msg_e001_message, loginFailResponse.message.content);


    }

    @Test
    public void testSignIn_WrongCredentials_1st_message() {
        MutableLiveData<Resource<SigninResponse>> wrongCredentialsValidation = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.wrongCredentials(5);
        wrongCredentialsValidation.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(wrongCredentialsValidation);
        Observer<Event<LoginViewModel.LoginFailResponse>> dummyObserver = event -> {
            //Just to active the livedata
        };
        viewModel.getLoginFailedEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();
        LoginViewModel.LoginFailResponse loginFailResponse = viewModel.getLoginFailedEvent().getValue().getContentIfNotHandled();
        Assert.assertEquals(R.string.login_invalid_credentials_dialog_1st_message, loginFailResponse.message.content);
        Assert.assertEquals(R.string.login_invalid_credentials_dialog_title, loginFailResponse.title);


    }

    @Test
    public void testSignIn_WrongCredentials_2st_message() {
        MutableLiveData<Resource<SigninResponse>> wrongCredentialsValidation = new MutableLiveData<>();
        SigninResponse signinResponse = SigninResponse.wrongCredentials(3);
        wrongCredentialsValidation.postValue(Resource.success(signinResponse));
        when(sessionManager.login("email@ibm.com", "password")).thenReturn(wrongCredentialsValidation);
        Observer<Event<LoginViewModel.LoginFailResponse>> dummyObserver = event -> {
            //Just to active the livedata
        };
        Observer<Event<Boolean>> anotherDummyObserver = event -> {
            //Just to active the livedata
        };

        viewModel.getPasswordResetEvent().observeForever(anotherDummyObserver);
        viewModel.getLoginFailedEvent().observeForever(dummyObserver);
        viewModel.getEmailInputField().setText("email@ibm.com");
        viewModel.getPasswordInputField().setText("password");
        viewModel.onClickSignIn();
        LoginViewModel.LoginFailResponse loginFailResponse = viewModel.getLoginFailedEvent().getValue().getContentIfNotHandled();
        loginFailResponse.negativeButtonCallBack.call();
        Assert.assertEquals(R.string.login_invalid_credentials_dialog_title, loginFailResponse.title);
        Assert.assertEquals(R.string.login_invalid_credentials_dialog_2nd_message, loginFailResponse.message.content);
        Assert.assertEquals(signinResponse.getRemainingAttempts(), loginFailResponse.message.args[0]);
        Assert.assertEquals(R.string.login_invalid_credentials_reset_password, loginFailResponse.negativeButtonTitle);
        Assert.assertTrue(viewModel.getPasswordResetEvent().getValue().getContentIfNotHandled().booleanValue());


    }


}

