package suncor.com.android.model.pap;

import com.google.gson.annotations.SerializedName;

public class PayResponse {

    @SerializedName("transId")
    private String transactionId;

    @SerializedName("transStatus")
    private String transactionStatus;

}
