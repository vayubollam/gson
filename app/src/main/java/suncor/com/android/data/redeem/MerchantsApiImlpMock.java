package suncor.com.android.data.redeem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.model.redeem.response.MemberEligibilityResponse;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.CommonUtils.getMockResponse;

public class MerchantsApiImlpMock implements MerchantsApi {
    private Gson gson;
    private String responseJson = getMockResponse(null, "merchantsApiResponse.json");

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

    @Override
    public LiveData<Resource<MemberEligibilityResponse>> getMemberEligibility() {
        return null;
    }
}
