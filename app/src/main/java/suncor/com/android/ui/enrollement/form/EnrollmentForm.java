package suncor.com.android.ui.enrollement.form;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import suncor.com.android.R;
import suncor.com.android.databinding.EnrollmentFormFragmentBinding;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.enrollement.EnrollmentActivity;

public class EnrollmentForm extends Fragment implements OnBackPressedListener {

    EnrollmentFormFragmentBinding binding;

    public EnrollmentForm() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EnrollmentFormFragmentBinding.inflate(inflater, container, false);
        binding.appBar.setNavigationOnClickListener((v) -> {
            onBackPressed();
        });

        ((EnrollmentActivity) getActivity()).setOnBackPressedListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTerms();
    }

    private void initTerms() {
        String terms = getString(R.string.enrollment_terms);
        String url = getString(R.string.enrollment_terms_url);
        String agreement = getString(R.string.enrollment_terms_agreement, terms);
        SpannableString span = new SpannableString(agreement);
        span.setSpan(new SuncorURLSpan(url), agreement.indexOf(terms), agreement.indexOf(terms) + terms.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.termsAgreement.setText(span);
        binding.termsAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onBackPressed() {
        if (onItemFilled()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(R.string.enrollment_leave_alert_title);
            alertDialog.setMessage(R.string.enrollment_leave_alert_message);
            alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {
                getFragmentManager().popBackStack();
                ((EnrollmentActivity) getActivity()).setOnBackPressedListener(null);
            });
            alertDialog.setNegativeButton(R.string.cancel, null);
            alertDialog.show();
        } else {
            getFragmentManager().popBackStack();
            ((EnrollmentActivity) getActivity()).setOnBackPressedListener(null);
        }

    }

    private boolean onItemFilled() {
        return !(TextUtils.isEmpty(binding.firstNameInput.getText())
                && TextUtils.isEmpty(binding.lastNameInput.getText())
                && TextUtils.isEmpty(binding.emailInput.getText())
                && TextUtils.isEmpty(binding.passwordInput.getText())
                && TextUtils.isEmpty(binding.securityQuestionInput.getText())
                && TextUtils.isEmpty(binding.securityAnswerInput.getText())
                && TextUtils.isEmpty(binding.securityAnswerInput.getText())
                && TextUtils.isEmpty(binding.streetAddressInput.getText())
                && TextUtils.isEmpty(binding.cityInput.getText())
                && TextUtils.isEmpty(binding.provinceInput.getText())
                && TextUtils.isEmpty(binding.postalcodeInput.getText())
                && TextUtils.isEmpty(binding.phoneInput.getText()));
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
