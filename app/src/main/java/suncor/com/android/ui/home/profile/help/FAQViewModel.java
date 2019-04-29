package suncor.com.android.ui.home.profile.help;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;
import suncor.com.android.R;
import suncor.com.android.SuncorApplication;
import suncor.com.android.model.account.Question;
import suncor.com.android.utilities.Timber;

public class FAQViewModel extends ViewModel {
    private ArrayList<Question> questions;
    private Question selectedQuestion;



    @Inject
    public FAQViewModel(SuncorApplication suncorApplication, Gson gson) {
        InputStream jsonFile = suncorApplication.getResources().openRawResource(R.raw.gethelp);
        String jsonText = new Scanner(jsonFile).useDelimiter(("\\A")).next();
        try {
            JSONArray jsonArray = new JSONArray(jsonText);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONArray jsonElements = jsonObject.getJSONArray("questions");
            Question[] questionsArray = gson.fromJson(jsonElements.toString(), Question[].class);
            questions = new ArrayList<>(Arrays.asList(questionsArray));
            Timber.d("get help :" + questions.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = questions;
    }

    public Question getSelectedQuestion() {
        return selectedQuestion;
    }

    public void setSelectedQuestion(Question selectedQuestion) {
        this.selectedQuestion = selectedQuestion;
    }
}
