package suncor.com.android.ui.main.pap.receipt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.transaction.Transaction;
import suncor.com.android.utilities.UserLocalSettings;

public class ReceiptViewModel extends ViewModel {
    private final PapRepository papRepository;
    private final SessionManager sessionManager;

    @Inject
    public ReceiptViewModel(PapRepository papRepository, SessionManager sessionManager) {
        this.papRepository = papRepository;
        this.sessionManager = sessionManager;
    }

    public LiveData<Resource<Transaction>> getTransactionDetails(String transactionId, boolean isPartnerTransactionId) {
        return papRepository.getTransactionDetails(transactionId, isPartnerTransactionId);
    }

    public boolean isFirstTransactionOfMonth() {
        long lastStatusUpdate = sessionManager.getUserLocalSettings().getLong(UserLocalSettings.LAST_SUCCESSFUL_PAP_DATE);

        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH);

        c.setTimeInMillis(lastStatusUpdate);
        int lastSuccessMonth = c.get(Calendar.MONTH);

        return lastStatusUpdate == 0 || currentMonth != lastSuccessMonth;
    }
}
