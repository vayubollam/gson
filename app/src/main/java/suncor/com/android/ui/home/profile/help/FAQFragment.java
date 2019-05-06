package suncor.com.android.ui.home.profile.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFaqBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.account.Question;
import suncor.com.android.ui.home.common.BaseFragment;

public class FAQFragment extends BaseFragment {
    private FragmentFaqBinding binding;
    private FAQViewModel faqViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFaqBinding.inflate(inflater, container, false);
        faqViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(FAQViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.questionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.questionsRecycler.setAdapter(new FAQAdapter(faqViewModel.getQuestions(), getContext(), (this::selectQuestion)));
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());

    }

    public void selectQuestion(Question question) {
        faqViewModel.setSelectedQuestion(question);
        launchFAQResponseActivity();
    }

    private void launchFAQResponseActivity() {
        Navigation.findNavController(getView()).navigate(R.id.action_FAQFragment_to_FAQResponse);
    }

}
