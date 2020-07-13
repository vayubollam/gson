package suncor.com.android.ui.main.actionmenu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;

public class ActionMenuViewModel extends ViewModel {

    private final PapRepository repository;

    @Inject
    ActionMenuViewModel(PapRepository repository) {
        this.repository = repository;
    }

    LiveData<Resource<ActiveSession>> getActiveSession() {
        return repository.getActiveSession();
    }
}
