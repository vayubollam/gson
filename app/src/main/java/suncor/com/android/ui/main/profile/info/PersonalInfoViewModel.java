package suncor.com.android.ui.main.profile.info;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.account.EnrollmentsApi;
import suncor.com.android.data.profiles.ProfilesApi;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PasswordInputField;
import suncor.com.android.ui.common.input.PhoneInputField;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel.Alert;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.KeyStoreStorage;

public class PersonalInfoViewModel extends ViewModel {

    private Profile profile;
    private final MutableLiveData emptyLiveData = new MutableLiveData();
    private InputField firstNameField = new InputField();
    private InputField lastNameField = new InputField();
    private PasswordInputField passwordField = new PasswordInputField(R.string.enrollment_password_empty_error);
    private PhoneInputField phoneField = new PhoneInputField(R.string.profile_personnal_informations_phone_field_invalid_format);
    private EmailInputField emailInputField = new EmailInputField(R.string.profile_personnal_informations_email_empty_inline_error, R.string.profile_personnal_informations_email_format_inline_error, R.string.profile_personnal_informations_email_restricted_inline_error);
    private MutableLiveData<Event> _showSaveButtonEvent = new MutableLiveData<>();
    public LiveData<Event> showSaveButtonEvent = _showSaveButtonEvent;

    private MutableLiveData<Event<Alert>> _bottomSheetAlertObservable = new MutableLiveData<>();
    public LiveData<Event<Alert>> bottomSheetAlertObservable = _bottomSheetAlertObservable;

    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private MutableLiveData<Boolean> _isPasswordLoading = new MutableLiveData<>();
    public LiveData<Boolean> isPasswordLoading = _isPasswordLoading;


    private MediatorLiveData<Event<Boolean>> _navigateToSignIn = new MediatorLiveData<>();
    public LiveData<Event<Boolean>> navigateToSignIn = _navigateToSignIn;

    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;

    private MutableLiveData<Event> updateProfileEvent = new MutableLiveData<>();
    private MutableLiveData<Event> signOutEvent = new MutableLiveData<>();
    private boolean isUpdatingEmail;
    private boolean isUpdatingPassword;
    private boolean isSamePhoneNumber;
    private ProfileSharedViewModel profileSharedViewModel;
    private static final String CREDENTIALS_KEY = "credentials";
    private String password;
    private String email;

    public String getEmail() {
        return email;
    }
    public boolean isDuplicateEmail =  false;
    private MutableLiveData<Event<Integer>> _callEvent = new MutableLiveData<>();
    public LiveData<Event<Integer>> callEvent = _callEvent;


