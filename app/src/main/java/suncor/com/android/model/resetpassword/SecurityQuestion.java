package suncor.com.android.model.resetpassword;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class SecurityQuestion {

    @SerializedName("questionId")
    private String id;
    @SerializedName("question")
    String question;
    @SerializedName("profileIdEncrypted")
    String profileEncrypted;

    public SecurityQuestion(String id, String question, String profileEncrypted) {
        this.id = id;
        this.question = question;
        this.profileEncrypted = profileEncrypted;
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

    public String getProfileEncrypted() {
        return profileEncrypted;
    }

    public void setProfileEncrypted(String profileEncrypted) {
        this.profileEncrypted = profileEncrypted;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !(obj instanceof SecurityQuestion)) {
            return false;
        }
        return ((SecurityQuestion) obj).id.equals(this.id);
    }
}