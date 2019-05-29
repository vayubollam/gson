package suncor.com.android.data.repository.transcations;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.ArrayList;

import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;

public interface TransactionApi {
    LiveData<Resource<ArrayList<Transaction>>> getTransactions(String cardId, int startMonth, int monthsBack, Gson gson);
}
