package suncor.com.android.ui.main.pap.fuelup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;

public class FuelUpViewModel extends ViewModel {

    private final SettingsApi settingsApi;

    @Inject
    FuelUpViewModel(SettingsApi settingsApi) {
        this.settingsApi = settingsApi;
    }

    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
    }

}
