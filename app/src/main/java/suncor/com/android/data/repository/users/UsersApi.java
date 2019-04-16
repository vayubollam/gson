package suncor.com.android.data.repository.users;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;

public interface UsersApi {

    LiveData<Resource<Boolean>> createPassword(String email, String password);
}
