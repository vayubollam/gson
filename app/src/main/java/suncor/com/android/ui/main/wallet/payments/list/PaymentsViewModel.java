package suncor.com.android.ui.main.wallet.payments.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.data.payments.PaymentsRepository;
import suncor.com.android.model.Resource;
import suncor.com.android.model.payments.PaymentDetail;
import suncor.com.android.ui.common.Event;


public class PaymentsViewModel extends ViewModel {

    private final PaymentsRepository repository;
    private MediatorLiveData<ViewState> _viewState = new MediatorLiveData<>();
    public LiveData<ViewState> viewState = _viewState;

    private ViewState pendingViewState;

    private MutableLiveData<Event<Boolean>> retrieveCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> refreshCardsEvent = new MutableLiveData<>();
    private MutableLiveData<Calendar> dateOfUpdate = new MutableLiveData<>();

    private MutableLiveData<List<PaymentDetail>> payments = new MutableLiveData<>();
    private ArrayList<PaymentDetail> cards;

    @Inject
    public PaymentsViewModel(PaymentsRepository repository) {
        this.repository = repository;
        cards = new ArrayList<>();

        MediatorLiveData<Resource<ArrayList<PaymentDetail>>> apiCall = new MediatorLiveData<>();
        LiveData<Resource<ArrayList<PaymentDetail>>> retrieveCall = Transformations.switchMap(retrieveCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                pendingViewState = ViewState.LOADING;
                return repository.getPayments(true);
            }
            return new MutableLiveData<>();
        });

        LiveData<Resource<ArrayList<PaymentDetail>>> refreshCall = Transformations.switchMap(refreshCardsEvent, event -> {
            if (event.getContentIfNotHandled() != null) {
                pendingViewState = ViewState.REFRESHING;
                return repository.getPayments(true);
            }
            return new MutableLiveData<>();
        });

        apiCall.addSource(retrieveCall, apiCall::setValue);
        apiCall.addSource(refreshCall, apiCall::setValue);

        apiCall.observeForever((result) -> {
            if (result.status != Resource.Status.LOADING) {
                //even in error state, we may get some data
                if (result.data != null) {
                    saveCards(result.data);
                }
            }
        });

        _viewState.addSource(apiCall, result -> {
            switch (result.status) {
                case SUCCESS:
                    _viewState.setValue(ViewState.SUCCESS);
                    break;
                case LOADING:
                    _viewState.setValue(pendingViewState);
                    break;
                case ERROR:
                    _viewState.setValue(ViewState.FAILED);
                    break;
            }
        });
    }

    public void onAttached() {
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public MutableLiveData<Calendar> getDateOfUpdate() {
        return dateOfUpdate;
    }

    private void saveCards(List<PaymentDetail> cards) {
        this.cards.clear();
        this.cards.addAll(cards);

        payments.setValue(cards);

        dateOfUpdate.setValue(repository.getTimeOfLastUpdate());
    }

    public void retryAgain() {
        retrieveCardsEvent.setValue(Event.newEvent(true));
    }

    public void refreshPayments() {
        refreshCardsEvent.setValue(Event.newEvent(true));
    }

    public LiveData<List<PaymentDetail>> getPayments() {
        return payments;
    }

    public enum ViewState {
        LOADING, FAILED, SUCCESS, REFRESHING
    }

    public int getIndexofPaymentDetail(PaymentDetail PaymentDetail) {
        return cards.indexOf(PaymentDetail);
    }
}
