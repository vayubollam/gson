package suncor.com.android.data.repository.account;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;

public interface EmailCheckApi {

    LiveData<Resource<EmailState>> checkEmail(String email);

    enum EmailState {
        VALID, INVALID, UNCHECKED
    }
}
