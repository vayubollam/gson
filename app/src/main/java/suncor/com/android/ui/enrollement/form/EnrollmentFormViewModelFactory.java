package suncor.com.android.ui.enrollement.form;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.account.EmailCheckApi;

public class EnrollmentFormViewModelFactory implements ViewModelProvider.Factory {

    private final EmailCheckApi emailCheckApi;

    public EnrollmentFormViewModelFactory(EmailCheckApi emailCheckApi) {
        this.emailCheckApi = emailCheckApi;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EnrollmentFormViewModel.class)) {
            return (T) new EnrollmentFormViewModel(emailCheckApi);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
