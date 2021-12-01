package suncor.com.android.data.transcations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.Arrays;

import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.CommonUtils.getMockResponse;

public class TransactionsMock implements TransactionApi {
    @Override
    public LiveData<Resource<ArrayList<Transaction>>> getTransactions(String cardId, int startMonth, int monthsBack) {
        MutableLiveData<Resource<ArrayList<Transaction>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String jsonText;
            if (startMonth == 7) {
                jsonText = getMockResponse(null, "transactionsApiResponse.json");
            } else {
                jsonText = "[]";
            }

            try {
                if (true) {
                    Transaction[] transactions = new Gson().fromJson(jsonText, Transaction[].class);
                    result.postValue(Resource.success(new ArrayList<>(Arrays.asList(transactions))));
                } else {
                    result.postValue(Resource.error("001"));
                }
            } catch (JsonSyntaxException e) {
                result.postValue(Resource.error(ErrorCodes.GENERAL_ERROR));
                Timber.d("Transactions Api failed due to :" + e.toString());
            }

        });
        thread.start();

        return result;
    }
}
