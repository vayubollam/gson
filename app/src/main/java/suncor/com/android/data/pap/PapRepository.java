package suncor.com.android.data.pap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

import javax.inject.Inject;
import javax.inject.Singleton;

import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;

@Singleton
public class PapRepository {

    private PapApi papApi;
    private ActiveSession cachedActiveSession;

    @Inject
    public PapRepository(PapApi papApi, SessionManager sessionManager) {
        this.papApi = papApi;
        sessionManager.getLoginState().observeForever((state) -> {
            if (state == SessionManager.LoginState.LOGGED_OUT && cachedActiveSession != null) {
                cachedActiveSession = null;
            }
        });
    }

    public LiveData<Resource<ActiveSession>> getActiveSession() {
        MediatorLiveData<Resource<ActiveSession>> result = new MediatorLiveData<>();
        return Transformations.map(papApi.activeSession(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                cachedActiveSession = resource.data;
                return Resource.success(cachedActiveSession);
            } else if (resource.status == Resource.Status.ERROR) {
                if (cachedActiveSession != null) {
                    cachedActiveSession = null;
                }
                return resource;
            } else if (resource.status == Resource.Status.LOADING) {
                return Resource.loading(cachedActiveSession);
            } else {
                return resource;
            }
        });
    }
}
