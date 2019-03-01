package suncor.com.android.ui.enrollement.form;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import suncor.com.android.R;
import suncor.com.android.databinding.EnrollmentFormFragmentBinding;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.enrollement.EnrollmentActivity;
import suncor.com.android.uicomponents.SuncorSelectInputLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class EnrollmentFormFragment extends Fragment implements OnBackPressedListener {

    EnrollmentFormFragmentBinding binding;
    ArrayList<Pair<SuncorTextInputLayout, Integer>> requiredInputFieldsWithErrors = new ArrayList<>();

    public EnrollmentFormFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EnrollmentFormFragmentBinding.inflate(inflater, container, false);
        binding.setEventHandler(this);
        binding.appBar.setNavigationOnClickListener((v) -> {
            onBackPressed();
        });
        requiredInputFieldsWithErrors.add(new Pair(binding.firstNameInput, R.string.enrollment_first_name_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.lastNameInput, R.string.enrollment_last_name_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.emailInput, R.string.enrollment_email_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.passwordInput, R.string.enrollment_password_empty_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.streetAddressInput, R.string.enrollment_street_address_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.cityInput, R.string.enrollment_city_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.provinceInput, R.string.enrollment_province_error));
        requiredInputFieldsWithErrors.add(new Pair(binding.postalcodeInput, R.string.enrollment_postalcode_error));

        ((EnrollmentActivity) getActivity()).setOnBackPressedListener(this);
        binding.phoneInput.getEditText().addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        binding.postalcodeInput.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4 && s.charAt(s.length() - 1) != ' ') {
                    s.insert(3, " ");
                }
            }
        });
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
        if (oneItemFilled()) {
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

    public void joinButtonClicked() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        boolean isRequiredItemsFilled = true;
        boolean firstItemFocused = false;
        for (Pair<SuncorTextInputLayout, Integer> input : requiredInputFieldsWithErrors) {
            if (TextUtils.isEmpty(input.first.getText())) {
                isRequiredItemsFilled = false;
                if (input.first != binding.passwordInput || !firstItemFocused) {
                    input.first.setError(input.second);
                }
                if (!firstItemFocused) {
                    focusOnItem(input.first);
                    firstItemFocused = true;
                }
            } else {
                input.first.clearFocus();
            }
        }

        if (isRequiredItemsFilled) {
            Toast.makeText(getContext(), "Will Join", Toast.LENGTH_LONG).show();
        }
    }


    public void textChanged(SuncorTextInputLayout input, Editable s) {
        if (s.length() > 0) {
            input.post(() -> {
                input.setError("");
            });
        }
        if (input == binding.streetAddressInput || input == binding.passwordInput) {
            scrollToView(input);
        }
    }

    public void focusChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            scrollToView(view);
        }
    }

    private void scrollToView(View view) {
        binding.scrollView.post(() -> {
            int viewYPosition = view.getTop();
            int scrollPosition;
            if (view == binding.streetAddressInput || view == binding.passwordInput) {
                scrollPosition = viewYPosition;
            } else {
                int halfHeight = binding.scrollView.getHeight() / 2 - view.getHeight() / 2;
                scrollPosition = Math.max(viewYPosition - halfHeight, 0);
            }
            binding.scrollView.smoothScrollTo(0, scrollPosition);
        });
    }

    private boolean oneItemFilled() {
        for (Pair<SuncorTextInputLayout, Integer> input : requiredInputFieldsWithErrors) {
            if (!TextUtils.isEmpty(input.first.getText())) {
                return true;
            }
        }
        return !(TextUtils.isEmpty(binding.phoneInput.getText())
                && TextUtils.isEmpty(binding.securityQuestionInput.getText())
                && TextUtils.isEmpty(binding.securityAnswerInput.getText()));
    }

    private void focusOnItem(SuncorTextInputLayout input) {
        input.getEditText().requestFocus();
        if (!(input instanceof SuncorSelectInputLayout)) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input.getEditText(), InputMethodManager.SHOW_IMPLICIT);
        }
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
