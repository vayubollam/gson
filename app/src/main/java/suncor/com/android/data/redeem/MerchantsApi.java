package suncor.com.android.data.redeem;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import suncor.com.android.model.Resource;
import suncor.com.android.model.merchants.Merchant;

public interface MerchantsApi {

    LiveData<Resource<ArrayList<Merchant>>> retrieveMerchants();
}
