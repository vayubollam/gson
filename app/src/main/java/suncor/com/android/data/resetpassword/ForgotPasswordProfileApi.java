package suncor.com.android.data.resetpassword;

import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;

public interface ForgotPasswordProfileApi {

    LiveData<Resource<Boolean>> generateResetPasswordEmail(String email);
}
