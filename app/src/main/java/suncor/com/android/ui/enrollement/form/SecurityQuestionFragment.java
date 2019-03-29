package suncor.com.android.ui.enrollement.form;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import suncor.com.android.R;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.SecurityQuestion;
import suncor.com.android.uicomponents.SuncorAppBarLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecurityQuestionFragment extends Fragment {

    private ArrayList<String> questions;
    private SecurityQuestionViewModel securityQuestionViewModel;
    private EnrollmentFormViewModel enrollmentFormViewModel;


    @Inject
    ViewModelFactory viewModelFactory;

    public SecurityQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidSupportInjection.inject(this);
        securityQuestionViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(SecurityQuestionViewModel.class);
        enrollmentFormViewModel = ViewModelProviders.of(getActivity()).get(EnrollmentFormViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_security_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SuncorAppBarLayout appBarLayout = getView().findViewById(R.id.app_bar);
        appBarLayout.setNavigationOnClickListener(v -> {
            Navigation.findNavController(getView()).navigateUp();
        });
        RecyclerView questionsRecyclerView = getView().findViewById(R.id.security_question_recycler);
        questionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        questions = new ArrayList<>();
        for (SecurityQuestion question : securityQuestionViewModel.questionArrayList
        ) {
            questions.add(question.getLocalizedQuestion());
        }

        ChoiceSelectorAdapter choiceSelectorAdapter = new ChoiceSelectorAdapter(questions, (this::onSecurityQuestionSelected), securityQuestionViewModel.getSelectedItem());
        questionsRecyclerView.setAdapter(choiceSelectorAdapter);
    }

    public void onSecurityQuestionSelected(int selectedQuestion) {
        enrollmentFormViewModel.setSelectedQuestion(securityQuestionViewModel.questionArrayList.get(selectedQuestion));
        securityQuestionViewModel.setSelectedItem(selectedQuestion);

    }
}
