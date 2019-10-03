package suncor.com.android.model.redeem.response;

import android.os.Parcel;
import android.os.Parcelable;


public class Shipping implements Parcelable {

    private String emailSentTo;

    protected Shipping(Parcel in) {
        emailSentTo = in.readString();
    }

    public Shipping(String emailSentTo) {
        this.emailSentTo = emailSentTo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(emailSentTo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Shipping> CREATOR = new Creator<Shipping>() {
        @Override
        public Shipping createFromParcel(Parcel in) {
            return new Shipping(in);
        }

        @Override
        public Shipping[] newArray(int size) {
            return new Shipping[size];
        }
    };

    public String getEmailSentTo() {
        return emailSentTo;
    }

    public void setEmailSentTo(String emailSentTo) {
        this.emailSentTo = emailSentTo;
    }
}
