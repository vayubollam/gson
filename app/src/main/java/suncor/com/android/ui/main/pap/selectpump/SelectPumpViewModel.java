package suncor.com.android.ui.main.pap.selectpump;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;

public class SelectPumpViewModel extends ViewModel {

    private final PapRepository repository;

    @Inject
    SelectPumpViewModel(PapRepository repository) {
        this.repository = repository;
    }


    LiveData<Resource<ActiveSession>> getStoreDetails(String storeId) {
        return repository.getStoreDetails(storeId);
    }
}
