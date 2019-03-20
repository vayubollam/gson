package suncor.com.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

import androidx.annotation.Nullable;

public class SecurityQuestion {

    @SerializedName("questionId")
    private String id;
    @SerializedName("questionEn")
    String engQuestion;
    @SerializedName("questionFr")
    String frQuestion;

    public SecurityQuestion(String id, String engQuestion, String frQuestion) {
        this.id = id;
        this.engQuestion = engQuestion;
        this.frQuestion = frQuestion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEngQuestion() {
        return engQuestion;
    }

    public void setEngQuestion(String engQuestion) {
        this.engQuestion = engQuestion;
    }

    public String getFrQuestion() {
        return frQuestion;
    }

    public void setFrQuestion(String frQuestion) {
        this.frQuestion = frQuestion;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof SecurityQuestion)) {
            return false;
        }
        return ((SecurityQuestion) obj).id.equals(this.id);
    }

    public String getLocalizedQuestion() {
        return Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? this.frQuestion : this.engQuestion;
    }
}