    @SuppressWarnings("unchecked")
    @Inject
    public PersonalInfoViewModel(SessionManager sessionManager, ProfilesApi profilesApi, EnrollmentsApi enrollmentsApi, KeyStoreStorage keyStoreStorage, FingerprintManager fingerprintManager) {
        String savedCredentials = keyStoreStorage.retrieve(CREDENTIALS_KEY);
        if (savedCredentials != null) try {
            JSONObject credentials = new JSONObject(savedCredentials);
            password = credentials.getString("password");
            email = credentials.getString("email");
        } catch (JSONException e) {
            return;
        }
        profile = sessionManager.getProfile();
        firstNameField.setText(profile.getFirstName());
        lastNameField.setText(profile.getLastName());
        phoneField.setText(profile.getPhone());
        emailInputField.setText(profile.getEmail());
        passwordField.setText(password);


        LiveData<Resource<Boolean>> apiObservable = Transformations.switchMap(updateProfileEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                ProfileRequest request = new ProfileRequest();
                isUpdatingEmail = !emailInputField.getText().equals(profile.getEmail());
                isUpdatingPassword = !passwordField.getText().equals(password);
                isSamePhoneNumber = samePhoneNumber(phoneField.getText());
                boolean profileShouldBeUpdated = isUpdatingPassword || isUpdatingEmail || !isSamePhoneNumber;

                if (profileShouldBeUpdated) {
                    if (isUpdatingEmail) {
                        request.setEmail(emailInputField.getText());
                        request.setPetroPointsCardNumber(sessionManager.getProfile().getPetroPointsNumber());
                    }
                    if (!isSamePhoneNumber) {
                        request.setPhoneNumber(phoneField.getText());

                    }
                    if (isUpdatingPassword) {
                        request.setPassword(passwordField.getText());
                    }
                    request.setSecurityAnswerEncrypted(profileSharedViewModel.getEcryptedSecurityAnswer());
                    return profilesApi.updateProfile(request);
                } else {
                    //Generate a loading event to navigate to previous screen
                    MutableLiveData<Resource<Boolean>> loadingLiveData = new MutableLiveData<>();
                    loadingLiveData.setValue(Resource.loading());
                    return loadingLiveData;
                }
            } else {
                return emptyLiveData;
            }
        });

        apiObservable.observeForever(result -> {
            switch (result.status) {
                case LOADING:

                    if (isUpdatingEmail) {
                        _isLoading.setValue(true);
                    } else if (isUpdatingPassword) {
                        _isPasswordLoading.setValue(true);
                    } else {
                        _navigateToProfile.setValue(Event.newEvent(true));
                    }
                    break;
                case SUCCESS:
                    if (isUpdatingPassword || isUpdatingEmail) {
                        signOutEvent.setValue(Event.newEvent(true));
                        fingerprintManager.deactivateAutoLogin();
                        fingerprintManager.deactivateFingerprint();
                        keyStoreStorage.remove(CREDENTIALS_KEY);
                    }
                    if (isUpdatingEmail) {
                        email = null;
                    } else if (!isSamePhoneNumber) {
                        profileSharedViewModel.postToast(R.string.profile_update_toast);
                        //Update the saved profile of the app
                        sessionManager.getProfile().setPhone(phoneField.getText());
                    }
                    break;
                case ERROR:
                    _isPasswordLoading.setValue(false);
                    _isLoading.setValue(false);
                    Alert alert = new Alert();
                    if (Objects.requireNonNull(result.message).equalsIgnoreCase(ErrorCodes.ERR_EMAIL_ALREADY_EXISTS)) {
                        alert.title = R.string.profile_personnal_informations_email_duplicate_alert_title;
                        alert.message = R.string.profile_personnal_informations_email_duplicate_alert_message;
                        alert.positiveButton = R.string.profile_personnal_informations_email_duplicate_different_email_button;
                        alert.positiveButtonClick = () -> {
                            isDuplicateEmail = true;
                            emailInputField.setText("");
                            emailInputField.notifyPropertyChanged(BR.text);
                        };
                        alert.negativeButton = R.string.profile_personnal_informations_email_duplicate_undo_button;
                        alert.negativeButtonClick =  () -> {
                            if (!emailInputField.getText().equals(profile.getEmail())) {
                                emailInputField.setText(profile.getEmail());
                                emailInputField.notifyPropertyChanged(BR.text);
                            }
                        };
                    }
                    else if (Objects.requireNonNull(result.message).equalsIgnoreCase(ErrorCodes.ERR_PASSWORD_USED_EARLIER)) {
                        alert.title = R.string.msg_used_password_title;
                        alert.message = R.string.msg_used_password_message;
                        alert.positiveButton = R.string.ok;
                    }
                    else {
                        alert.title = R.string.msg_am001_title;
                        alert.message = R.string.msg_am001_message;
                        alert.positiveButton = R.string.ok;
                    }
                    profileSharedViewModel.postAlert(alert);
                    break;
            }
        });

        //Handle signOutEvent after changing email with success
        _navigateToSignIn.addSource(
                Transformations.switchMap(signOutEvent, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        return sessionManager.logout();
                    }
                    return new MutableLiveData<>();
                }),
                signOutResult -> {
                    if (signOutResult.status == Resource.Status.SUCCESS) {
                        _isLoading.postValue(false);
                        _isPasswordLoading.postValue(false);
                        _navigateToSignIn.postValue(Event.newEvent(true));
                    } else if (signOutResult.status == Resource.Status.ERROR) {
                        Alert alert = new Alert();
                        alert.title = R.string.msg_e001_title;
                        alert.message = R.string.msg_e001_message;
                        alert.positiveButton = R.string.ok;
                        profileSharedViewModel.postAlert(alert);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private boolean samePhoneNumber(String text) {
        String fromProfile;
        if (profile.getPhone() == null) {
            fromProfile = "";
        } else {
            fromProfile = profile.getPhone().replace("-", "");
        }
        return text.replace("-", "").equals(fromProfile);
    }

    public void setProfileSharedViewModel(ProfileSharedViewModel profileSharedViewModel) {
        this.profileSharedViewModel = profileSharedViewModel;
    }

    public InputField getFirstNameField() {
        return firstNameField;
    }

    public InputField getLastNameField() {
        return lastNameField;
    }

    public PhoneInputField getPhoneField() {
        return phoneField;
    }

    public EmailInputField getEmailInputField() {
        return emailInputField;
    }

    public PasswordInputField getPasswordField() {
        return passwordField;
    }

    public void phoneTextChanged(String text) {
        if (!samePhoneNumber(text)) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void passwordTextChanged(String text) {
        if (!text.equals(passwordField.getText())) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void emailTextChanged(String text) {
        if (!text.equals(emailInputField.getText())) {
            _showSaveButtonEvent.setValue(Event.newEvent(true));
        }
    }

    public void save(boolean hasConnection) {
        boolean isValid = true;
        if (!phoneField.isValid()) {
            phoneField.setShowError(true);
            isValid = false;
        }
        if (!passwordField.isValid()) {
            passwordField.setShowError(true);
            isValid = false;
        }
        if (!emailInputField.isValid()) {
            emailInputField.setShowError(true);
            Alert alert = new Alert();
            alert.title = emailInputField.isEmpty() ? R.string.profile_personnal_informations_email_empty_alert_title : R.string.profile_personnal_informations_email_invalid_alert_title;
            alert.message = emailInputField.isEmpty() ? R.string.profile_personnal_informations_email_empty_alert_message : R.string.profile_personnal_informations_email_invalid_alert_message;
            alert.positiveButton = R.string.profile_get_help_call;
            alert.positiveButtonClick = () -> {
                if(!emailInputField.isEmpty()){
                    _callEvent.setValue(new Event<Integer>(R.string.customer_support_number));
                }
                emailInputField.setText("");
                emailInputField.notifyPropertyChanged(BR.text);
            };
            alert.negativeButton = R.string.cancel;
            alert.negativeButtonClick = () -> {
                emailInputField.setShowError(true);
                emailInputField.notifyPropertyChanged(BR.text);
            };
            profileSharedViewModel.postAlert(alert);

            isValid = false;
        }
        if (isValid) {
            if (!hasConnection) {
                Alert alert = new Alert();
                alert.title = R.string.msg_e002_title;
                alert.message = R.string.msg_e002_message;
                alert.positiveButton = R.string.ok;
                profileSharedViewModel.postAlert(alert);
            } else {
                if (!passwordField.getText().equals(password) || !emailInputField.getText().equals(profile.getEmail())) {
                    Alert signoutAlert = new Alert();
                    signoutAlert.title = R.string.profile_personnal_informations_email_alert_title;
                    signoutAlert.message = R.string.profile_personnal_informations_email_alert_message;
                    signoutAlert.positiveButton = R.string.profile_personnal_informations_email_alert_signout_button;
                    signoutAlert.negativeButton = R.string.cancel;
                    signoutAlert.negativeButtonClick = () -> {
                        if(!emailInputField.getText().equals(profile.getEmail())) {
                            emailInputField.setText(profile.getEmail());
                            emailInputField.notifyPropertyChanged(BR.text);
                        }
                    };
                    signoutAlert.positiveButtonClick = this::callUpdateProfile;
                    profileSharedViewModel.postAlert(signoutAlert);
                } else {
                    callUpdateProfile();
                }
            }
        }
    }

    private void callUpdateProfile() {
        updateProfileEvent.setValue(Event.newEvent(true));
    }

}
