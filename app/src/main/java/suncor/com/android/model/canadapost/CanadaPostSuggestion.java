package suncor.com.android.model.canadapost;

import com.google.gson.annotations.SerializedName;

public class CanadaPostSuggestion {
    private String id;
    private String text;
    private String description;
    private Next next;

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }

    public Next getNext() {
        return next;
    }

    public enum Next {
        @SerializedName("Find") FIND,
        @SerializedName("Retrieve") RETRIEVE
    }
}
