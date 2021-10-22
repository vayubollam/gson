package suncor.com.android.ui.main.actionmenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;

public class ActionMenuViewModel extends ViewModel {

    private final PapRepository repository;
    private final SettingsApi settingsApi;

    @Inject
    public ActionMenuViewModel(PapRepository repository, SettingsApi settingsApi) {
        this.repository = repository;
        this.settingsApi = settingsApi;
    }

    LiveData<Resource<ActiveSession>> getActiveSession() {
        return repository.getActiveSession();
    }

    LiveData<Integer> getGeoFenceLimit() {
        return Transformations.map(settingsApi.retrieveSettings(), result -> {
            if (result.status == Resource.Status.SUCCESS) {
                return result.data.getSettings().getPap().getGeofenceDistanceMeters();
            }
            return 70;
        });
    }
}
