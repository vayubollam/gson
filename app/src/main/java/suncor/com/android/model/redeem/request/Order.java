package suncor.com.android.model.redeem.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Order implements Parcelable {

    @SerializedName("transaction")
    private RedeemTransaction redeemTransaction;
    private ShoppingCart shoppingCart;
    private boolean linkProductsToAccount;


    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public void setRedeemTransaction(RedeemTransaction redeemTransaction) {
        this.redeemTransaction = redeemTransaction;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Order(RedeemTransaction redeemTransaction, ShoppingCart shoppingCart) {
        this.redeemTransaction = redeemTransaction;
        this.shoppingCart = shoppingCart;
    }

    public RedeemTransaction getRedeemTransaction() {
        return redeemTransaction;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public boolean isLinkProductsToAccount() {
        return linkProductsToAccount;
    }

    public void setLinkProductsToAccount(boolean linkProductsToAccount) {
        this.linkProductsToAccount = linkProductsToAccount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {

        dest.writeParcelable(redeemTransaction, flag);
        dest.writeParcelable(shoppingCart, flag);
        dest.writeInt(linkProductsToAccount ? 1 : 0);
    }

    protected Order(Parcel in) {
        redeemTransaction = in.readParcelable(RedeemTransaction.class.getClassLoader());
        shoppingCart = in.readParcelable(ShoppingCart.class.getClassLoader());
        linkProductsToAccount = in.readInt() == 1;
    }

}
