package suncor.com.android.data.repository.account;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.NewEnrollment;
import suncor.com.android.model.Resource;

public interface EnrollmentsApi {
    public LiveData<Resource<Boolean>> registerAccount(NewEnrollment account);
}
