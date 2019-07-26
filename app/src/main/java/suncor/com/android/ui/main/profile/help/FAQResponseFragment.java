package suncor.com.android.ui.main.profile.help;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.databinding.FragmentFaqresponseBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.account.Question;
import suncor.com.android.ui.main.common.BaseFragment;

public class FAQResponseFragment extends BaseFragment {
    private FragmentFaqresponseBinding binding;
    private FAQViewModel faqViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        faqViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(FAQViewModel.class);
        binding = FragmentFaqresponseBinding.inflate(inflater, container, false);
        binding.setQuestion(faqViewModel.getSelectedQuestion());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.answerTxt.setText(faqViewModel.getSelectedQuestion().getResponse());
        binding.okButton.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
    }

    @Override
    protected String getScreenName() {
        Question question = faqViewModel.getSelectedQuestion();
        return "my-petro-points-account-get-help-view-" + question.getId();
    }
}
