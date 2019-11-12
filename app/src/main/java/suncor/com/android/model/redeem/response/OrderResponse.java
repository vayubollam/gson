package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

import suncor.com.android.model.redeem.request.ShoppingCart;

import com.google.gson.annotations.SerializedName;

public class OrderResponse implements Parcelable {

    private Status status;
    private Shipping shipping;
    private ShoppingCart shoppingCart;
    //private ShoppingCart shoppingCart;
    //TODO: confirm using which shoppingCart
    private Transaction transaction;
    private String orderId;

    protected OrderResponse(Parcel in) {
        shipping = in.readParcelable(Shipping.class.getClassLoader());
        shoppingCart = in.readParcelable(ShoppingCart.class.getClassLoader());
        transaction = in.readParcelable(Transaction.class.getClassLoader());
        orderId = in.readString();
        status = Status.valueOf(in.readString());
    }

    public enum Status {
        @SerializedName("ok")
        OK,
        @SerializedName("delayed")
        DELAYED
    }

    public OrderResponse(Status status, Shipping shipping, ShoppingCart shoppingCart, Transaction transaction, String orderId) {
        this.status = status;
        this.shipping = shipping;
        this.shoppingCart = shoppingCart;
        this.transaction = transaction;
        this.orderId = orderId;
    }

    public OrderResponse() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(shipping, flags);
        dest.writeParcelable(shoppingCart, flags);
        dest.writeParcelable(transaction, flags);
        dest.writeString(orderId);
        dest.writeString(status.name());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderResponse> CREATOR = new Creator<OrderResponse>() {
        @Override
        public OrderResponse createFromParcel(Parcel in) {
            return new OrderResponse(in);
        }

        @Override
        public OrderResponse[] newArray(int size) {
            return new OrderResponse[size];
        }
    };


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

