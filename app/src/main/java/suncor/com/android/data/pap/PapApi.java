package suncor.com.android.data.pap;


import androidx.lifecycle.LiveData;

import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.ActiveSession;

public interface PapApi {
    LiveData<Resource<ActiveSession>> activeSession();
}
