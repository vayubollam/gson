package suncor.com.android.ui.main.rewards;

import android.os.Parcel;
import android.os.Parcelable;

public class Reward implements Parcelable {
    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        @Override
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        @Override
        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };
    private String title;
    private String subtitle;
    private String points;
    private String description;
    private String smallImage;
    private String largeImage;

    protected Reward(Parcel in) {
        title = in.readString();
        subtitle = in.readString();
        points = in.readString();
        description = in.readString();
        smallImage = in.readString();
        largeImage = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public String getLargeImage() {
        return largeImage;
    }

    public String getSmallImage() {
        return smallImage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(points);
        dest.writeString(description);
        dest.writeString(smallImage);
        dest.writeString(largeImage);
    }
}
