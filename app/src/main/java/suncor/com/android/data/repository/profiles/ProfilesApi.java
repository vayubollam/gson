package suncor.com.android.data.repository.profiles;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.utilities.Consumer;

public interface ProfilesApi {
    void retrieveProfile(Consumer<Profile> successCallback, Consumer<String> errorCallback);

    LiveData<Resource<Boolean>> updateProfile(ProfileRequest profileRequest);
}
