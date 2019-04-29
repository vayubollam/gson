package suncor.com.android.ui.home.profile.help;


import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFaqresponseBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

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
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_faqresponse, container, false);
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
            span = new SuncorURLSpan(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        binding.answerTxt.setText(s);
        binding.okButton.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.navigationButton.setOnClickListener(v -> getFragmentManager().popBackStack());
    }

    private class SuncorURLSpan extends android.text.style.URLSpan {

        SuncorURLSpan(String url) {
            super(url);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.gibson_semibold);
            ds.setTypeface(typeface);
            ds.setColor(getResources().getColor(R.color.red));
        }
    }
}
