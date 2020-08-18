package suncor.com.android.ui.main.pap.fuelup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.pap.ActiveSession;

public class FuelUpViewModel extends ViewModel {

    private final SettingsApi settingsApi;
    private final PapRepository papRepository;

    @Inject
    FuelUpViewModel(SettingsApi settingsApi, PapRepository papRepository) {
        this.settingsApi = settingsApi;
        this.papRepository = papRepository;
    }


    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
    }


    LiveData<Resource<ActiveSession>> getActiveSession() {
        return papRepository.getActiveSession();
    }

}
