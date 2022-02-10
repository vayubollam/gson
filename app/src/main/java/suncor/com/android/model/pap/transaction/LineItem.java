package suncor.com.android.model.pap.transaction;

import java.util.List;

public  class LineItem {

    private double unitPrice;
    private String itemId;
    private String itemDescription;
    private String posCode;
    private double quantity;
    private double total;
    private boolean isFuelItem;
    private List<Offer> offers;


    //add properties
    public static final class Offer {
        //Do-Nothing
    }

}
