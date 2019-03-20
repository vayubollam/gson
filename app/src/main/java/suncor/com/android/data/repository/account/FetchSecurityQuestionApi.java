package suncor.com.android.data.repository.account;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SecurityQuestion;

public interface FetchSecurityQuestionApi {
    LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions();
}
