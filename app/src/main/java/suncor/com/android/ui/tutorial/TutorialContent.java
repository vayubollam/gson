package suncor.com.android.ui.tutorial;

public class TutorialContent {
    private String header;
    private int imageId;

    TutorialContent(String header, int image) {
        this.header = header;
        this.imageId = image;
    }

    public String getHeader() {
        return header;
    }

    public int getImage() {
        return imageId;
    }

}
