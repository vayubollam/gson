package suncor.com.android.ui.home.profile.help;


import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import suncor.com.android.databinding.FragmentFaqresponseBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.SuncorURLSpan;

public class FAQResponse extends DaggerFragment {
    public static final String FAQ_RESPONSE_TAG = "faq_response";
    private FragmentFaqresponseBinding binding;
    private FAQViewModel faqViewModel;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        faqViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(FAQViewModel.class);
        binding = FragmentFaqresponseBinding.inflate(inflater, container, false);
        binding.setQuestion(faqViewModel.getSelectedQuestion());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.answerTxt.setText(faqViewModel.getSelectedQuestion().getResponse());
        binding.answerTxt.setAutoLinkMask(0);
        URLSpan[] spans = binding.answerTxt.getUrls();
        Spannable s = new SpannableString(binding.answerTxt.getText());
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new SuncorURLSpan(span.getURL(), getContext());
            s.setSpan(span, start, end, 0);
        }
        binding.answerTxt.setText(s);
        binding.okButton.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.appBar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }


}
