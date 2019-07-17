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

public class PreferencesViewModel extends ViewModel {
    private ObservableBoolean emailOffers = new ObservableBoolean();
    private ObservableBoolean donotEmail = new ObservableBoolean();
    private ObservableBoolean textOffers = new ObservableBoolean();
    private ObservableBoolean isEditing = new ObservableBoolean();
    private ObservableBoolean hasPhoneNumber = new ObservableBoolean();
    private ProfileSharedViewModel profileSharedViewModel;
    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;
    private LiveData<Resource<Boolean>> profilesApiCall;
    private MutableLiveData<Event<Boolean>> updateEvent = new MutableLiveData<>();

    private boolean hasUpdatedEmailOffers = false;
    private boolean hasUpdatedTextOffers = false;

    @Inject
    public PreferencesViewModel(ProfilesApi profilesApi, SessionManager sessionManager) {
        Profile profile = sessionManager.getProfile();
        emailOffers.set(profile.isEmailOffers());
        textOffers.set(profile.isTextOffers());
        donotEmail.set(profile.isDoNotEmail());
        Observable.OnPropertyChangedCallback editCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                isEditing.set(true);
            }
        };
        emailOffers.addOnPropertyChangedCallback(editCallback);
        textOffers.addOnPropertyChangedCallback(editCallback);
        donotEmail.addOnPropertyChangedCallback(editCallback);

        if (profile.getPhone() == null || profile.getPhone().isEmpty()) {
            hasPhoneNumber.set(false);
        } else {
            hasPhoneNumber.set(true);
        }


        profilesApiCall = Transformations.switchMap(updateEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                ProfileRequest request = new ProfileRequest(profile);
                if (hasUpdatedEmailOffers) {
                    request.setEmailOffers(emailOffers.get());
                }
                if (hasUpdatedTextOffers) {
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
                    if (hasUpdatedEmailOffers) {
                        sessionManager.getProfile().setEmailOffers(emailOffers.get());
                        sessionManager.getProfile().setDoNotEmail(donotEmail.get());
                    }
                    if (hasUpdatedTextOffers) {
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
        hasUpdatedEmailOffers = true;
    }

    public ObservableBoolean getTextOffers() {
        return textOffers;
    }

    public void setTextOffers(boolean value) {
        textOffers.set(value);
        hasUpdatedTextOffers = true;
    }

    public ObservableBoolean getIsEditing() {
        return isEditing;
    }

    public void setProfileSharedViewModel(ProfileSharedViewModel profileSharedViewModel) {
        this.profileSharedViewModel = profileSharedViewModel;
    }

    public void save(boolean hasConnection) {
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
    }
}
