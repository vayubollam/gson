package suncor.com.android.ui.tutorial;

import android.graphics.drawable.Drawable;

public class TutorialContent {
    private String header;
    private Drawable image;

    public TutorialContent(String header, Drawable image) {
        this.header = header;
        this.image = image;
    }

    public String getHeader() {
        return header;
    }

    public Drawable getImage() {
        return image;
    }

}
