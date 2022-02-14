package suncor.com.android.ui.main.pap.selectpump;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.GetRedeemableFlag;
import suncor.com.android.model.pap.P97StoreDetailsResponse;

public class SelectPumpViewModel extends ViewModel {

    private final PapRepository repository;
    public MutableLiveData<Resource<GetRedeemableFlag>> redeemableFlag = new MutableLiveData<>();

    @Inject
    SelectPumpViewModel(PapRepository repository) {
        this.repository = repository;
    }


    public LiveData<Resource<P97StoreDetailsResponse>> getStoreDetails(String storeId) {
        return repository.getStoreDetails(storeId);
    }

    LiveData<Resource<Boolean>> isPAPAvailable(String storeId) {
        return Transformations.map(getStoreDetails(storeId), result ->
                new Resource<>(result.status,
                        result.data != null && result.data.mobilePaymentStatus.getPapAvailable(),
                        result.message));
    }

    public void getRedeemableFlag(String stateCode) {

        repository.getRedeemableFlag(stateCode).observeForever(it -> {
            switch (it.status) {
                case LOADING:
                    redeemableFlag.postValue(Resource.loading());

                case SUCCESS:
                    redeemableFlag.postValue(Resource.success(it.data != null ? it.data : null));

                case ERROR:
                    redeemableFlag.postValue(Resource.error(it.message));

            }
        });
    }
}
