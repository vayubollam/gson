package suncor.com.android.ui.tutorial;

import android.net.Uri;

public class TutorialContent {
    private String header;
    private String videoPath;

    public TutorialContent(String header, String videoPath) {
        this.header = header;
        this.videoPath = videoPath;
    }

    public String getHeader() {
        return header;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public Uri getVideoUri() {
        return Uri.parse(videoPath);
    }
}
