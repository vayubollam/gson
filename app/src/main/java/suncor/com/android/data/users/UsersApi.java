package suncor.com.android.data.users;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;

public interface UsersApi {

    LiveData<Resource<Boolean>> createPassword(String email, String password, String emailEncrypted);
}
