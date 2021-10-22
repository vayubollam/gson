package suncor.com.android.ui.main.profile.preferences;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.profiles.ProfilesApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.FingerprintManager;

public class PreferencesViewModel extends ViewModel {
    private final FingerprintManager fingerprintManager;
    private final Profile profile;

    private ObservableBoolean emailOffers = new ObservableBoolean();
    private ObservableBoolean donotEmail = new ObservableBoolean();
    private ObservableBoolean textOffers = new ObservableBoolean();
    private ObservableBoolean enableFingerprint = new ObservableBoolean();
    private ObservableBoolean isEditing = new ObservableBoolean();
    private ObservableBoolean hasPhoneNumber = new ObservableBoolean();
    private ProfileSharedViewModel profileSharedViewModel;
    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;
    private LiveData<Resource<Boolean>> profilesApiCall;
    private MutableLiveData<Event<Boolean>> updateEvent = new MutableLiveData<>();

    private boolean hasUpdatedPhoneOffers;

    @Inject
    public PreferencesViewModel(ProfilesApi profilesApi, SessionManager sessionManager, FingerprintManager fingerprintManager) {
        this.fingerprintManager = fingerprintManager;
        profile = sessionManager.getProfile();
        emailOffers.set(profile.isEmailOffers());
        textOffers.set(profile.isTextOffers());
        donotEmail.set(profile.isDoNotEmail());
        enableFingerprint.set(fingerprintManager.isFingerprintActivated());
        Observable.OnPropertyChangedCallback editCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                isEditing.set(true);
            }
        };
        emailOffers.addOnPropertyChangedCallback(editCallback);
        textOffers.addOnPropertyChangedCallback(editCallback);
        donotEmail.addOnPropertyChangedCallback(editCallback);
        enableFingerprint.addOnPropertyChangedCallback(editCallback);

        if (profile.getPhone() == null || profile.getPhone().isEmpty()) {
            hasPhoneNumber.set(false);
        } else {
            hasPhoneNumber.set(true);
        }


        profilesApiCall = Transformations.switchMap(updateEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                ProfileRequest request = new ProfileRequest();
                if (hasUpdatedPhoneOffers) {
                    request.setEmailOffers(emailOffers.get());
                    request.setTextOffers(textOffers.get());
                }
                if (profile.isTextOffers() != textOffers.get()) {
                    request.setEmailOffers(emailOffers.get());
                    request.setTextOffers(textOffers.get());
                }
                return profilesApi.updateProfile(request);
            } else {
                return new MutableLiveData<>();
            }
        });

        profilesApiCall.observeForever(resource -> {
            switch (resource.status) {
                case LOADING:
                    _navigateToProfile.setValue(Event.newEvent(true));
                    break;
                case SUCCESS:
                    profileSharedViewModel.postToast(R.string.profile_update_toast);
                    if (sessionManager.getProfile() != null) {
                        sessionManager.getProfile().setEmailOffers(emailOffers.get());
                        sessionManager.getProfile().setDoNotEmail(donotEmail.get());
                        sessionManager.getProfile().setTextOffers(textOffers.get());
                    }
                    break;
                case ERROR:
                    ProfileSharedViewModel.Alert alert = new ProfileSharedViewModel.Alert();
                    alert.title = R.string.msg_am001_title;
                    alert.message = R.string.msg_am001_message;
                    alert.positiveButton = R.string.ok;
                    profileSharedViewModel.postAlert(alert);
            }
        });
    }

    public ObservableBoolean getDonotEmail() {
        return donotEmail;
    }

    public ObservableBoolean getHasPhoneNumber() {
        return hasPhoneNumber;
    }

    public ObservableBoolean getEmailOffers() {
        return emailOffers;
    }

    public void setEmailOffers(boolean value) {
        emailOffers.set(value);
        donotEmail.set(!value);
        hasUpdatedPhoneOffers = true;
    }

    public ObservableBoolean getTextOffers() {
        return textOffers;
    }

    public ObservableBoolean getEnableFingerprint() {
        return enableFingerprint;
    }

    public void setTextOffers(boolean value) {
        textOffers.set(value);
    }

    public ObservableBoolean getIsEditing() {
        return isEditing;
    }

    public void setProfileSharedViewModel(ProfileSharedViewModel profileSharedViewModel) {
        this.profileSharedViewModel = profileSharedViewModel;
    }

    public boolean supportsFingerprint() {
        return fingerprintManager.isFingerPrintExistAndEnrolled();
    }

    public void save(boolean hasConnection) {
        if (enableFingerprint.get()) {
            fingerprintManager.activateFingerprint();
        } else {
            fingerprintManager.deactivateFingerprint();
        }

        if (hasUpdatedPhoneOffers || profile.isTextOffers() != textOffers.get()) {
            if (!hasConnection) {
                _navigateToProfile.setValue(Event.newEvent(true));
                ProfileSharedViewModel.Alert alert = new ProfileSharedViewModel.Alert();
                alert.title = R.string.msg_e002_title;
                alert.message = R.string.msg_e002_message;
                alert.positiveButton = R.string.ok;
                profileSharedViewModel.postAlert(alert);
            } else {
                updateEvent.setValue(Event.newEvent(true));
            }
        } else {
            _navigateToProfile.setValue(Event.newEvent(true));
            profileSharedViewModel.postToast(R.string.profile_update_toast);
        }
    }

}
