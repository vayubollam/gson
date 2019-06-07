package suncor.com.android.data.repository.transcations;

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

public class TransactionsMock implements TransactionApi {
    @Override
    public LiveData<Resource<ArrayList<Transaction>>> getTransactions(String cardId, int startMonth, int monthsBack, Gson gson) {
        MutableLiveData<Resource<ArrayList<Transaction>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String jsonText;
            if (startMonth == 6) {
                jsonText = "[\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"redemption\",\n" +
                        "                                \"date\": \"2019-06-11T00:00:00\",\n" +
                        "                                \"basePoints\": -10000,\n" +
                        "                                \"bonusPoints\": 0,\n" +
                        "                                \"totalPoints\": -10000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"SWP PPTS SUPERWORKS\",\n" +
                        "                                \"locationAddress\": \"3100 Ellesmere Road, Scarborough ON - M1E4C2\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"purchase\",\n" +
                        "                                \"date\": \"2019-06-09T00:00:00\",\n" +
                        "                                \"basePoints\": 304,\n" +
                        "                                \"bonusPoints\": 183,\n" +
                        "                                \"totalPoints\": 487,\n" +
                        "                                \"purchaseAmount\": 32.91,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"3100 Ellesmere Road, Scarborough ON - M1E4C2\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"bonus\",\n" +
                        "                                \"date\": \"2019-06-11T00:00:00\",\n" +
                        "                                \"basePoints\": 0,\n" +
                        "                                \"bonusPoints\": 1000,\n" +
                        "                                \"totalPoints\": 1000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"Online/Petro-Points Partner\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"bonus\",\n" +
                        "                                \"date\": \"2019-06-11T00:00:00\",\n" +
                        "                                \"basePoints\": 0,\n" +
                        "                                \"bonusPoints\": 1000,\n" +
                        "                                \"totalPoints\": 1000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"Online/Petro-Points Partner\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"bonus\",\n" +
                        "                                \"date\": \"2019-06-11T00:00:00\",\n" +
                        "                                \"basePoints\": 0,\n" +
                        "                                \"bonusPoints\": 1000,\n" +
                        "                                \"totalPoints\": 1000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"Online/Petro-Points Partner\"\n" +
                        "                            }\n" +
                        "                        ]";
            } else if (startMonth == 4) {
                jsonText = "[\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"redemption\",\n" +
                        "                                \"date\": \"2019-04-09T00:00:00\",\n" +
                        "                                \"basePoints\": -10000,\n" +
                        "                                \"bonusPoints\": 0,\n" +
                        "                                \"totalPoints\": -10000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"SWP PPTS SUPERWORKS\",\n" +
                        "                                \"locationAddress\": \"3100 Ellesmere Road, Scarborough ON - M1E4C2\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"purchase\",\n" +
                        "                                \"date\": \"2019-04-09T00:00:00\",\n" +
                        "                                \"basePoints\": 304,\n" +
                        "                                \"bonusPoints\": 183,\n" +
                        "                                \"totalPoints\": 487,\n" +
                        "                                \"purchaseAmount\": 32.91,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"3100 Ellesmere Road, Scarborough ON - M1E4C2\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"bonus\",\n" +
                        "                                \"date\": \"2019-04-11T00:00:00\",\n" +
                        "                                \"basePoints\": 0,\n" +
                        "                                \"bonusPoints\": 1000,\n" +
                        "                                \"totalPoints\": 1000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"Online/Petro-Points Partner\"\n" +
                        "                            },\n" +
                        "                            {\n" +
                        "                                \"transactionType\": \"bonus\",\n" +
                        "                                \"date\": \"2019-04-11T00:00:00\",\n" +
                        "                                \"basePoints\": 0,\n" +
                        "                                \"bonusPoints\": 1000,\n" +
                        "                                \"totalPoints\": 1000,\n" +
                        "                                \"purchaseAmount\": 0.00,\n" +
                        "                                \"rewardDescription\": \"\",\n" +
                        "                                \"locationAddress\": \"Online/Petro-Points Partner\"\n" +
                        "                            }\n" +
                        "                        ]";
            } else {
                jsonText = "[]";
            }

            try {
                if (true) {
                    Transaction[] transactions = gson.fromJson(jsonText, Transaction[].class);
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
