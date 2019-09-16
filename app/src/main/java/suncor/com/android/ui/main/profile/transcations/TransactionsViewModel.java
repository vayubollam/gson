package suncor.com.android.ui.main.profile.transcations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.data.transcations.TransactionApi;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.ui.common.Event;

public class TransactionsViewModel extends ViewModel {


    public final LiveData<Resource<ArrayList<Transaction>>> transactionsLiveData;
    private final MutableLiveData<HashMap<Integer, ArrayList<Transaction>>> _transactions = new MutableLiveData<>();
    public final LiveData<HashMap<Integer, ArrayList<Transaction>>> transactions = _transactions;
    private final MutableLiveData<Event<Boolean>> loadTransactions = new MutableLiveData<>();
    private int startMonth = 0;
    private int monthsBack = 0;
    private HashMap<Integer, ArrayList<Transaction>> listHashMap = new HashMap<>();

    @Inject
    public TransactionsViewModel(SessionManager sessionManager, TransactionApi transactionApi) {
        transactionsLiveData = Transformations.switchMap(loadTransactions, (event) -> {
            if (event.getContentIfNotHandled() != null) {
                updateStartEndMonths();
                return transactionApi.getTransactions(sessionManager.getProfile().getPetroPointsNumber(), startMonth, monthsBack);
            } else {
                return new MutableLiveData<>();
            }
        });
        transactionsLiveData.observeForever(arrayListResource -> {
            if (arrayListResource.status == Resource.Status.SUCCESS) {
                if (arrayListResource.data.isEmpty()) {
                    if (transactions.getValue() == null) {
                        listHashMap.put(getCurrentMonth(0), new ArrayList<>());
                        listHashMap.put(getCurrentMonth(1), new ArrayList<>());

                    } else {
                        switch (transactions.getValue().keySet().size()) {
                            case 2:
                                listHashMap.put(getCurrentMonth(2), new ArrayList<>());
                                break;
                            case 3:
                                listHashMap.put(getCurrentMonth(3), new ArrayList<>());
                                break;
                        }
                    }
                    _transactions.setValue(listHashMap);
                    return;
                }
                listHashMap = groupByMonth(arrayListResource.data);
                if (startMonth == getCurrentMonth(0) && listHashMap.keySet().size() == 1) {
                    if (!listHashMap.containsKey(getCurrentMonth(0)))
                        listHashMap.put(getCurrentMonth(0), new ArrayList<>());
                    if (!listHashMap.containsKey(getCurrentMonth(1)))
                        listHashMap.put(getCurrentMonth(1), new ArrayList<>());
                }
                _transactions.setValue(sortTransactions(listHashMap));
            }
        });
        loadTransactions();
    }

    private HashMap<Integer, ArrayList<Transaction>> sortTransactions(HashMap<Integer, ArrayList<Transaction>> listHashMap) {
        HashMap<Integer, ArrayList<Transaction>> sortedTransactions = new HashMap<>();
        for (Integer key : listHashMap.keySet()
        ) {
            ArrayList<Transaction> sortedTransactionsArray = listHashMap.get(key);
            Collections.sort(sortedTransactionsArray);
            sortedTransactions.put(key, sortedTransactionsArray);
        }
        return sortedTransactions;
    }

    private void updateStartEndMonths() {
        if (transactions.getValue() == null) {
            startMonth = getCurrentMonth(0);
            monthsBack = 2;
            return;
        }
        switch (transactions.getValue().keySet().size()) {
            case 0:
                startMonth = getCurrentMonth(0);
                monthsBack = 2;
                break;
            case 1:
            case 2:
                startMonth = getCurrentMonth(2);
                monthsBack = 1;
                break;
            case 3:
                startMonth = getCurrentMonth(3);
                monthsBack = 1;
                break;
        }
    }

    private HashMap<Integer, ArrayList<Transaction>> groupByMonth(ArrayList<Transaction> transactions) {

        for (Transaction transaction : transactions
        ) {
            if (listHashMap.containsKey(transaction.getMonth())) {
                Objects.requireNonNull(listHashMap.get(transaction.getMonth())).add(transaction);
            } else {
                ArrayList<Transaction> transactionArrayList = new ArrayList<>();
                transactionArrayList.add(transaction);
                listHashMap.put(transaction.getMonth(), transactionArrayList);
            }

        }
        return listHashMap;
    }

    public void loadTransactions() {
        this.loadTransactions.setValue(Event.newEvent(true));
    }

    public int getCurrentMonth(int monthsToRemove) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -monthsToRemove);
        return c.get(Calendar.MONTH) + 1;
    }

    public String getMonthName(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }
}
