package suncor.com.android.ui.main.pap.receipt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.pap.transaction.Transaction;

public class ReceiptViewModel extends ViewModel {
    private final PapRepository papRepository;

    @Inject
    public ReceiptViewModel(PapRepository papRepository) {
        this.papRepository = papRepository;
    }

    public LiveData<Resource<Transaction>> getTransactionDetails(String transactionId, boolean isPartnerTransactionId) {
        return papRepository.getTransactionDetails(transactionId, isPartnerTransactionId);
    }
}
