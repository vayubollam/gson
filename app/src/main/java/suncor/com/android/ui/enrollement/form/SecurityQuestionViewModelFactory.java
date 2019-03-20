package suncor.com.android.ui.enrollement.form;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import suncor.com.android.data.repository.account.FetchSecurityQuestionApi;

public class SecurityQuestionViewModelFactory implements ViewModelProvider.Factory {
    private FetchSecurityQuestionApi fetchSecurityQuestionApi;

    public SecurityQuestionViewModelFactory(FetchSecurityQuestionApi fetchSecurityQuestionApi) {
        this.fetchSecurityQuestionApi = fetchSecurityQuestionApi;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SecurityQuestionViewModel.class)) {
            return (T) new SecurityQuestionViewModel(fetchSecurityQuestionApi);
        }
        throw new IllegalArgumentException("Unknown ViewModel Class");
    }
}
