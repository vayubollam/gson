package suncor.com.android.ui.main.pap.selectpump;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.P97StoreDetailsResponse;

public class SelectPumpViewModel extends ViewModel {

    private final PapRepository repository;

    @Inject
    SelectPumpViewModel(PapRepository repository) {
        this.repository = repository;
    }


    LiveData<Resource<P97StoreDetailsResponse>> getStoreDetails(String storeId) {
        return repository.getStoreDetails(storeId);
    }

    LiveData<Resource<Boolean>> isPAPAvailable(String storeId) {
        return Transformations.map(getStoreDetails(storeId), result ->
                new Resource<>(result.status,
                        result.data != null && result.data.mobilePaymentStatus.getPapAvailable(),
                        result.message));
    }
}
