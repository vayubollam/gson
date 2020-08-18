package suncor.com.android.ui.main.pap.fuelup;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.data.pap.PapRepository;
import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.pap.ActiveSession;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem;

public class FuelUpViewModel extends ViewModel {

    private final SettingsApi settingsApi;
    private final PapRepository papRepository;
    private final PaymentsRepository paymentsRepository;

    @Inject
    FuelUpViewModel(SettingsApi settingsApi, PapRepository papRepository, PaymentsRepository paymentsRepository) {
        this.settingsApi = settingsApi;
        this.papRepository = papRepository;
        this.paymentsRepository = paymentsRepository;
    }


    LiveData<Resource<SettingsResponse>> getSettingResponse() {
        return settingsApi.retrieveSettings();
    }


    LiveData<Resource<ActiveSession>> getActiveSession() {
        return papRepository.getActiveSession();
    }

    LiveData<Resource<ArrayList<PaymentListItem>>> getPayments(Context context) {
        return Transformations.map(paymentsRepository.getPayments(false), result -> {
            ArrayList<PaymentListItem> payments = new ArrayList<>();

            if (result.data != null) {
                for (PaymentDetail paymentDetail : result.data) {
                    payments.add(new PaymentListItem(context, paymentDetail));
                }
            }

            return new Resource(result.status, payments, result.message);
        });
    }

}
