package suncor.com.android.data.profiles;

import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.account.DeleteAccountRequest;
import suncor.com.android.model.account.Profile;
import suncor.com.android.model.account.ProfileRequest;
import suncor.com.android.model.account.SecurityQuestion;
import suncor.com.android.utilities.Consumer;

public interface ProfilesApi {
    void retrieveProfile(Consumer<Profile> successCallback, Consumer<String> errorCallback);

    LiveData<Resource<Boolean>> updateProfile(ProfileRequest profileRequest);

    LiveData<Resource<SecurityQuestion>> getSecurityQuestion();

    LiveData<Resource<String>> validateSecurityQuestion(String answer);

    LiveData<Resource<Boolean>> deleteAccount(DeleteAccountRequest deleteAccountRequest);
}
