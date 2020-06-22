package suncor.com.android.ui.main.wallet.payments.add;

import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.cards.CardsRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.AddCardRequest;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.payments.AddPayment;
import suncor.com.android.ui.common.Event;
import suncor.com.android.ui.common.cards.CardFormat;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.wallet.cards.add.CVVInputField;
import suncor.com.android.ui.main.wallet.cards.add.CardNumberInputField;

public class AddPaymentViewModel extends ViewModel {

    private final PaymentsRepository repository;

    @Inject
    AddPaymentViewModel(PaymentsRepository repository) {
        this.repository = repository;
    }

    LiveData<Resource<Uri>> getAddPaymentEndpoint() {
        return Transformations.switchMap(repository.addPayment(), result -> {
            MutableLiveData<Resource<Uri>> data = new MutableLiveData<>();
            data.setValue(new Resource<>(result.status, result.data != null ? result.data.getEndpointUrl() : null, result.message));
            return data;
        });
    }
}
