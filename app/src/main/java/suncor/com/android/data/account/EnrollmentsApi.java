package suncor.com.android.data.account;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.model.account.EnrollmentPointsAndHours;
import suncor.com.android.model.account.NewEnrollment;
import suncor.com.android.model.account.SecurityQuestion;

public interface EnrollmentsApi {
    LiveData<Resource<EnrollmentPointsAndHours>> registerAccount(NewEnrollment account);

    LiveData<Resource<ArrayList<SecurityQuestion>>> fetchSecurityQuestions();

    LiveData<Resource<CardStatus>> checkCardStatus(String cardNumber, String postalCode, String lastName);

}
