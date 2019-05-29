package suncor.com.android.ui.home.profile.info;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import suncor.com.android.BR;
import suncor.com.android.R;
import suncor.com.android.data.repository.profiles.ProfilesApi;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.input.EmailInputField;
import suncor.com.android.ui.common.input.InputField;
import suncor.com.android.ui.common.input.PhoneInputField;
import suncor.com.android.ui.home.profile.ProfileSharedViewModel;
import suncor.com.android.ui.home.profile.ProfileSharedViewModel.Alert;

public class PersonalInfoViewModel extends ViewModel {

    private final Profile profile;
    private final MutableLiveData emptyLiveData = new MutableLiveData();
    private InputField firstNameField = new InputField();
    private InputField lastNameField = new InputField();
    private PhoneInputField phoneField = new PhoneInputField(R.string.profile_personnal_informations_phone_field_invalid_format);
    private EmailInputField emailInputField = new EmailInputField(R.string.profile_personnal_informations_email_empty_inline_error, R.string.profile_personnal_informations_email_format_inline_error, R.string.profile_personnal_informations_email_restricted_inline_error);
    private MutableLiveData<Event> _showSaveButtonEvent = new MutableLiveData<>();
    public LiveData<Event> showSaveButtonEvent = _showSaveButtonEvent;

    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    public LiveData<Boolean> isLoading = _isLoading;

    private MediatorLiveData<Event<Boolean>> _navigateToSignIn = new MediatorLiveData<>();
    public LiveData<Event<Boolean>> navigateToSignIn = _navigateToSignIn;

    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;

    private MutableLiveData<Event> updateProfileEvent = new MutableLiveData<>();
    private MutableLiveData<Event> signOutEvent = new MutableLiveData<>();
    private boolean isUpdatingEmail;

    private ProfileSharedViewModel profileSharedViewModel;

    @SuppressWarnings("unchecked")
    @Inject
    public PersonalInfoViewModel(SessionManager sessionManager, ProfilesApi profilesApi) {
        profile = sessionManager.getProfile();
        firstNameField.setText(profile.getFirstName());
        lastNameField.setText(profile.getLastName());
        phoneField.setText(profile.getPhone());
        emailInputField.setText(profile.getEmail());
        LiveData<Resource<Boolean>> apiObservable = Transformations.switchMap(updateProfileEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                ProfileRequest request = new ProfileRequest(profile);
                isUpdatingEmail = !emailInputField.getText().equals(profile.getEmail());
                boolean isSamePhoneNumber = samePhoneNumber(phoneField.getText());
                boolean profileShouldBeUpdated = isUpdatingEmail || !isSamePhoneNumber;

                if (profileShouldBeUpdated) {
                    request.setEmail(emailInputField.getText());
                    request.setPhoneNumber(phoneField.getText().replace("-", ""));

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
                    } else {
                        _navigateToProfile.setValue(Event.newEvent(true));
                    }
                    break;
                case SUCCESS:
                    if (isUpdatingEmail) {
                        signOutEvent.setValue(Event.newEvent(true));
                    } else {
                        profileSharedViewModel.postToast(R.string.profile_update_toast);
                        //Update the saved profile of the app
                        sessionManager.getProfile().setPhone(phoneField.getText());
                    }
                    break;
                case ERROR:
                    _isLoading.setValue(false);
                    Alert alert = new Alert();
                    if (ErrorCodes.ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE.equals(result.message)) {
                        alert.title = R.string.profile_personnal_informations_email_duplicate_alert_title;
                        alert.message = R.string.profile_personnal_informations_email_duplicate_alert_message;
                        alert.positiveButton = R.string.profile_personnal_informations_email_duplicate_different_email_button;
                        alert.positiveButtonClick = () -> {
                            emailInputField.setText("");
                            emailInputField.notifyPropertyChanged(BR.text);
                        };
                        alert.negativeButton = R.string.profile_personnal_informations_email_duplicate_undo_button;
                        alert.negativeButtonClick = () -> {
                            emailInputField.setText(profile.getEmail());
                            emailInputField.notifyPropertyChanged(BR.text);
                        };
                    } else if (ErrorCodes.ERR_RESTRICTED_DOMAIN.equals(result.message)) {
                        emailInputField.setRestricted(true);
                        alert.title = R.string.profile_personnal_informations_email_restricted_alert_message;
                        alert.positiveButton = R.string.ok;
                        alert.positiveButtonClick = () -> {
                            emailInputField.setText("");
                            emailInputField.notifyPropertyChanged(BR.text);
                        };
                        alert.negativeButton = R.string.cancel;
                        alert.negativeButtonClick = () -> {
                            emailInputField.setText(profile.getEmail());
                            emailInputField.notifyPropertyChanged(BR.text);
                        };
                    } else {
                        alert.title = R.string.profile_personnal_informations_failure_alert_title;
                        alert.message = R.string.profile_personnal_informations_failure_alert_message;
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

    public void phoneTextChanged(String text) {
        if (!samePhoneNumber(text)) {
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
        if (!emailInputField.isValid()) {
            emailInputField.setShowError(true);
            Alert alert = new Alert();
            alert.title = emailInputField.isEmpty() ? R.string.profile_personnal_informations_email_empty_alert_title : R.string.profile_personnal_informations_email_invalid_alert_title;
            alert.message = emailInputField.isEmpty() ? R.string.profile_personnal_informations_email_empty_alert_message : R.string.profile_personnal_informations_email_invalid_alert_message;
            alert.positiveButton = R.string.ok;
            alert.positiveButtonClick = () -> {
                emailInputField.setText("");
                emailInputField.notifyPropertyChanged(BR.text);
            };
            alert.negativeButton = R.string.cancel;
            alert.negativeButtonClick = () -> {
                emailInputField.setText(profile.getEmail());
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
            } else {
                if (!emailInputField.getText().equals(profile.getEmail())) {
                    Alert alert = new Alert();
                    alert.title = R.string.profile_personnal_informations_email_alert_title;
                    alert.message = R.string.profile_personnal_informations_email_alert_message;
                    alert.positiveButton = R.string.profile_personnal_informations_email_alert_signout_button;
                    alert.negativeButton = R.string.cancel;
                    alert.positiveButtonClick = this::callUpdateProfile;
                    profileSharedViewModel.postAlert(alert);
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
