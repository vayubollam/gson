package suncor.com.android.data.resetpassword;

import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.resetpassword.ResetPasswordRequest;
import suncor.com.android.model.resetpassword.SecurityQuestion;

public interface ForgotPasswordProfileApi {

    LiveData<Resource<Boolean>> generateResetPasswordEmail(String email);
    LiveData<Resource<SecurityQuestion>> getSecurityQuestionToResetPassword(String GUID);
    LiveData<Resource<String>> validateSecurityQuestion(String questionId,String answer, String profileIdEncrypted, String GUID);
    LiveData<Resource<Boolean>> resetPassword(String profileIdEncrypted, String GUID, ResetPasswordRequest resetPasswordRequest);

}
