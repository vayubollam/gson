package suncor.com.android.ui.home.profile;

import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.ui.common.Event;

public class ProfileSharedViewModel extends ViewModel {

    private MutableLiveData<Event<Alert>> _alertObservable = new MutableLiveData<>();
    public LiveData<Event<Alert>> alertObservable = _alertObservable;

    private MutableLiveData<Event<Integer>> _toastObservable = new MutableLiveData<>();
    public LiveData<Event<Integer>> toastObservable = _toastObservable;


    public void postAlert(Alert alert) {
        _alertObservable.postValue(Event.newEvent(alert));
    }

    public void postToast(@StringRes int message) {
        _toastObservable.postValue(Event.newEvent(message));
    }

    public static class Alert {
        @StringRes
        public int title = -1;

        @StringRes
        public int message = -1;

        @StringRes
        public int positiveButton = -1;

        public Runnable positiveButtonClick;

        @StringRes
        public int negativeButton = -1;

        public Runnable negativeButtonClick;
    }
}
