package suncor.com.android.data.repository.account;

import java.util.ArrayList;

import javax.annotation.Nullable;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.SecurityQuestion;

public interface EnrollmentsApi {
    LiveData<Resource<Integer>> registerAccount(NewEnrollment account);

    LiveData<Resource<EmailState>> checkEmail(String email, @Nullable String petroCanadaId);

    LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions();

    LiveData<Resource<CardStatus>> checkCardStatus(String cardNumber, String postalCode, String lastName);


    enum EmailState {
        VALID, ALREADY_REGISTERED, RESTRICTED, UNCHECKED
    }
}
