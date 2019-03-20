package suncor.com.android.ui.enrollement.form;

import java.util.ArrayList;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import suncor.com.android.data.repository.account.FetchSecurityQuestionApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SecurityQuestion;

public class SecurityQuestionViewModel extends ViewModel {
    private FetchSecurityQuestionApi fetchSecurityQuestionApi;
    public MutableLiveData<Resource<ArrayList<SecurityQuestion>>> securityQuestions = new MutableLiveData<>();
    public ArrayList<SecurityQuestion> questionArrayList = new ArrayList<>();
    public int selectedItem;

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    public SecurityQuestionViewModel(FetchSecurityQuestionApi fetchSecurityQuestionApi) {
        this.fetchSecurityQuestionApi = fetchSecurityQuestionApi;
        setSelectedItem(-1);
    }

    public void fetchQuestion() {
        fetchSecurityQuestionApi.fetchSecurityQuestions().observeForever((arrayListResource) -> {
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
