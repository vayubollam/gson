package suncor.com.android.data.repository.cards;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;

public class CardsApiMock implements CardsApi {
    @Override
    public LiveData<Resource<ArrayList<CardDetail>>> retrieveCards() {
        MutableLiveData<Resource<ArrayList<CardDetail>>> result = new MutableLiveData<>();
        ArrayList<CardDetail> cards = new ArrayList<>();
        Gson gson = new Gson();
        cards.addAll(Arrays.asList(gson.fromJson(responseJson, CardDetail[].class)));
        result.postValue(Resource.success(cards));
        return result;
    }

    private String responseJson = "[{\n" +
            "                        \"cardType\": \"PPTS\",\n" +
            "                        \"cardNumber\": \"706988500050292051\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"11LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"pointsBalance\": 12345\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"FSR\",\n" +
            "                        \"cardNumber\": \"706988500050292051\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"litresRemaining\": 12,\n" +
            "                        \"cpl\": 0.05\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"PPC\",\n" +
            "                        \"cardNumber\": \"706999700401020280\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"56PawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"litresRemaining\": 12,\n" +
            "                        \"cpl\": 0.10\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"WAG\",\n" +
            "                        \"cardNumber\": \"706981001000120561\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"11LawF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"unitsRemaining\": 12\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"SP\",\n" +
            "                        \"cardNumber\": \"706981003009633263\",\n" +
            "                        \"cardNumberEncrypted\": \"25AbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\",\n" +
            "                        \"daysRemaining\": 12\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"MORE\",\n" +
            "                        \"cardNumber\": \"60476748007905331\",\n" +
            "                        \"cardNumberEncrypted\": \"198GbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"CAA\",\n" +
            "                        \"cardNumber\": \"6202909505788005\",\n" +
            "                        \"cardNumberEncrypted\": \"94iIbCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"25LawF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"HBC\",\n" +
            "                        \"cardNumber\": \"600294472306670\",\n" +
            "                        \"cardNumberEncrypted\": \"33oPCF1v1Or4s1D4s0kJEA==\",\n" +
            "                        \"serviceId\": \"24HuwF1v1er4s1D4s0kJEA==\"\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"cardType\": \"RBC\"\n" +
            "                    }]";
}
