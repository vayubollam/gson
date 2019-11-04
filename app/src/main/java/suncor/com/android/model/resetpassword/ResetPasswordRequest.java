package suncor.com.android.model.resetpassword;

import android.os.Parcel;
import android.os.Parcelable;

public class ResetPasswordRequest implements Parcelable {

    private String profileIdEncrypted;
    private String GUID;
    private String securityAnswerEncrypted;
    private String password;


    public ResetPasswordRequest() {
    }

    protected ResetPasswordRequest(Parcel in) {
        profileIdEncrypted = in.readString();
        GUID = in.readString();
        securityAnswerEncrypted = in.readString();
        password = in.readString();
    }

    public static final Creator<ResetPasswordRequest> CREATOR = new Creator<ResetPasswordRequest>() {
        @Override
        public ResetPasswordRequest createFromParcel(Parcel in) {
            return new ResetPasswordRequest(in);
        }

        @Override
        public ResetPasswordRequest[] newArray(int size) {
            return new ResetPasswordRequest[size];
        }
    };

    public String getSecurityAnswerEncrypted() {
        return securityAnswerEncrypted;
    }

    public void setSecurityAnswerEncrypted(String securityAnswerEncrypted) {
        this.securityAnswerEncrypted = securityAnswerEncrypted;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public String getProfileIdEncrypted() {
        return profileIdEncrypted;
    }

    public void setProfileIdEncrypted(String profileIdEncrypted) {
        this.profileIdEncrypted = profileIdEncrypted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileIdEncrypted);
        parcel.writeString(GUID);
        parcel.writeString(securityAnswerEncrypted);
        parcel.writeString(password);
    }
}