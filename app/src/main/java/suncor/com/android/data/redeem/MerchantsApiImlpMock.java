package suncor.com.android.data.redeem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.utilities.Timber;

public class MerchantsApiImlpMock implements MerchantsApi {
    private Gson gson;
    private String responseJson = " [\n" +
            "                          {\n" +
            "                            \"merchantName\": \"Cineplex\",\n" +
            "                            \"displayOrder\": 1,\n" +
            "                            \"merchantId\": 3089,\n" +
            "                            \"eGifts\":\n" +
            "                            [\n" +
            "                                {\n" +
            "                                    \"id\": 140,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 10\",\n" +
            "                                    \"value\": 10,\n" +
            "                                    \"petroPointsRequired\": 10000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                },\n" +
            "                                {\n" +
            "                                    \"id\": 141,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 25\",\n" +
            "                                    \"value\": 25,\n" +
            "                                    \"petroPointsRequired\": 25000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                },                               {\n" +
            "                                    \"id\": 142,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 50\",\n" +
            "                                    \"value\": 50,\n" +
            "                                    \"petroPointsRequired\": 50000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                },                               {\n" +
            "                                    \"id\": 143,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 100\",\n" +
            "                                    \"value\": 100,\n" +
            "                                    \"petroPointsRequired\": 100000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                },                               {\n" +
            "                                    \"id\": 144,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 250\",\n" +
            "                                    \"value\": 250,\n" +
            "                                    \"petroPointsRequired\": 250000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                },                               {\n" +
            "                                    \"id\": 145,\n" +
            "                                    \"description\": \"CINEPLEX E-CARD 500\",\n" +
            "                                    \"value\": 500,\n" +
            "                                    \"petroPointsRequired\": 500000,\n" +
            "                                    \"merchantId\": 3089\n" +
            "                                }\n" +
            "                            ]\n" +
            "                       },\n" +
            "                       {\n" +
            "                            \"merchantName\": \"Hudson's Bay\",\n" +
            "                            \"displayOrder\": 2,\n" +
            "                            \"merchantId\": 2288,\n" +
            "                            \"eGifts\":\n" +
            "                            [\n" +
            "                                {\n" +
            "                                    \"id\": 116,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 10.00\",\n" +
            "                                    \"value\": 10,\n" +
            "                                    \"petroPointsRequired\": 10000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                },\n" +
            "                                {\n" +
            "                                    \"id\": 117,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 25.00\",\n" +
            "                                    \"value\": 25,\n" +
            "                                    \"petroPointsRequired\": 25000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                },                                {\n" +
            "                                    \"id\": 118,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 50.00\",\n" +
            "                                    \"value\": 50,\n" +
            "                                    \"petroPointsRequired\": 50000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                },                                {\n" +
            "                                    \"id\": 119,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 100.00\",\n" +
            "                                    \"value\": 100,\n" +
            "                                    \"petroPointsRequired\": 100000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                },                                {\n" +
            "                                    \"id\": 120,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 250.00\",\n" +
            "                                    \"value\": 250,\n" +
            "                                    \"petroPointsRequired\": 250000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                },                                {\n" +
            "                                    \"id\": 121,\n" +
            "                                    \"description\": \"HBC E-GIFT CARD 500.00\",\n" +
            "                                    \"value\": 500,\n" +
            "                                    \"petroPointsRequired\": 500000,\n" +
            "                                    \"merchantId\": 2288\n" +
            "                                }\n" +
            "                            ]\n" +
            "                          }\n" +
            "                         ]";

    public MerchantsApiImlpMock(Gson gson) {
        this.gson = gson;
    }

    @Override
    public LiveData<Resource<ArrayList<Merchant>>> retrieveMerchants() {
        Timber.d("retrieve merchant from backend");
        MutableLiveData<Resource<ArrayList<Merchant>>> result = new MutableLiveData<>();
        result.postValue(Resource.loading());

        String jsonText = responseJson;
        Timber.d("Merchants API success, response:\n" + jsonText);

        Merchant[] merchants = gson.fromJson(jsonText, Merchant[].class);
        result.postValue(Resource.success(new ArrayList<>(Arrays.asList(merchants))));


        return result;
    }
}
