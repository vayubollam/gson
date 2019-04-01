package suncor.com.android.data.repository.account;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.NewEnrollment;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SecurityQuestion;

public interface EnrollmentsApi {
    LiveData<Resource<Boolean>> registerAccount(NewEnrollment account);

    LiveData<Resource<EmailCheckApi.EmailState>> checkEmail(String email);

    LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions();


    enum EmailState {
        VALID, INVALID, UNCHECKED
    }

}
