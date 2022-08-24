package suncor.com.android.data.redeem;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.model.redeem.response.MemberEligibilityResponse;

public interface MerchantsApi {

    LiveData<Resource<ArrayList<Merchant>>> retrieveMerchants();
    LiveData<Resource<MemberEligibilityResponse>> getMemberEligibility();
}
