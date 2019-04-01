package suncor.com.android.ui.enrollment.form;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.account.EnrollmentsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.SecurityQuestion;

public class SecurityQuestionViewModel extends ViewModel {
    private EnrollmentsApi enrollmentsApi;
    public MutableLiveData<Resource<ArrayList<SecurityQuestion>>> securityQuestions = new MutableLiveData<>();
    public ArrayList<SecurityQuestion> questionArrayList = new ArrayList<>();
    public int selectedItem;

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @Inject
    public SecurityQuestionViewModel(EnrollmentsApi enrollmentsApi) {
        this.enrollmentsApi = enrollmentsApi;
        setSelectedItem(-1);
    }

    public void fetchQuestion() {
        enrollmentsApi.fetchSecurityQuestions().observeForever((arrayListResource) -> {
            switch (arrayListResource.status) {
                case LOADING:
                    securityQuestions.postValue(Resource.loading());
                    break;
                case ERROR:
                    securityQuestions.postValue(Resource.error(arrayListResource.message));
                    break;
                case SUCCESS:
                    securityQuestions.postValue(Resource.success(arrayListResource.data));
                    questionArrayList = arrayListResource.data;
                    break;
            }

        });
    }
}
