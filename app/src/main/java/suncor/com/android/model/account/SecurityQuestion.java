package suncor.com.android.model.account;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import androidx.annotation.Nullable;

public class SecurityQuestion {

    @SerializedName("questionId")
    private String id;
    @SerializedName("question")
    String question;

    public SecurityQuestion(String id,  String question) {
        this.id = id;
        this.question = question;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof SecurityQuestion)) {
            return false;
        }
        return ((SecurityQuestion) obj).id.equals(this.id);
    }
}
