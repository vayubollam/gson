package suncor.com.android.ui.main.profile.help;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentFaqBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.account.Question;
import suncor.com.android.ui.main.common.BaseFragment;

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
        Drawable divider = getResources().getDrawable(R.drawable.horizontal_divider);
        int inset = getResources().getDimensionPixelSize(R.dimen.faq_question_padding);
        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, 0, 0);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(insetDivider);
        binding.questionsRecycler.addItemDecoration(dividerItemDecoration);
        binding.questionsRecycler.setAdapter(new FAQAdapter(faqViewModel.getQuestions(), getContext(), (this::selectQuestion)));
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.callUsButton.setOnClickListener(v ->
        {
            String phone = getContext().getResources().getString(R.string.customer_support_number);
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            getContext().startActivity(intent);
        });
        binding.emailButton.setOnClickListener(v ->
        {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", getContext().getResources().getString(R.string.customer_support_email), null));
            getContext().startActivity(Intent.createChooser(emailIntent, null));
        });
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-get-help-view-list";
    }

    public void selectQuestion(Question question) {
        faqViewModel.setSelectedQuestion(question);
        launchFAQResponseActivity();
    }

    private void launchFAQResponseActivity() {
        Navigation.findNavController(getView()).navigate(R.id.action_FAQFragment_to_FAQResponse);
    }

}
