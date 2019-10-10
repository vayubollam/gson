package suncor.com.android.ui.main.actionmenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import suncor.com.android.ui.common.Event;

public class ActionMenuViewModel extends ViewModel {
    private MutableLiveData<Event<Boolean>> _navigateToPetroPoints = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToPetroPoints = _navigateToPetroPoints;

    private MutableLiveData<Event<Boolean>> _navigateToProfile = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToProfile = _navigateToProfile;

    private MutableLiveData<Event<Boolean>> _navigateToCarWash = new MutableLiveData<>();
    public LiveData<Event<Boolean>> navigateToCarWash = _navigateToCarWash;

    public void navigateToPetroPoints() {
        _navigateToPetroPoints.setValue(Event.newEvent(true));
    }

    public void navigateToProfile() {
        _navigateToProfile.setValue(Event.newEvent(true));
    }

    public void navigateToCarWash() {
        _navigateToCarWash.setValue(Event.newEvent(true));
    }
}
