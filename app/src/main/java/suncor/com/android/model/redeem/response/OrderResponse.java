package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderResponse implements Parcelable {

    private Status status;
    private Shipping shipping;
    private ShoppingCart shoppingCart;
    private Transaction transaction;
    private String orderId;
    private boolean linkProductsToAccount;
    private String[] productsDelivered;
    private String errorID;
    private String errorDescription;

    public void setErrorID(String errorID) {
        this.errorID = errorID;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorID() {
        return errorID;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    protected OrderResponse(Parcel in) {
        shipping = in.readParcelable(Shipping.class.getClassLoader());
        shoppingCart = in.readParcelable(ShoppingCart.class.getClassLoader());
        transaction = in.readParcelable(Transaction.class.getClassLoader());
        orderId = in.readString();
        status = Status.valueOf(in.readString());
        linkProductsToAccount = in.readInt() == 1;
        productsDelivered = in.createStringArray();
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
        dest.writeInt(linkProductsToAccount ? 1 : 0);
        dest.writeStringArray(productsDelivered);
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

    public boolean isLinkProductsToAccount() {
        return linkProductsToAccount;
    }

    public void setLinkProductsToAccount(boolean linkProductsToAccount) {
        this.linkProductsToAccount = linkProductsToAccount;
    }

    public String[] getProductsDelivered() {
        return productsDelivered;
    }

    public void setProductsDelivered(String[] productsDelivered) {
        this.productsDelivered = productsDelivered;
    }
}

