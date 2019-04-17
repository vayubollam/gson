package suncor.com.android.model.canadapost;

import com.google.gson.annotations.SerializedName;

public class CanadaPostSuggestion {
    @SerializedName("Id")
    private String id;
    @SerializedName("Text")
    private String text;
    @SerializedName("Description")
    private String description;
    @SerializedName("Next")
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
