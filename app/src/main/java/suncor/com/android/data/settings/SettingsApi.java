package suncor.com.android.data.settings;

import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;

public interface SettingsApi {
    LiveData<Resource<SettingsResponse>> retrieveSettings();
}