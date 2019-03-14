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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.R;
import suncor.com.android.databinding.EnrollmentFormFragmentBinding;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.enrollement.EnrollmentActivity;
import suncor.com.android.uicomponents.SuncorSelectInputLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class EnrollmentFormFragment extends Fragment implements OnBackPressedListener {

    EnrollmentFormFragmentBinding binding;
    ArrayList<SuncorTextInputLayout> requiredFields = new ArrayList<>();
    private EnrollmentFormViewModel viewModel;

    public EnrollmentFormFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EnrollmentFormFragmentBinding.inflate(inflater, container, false);
        viewModel = ViewModelProviders.of(this).get(EnrollmentFormViewModel.class);
        binding.setEventHandler(this);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener((v) -> {
            onBackPressed();
        });
        requiredFields.add(binding.firstNameInput);
        requiredFields.add(binding.lastNameInput);
        requiredFields.add(binding.emailInput);
        requiredFields.add(binding.passwordInput);
        requiredFields.add(binding.streetAddressInput);
        requiredFields.add(binding.cityInput);
        requiredFields.add(binding.provinceInput);
        requiredFields.add(binding.postalcodeInput);

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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        if (viewModel.oneItemFilled()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(R.string.enrollment_leave_alert_title);
            alertDialog.setMessage(R.string.enrollment_leave_alert_message);
            alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> Navigation.findNavController(getView()).navigateUp());
            alertDialog.setNegativeButton(R.string.cancel, null);
            alertDialog.show();
        } else {
            Navigation.findNavController(getView()).navigateUp();
        }
    }

    public void joinButtonClicked() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        int itemWithError = viewModel.canJoin();
        if (itemWithError != -1) {
            focusOnItem(requiredFields.get(itemWithError));
        } else {
            //TODO join
            Toast.makeText(getContext(), "Will Join", Toast.LENGTH_LONG).show();
        }
    }


    public void textChanged(SuncorTextInputLayout input, Editable s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        if (input == binding.streetAddressInput || input == binding.passwordInput) {
            scrollToView(input);
        }
    }

    //Applied on input fields
    public void focusChanged(View view, boolean hasFocus) {
        if (hasFocus) {
            scrollToView(view);
        }
        if (view == binding.passwordInput) {
            //we post the change on the scrollview handler to wait for scrolling event completion
            binding.scrollView.post(() -> viewModel.getPasswordField().setHasFocus(hasFocus));
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

    private void focusOnItem(SuncorTextInputLayout input) {
        //if the edittext has already focus, just scroll to it
        if (input.getEditText().hasFocus()) {
            scrollToView(input);
        } else {
            input.getEditText().requestFocus();
        }
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
