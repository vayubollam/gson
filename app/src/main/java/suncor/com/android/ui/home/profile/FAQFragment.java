package suncor.com.android.ui.home.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FaqFragmentBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

public class FAQFragment extends DaggerFragment {
    public static final String FAQ_FRAGMENT_TAG = "FAQ_FRAGMENT";
    private FaqFragmentBinding binding;
    private FAQViewModel faqViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.faq_fragment, container, false);
        faqViewModel = ViewModelProviders.of(this, viewModelFactory).get(FAQViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.questionsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.questionsRecycler.setAdapter(new FAQAdapter(faqViewModel.getQuestions(), getContext()));
        binding.appBar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());

    }

}
