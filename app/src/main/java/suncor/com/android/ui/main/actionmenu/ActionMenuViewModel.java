package suncor.com.android.ui.main.actionmenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.ui.main.home.HomeViewModel;
import suncor.com.android.ui.main.stationlocator.StationItem;

public class ActionMenuViewModel extends ViewModel {

    private final PapRepository repository;
    private final HomeViewModel homeViewModel;

    @Inject
    ActionMenuViewModel(PapRepository repository, HomeViewModel homeViewModel) {
        this.repository = repository;
        this.homeViewModel = homeViewModel;
    }

    LiveData<Resource<ActiveSession>> getActiveSession() {
        return repository.getActiveSession();
    }

    LiveData<Resource<StationItem>> getNearestStation() {
        return homeViewModel.nearestStation;
    }
}
