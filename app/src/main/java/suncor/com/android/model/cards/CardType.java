package suncor.com.android.model.cards;

import com.google.gson.annotations.SerializedName;

public enum CardType {
    //Petro Points Card
    @SerializedName("PPTS")
    PPTS,

    //Fuel Saving Reward Card
    @SerializedName("FSR")
    FSR,

    //(Electronic) Preferred Price Card
    @SerializedName("PPC")
    PPC,

    //Season Pass
    @SerializedName("SP")
    SP,

    //Wash and Go Card
    @SerializedName("WAG")
    WAG,

    //More Rewards Partner Card
    @SerializedName("MORE")
    MORE,

    //CAA Partner Card
    @SerializedName("CAA")
    CAA,

    //BCAA Partner Card
    @SerializedName("BCAA")
    BCAA,

    //Hudson Bay Company Partner Card
    @SerializedName("HBC")
    HBC,

    //RBC Credit Card
    @SerializedName("RBC")
    RBC
}