package suncor.com.android.ui.enrollement.form;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
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
import suncor.com.android.SuncorApplication;
import suncor.com.android.data.repository.account.EmailCheckApi;
import suncor.com.android.databinding.EnrollmentFormFragmentBinding;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;
import suncor.com.android.ui.enrollement.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.uicomponents.SuncorSelectInputLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

public class EnrollmentFormFragment extends Fragment implements OnBackPressedListener {

    EnrollmentFormFragmentBinding binding;
    ArrayList<SuncorTextInputLayout> requiredFields = new ArrayList<>();
    private EnrollmentFormViewModel viewModel;
    private boolean isExpanded = true;

    public EnrollmentFormFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EnrollmentFormViewModel.Factory factory = new EnrollmentFormViewModel.Factory(SuncorApplication.enrollmentsApi, SuncorApplication.emailCheckApi);
        viewModel = ViewModelProviders.of(getActivity(), factory).get(EnrollmentFormViewModel.class);
        viewModel.emailCheckLiveData.observe(this, (r) -> {
            //Ignore all results except success answers
            if (r.status == Resource.Status.SUCCESS && r.data == EmailCheckApi.EmailState.INVALID) {
                ModalDialog dialog = new ModalDialog();
                dialog.setCancelable(false);
                dialog.setTitle(getString(R.string.enrollment_invalid_email_title))
                        .setMessage(getString(R.string.enrollment_invalid_email_dialog_message))
                        .setRightButton(getString(R.string.enrollment_invalid_email_dialog_sign_in), (v) -> {
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        })
                        .setCenterButton(getString(R.string.enrollment_invalid_email_dialog_diff_email), (v) -> {
                            binding.emailInput.setText("");
                            dialog.dismiss();
                            focusOnItem(binding.emailInput);
                        })
                        .show(getFragmentManager(), ModalDialog.TAG);
            }
        });

        viewModel.joinLiveData.observe(this, (r) -> {
            //Ignore all results except success answers
            if (r.status == Resource.Status.SUCCESS) {
                getView().postDelayed(() -> getActivity().finish(), 1000);
            } else if (r.status == Resource.Status.ERROR) {
                Toast.makeText(getContext(), r.message, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EnrollmentFormFragmentBinding.inflate(inflater, container, false);
        binding.setEventHandler(this);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
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
        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());

        binding.provinceInput.setOnClickListener(v -> {
            isExpanded = binding.appBar.isExpanded();
            hideKeyBoard();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_enrollment_form_fragment_to_provinceFragment);

        });
        binding.securityQuestionInput.setOnClickListener(v -> {
            isExpanded = binding.appBar.isExpanded();
            hideKeyBoard();
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_enrollment_form_fragment_to_securityQuestionFragment);
        });
        binding.appBar.post(() -> {
            binding.appBar.setExpanded(isExpanded, false);
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
        hideKeyBoard();

        if (viewModel.oneItemFilled()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle(R.string.enrollment_leave_alert_title);
            alertDialog.setMessage(R.string.enrollment_leave_alert_message);
            alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {
                getActivity().finish();
            });
            alertDialog.setNegativeButton(R.string.cancel, null);
            alertDialog.show();
        } else {
            Navigation.findNavController(getView()).navigateUp();
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void joinButtonClicked() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        int itemWithError = viewModel.validateAndJoin();
        if (itemWithError != -1) {
            focusOnItem(requiredFields.get(itemWithError));
        }
    }


    public void textChanged(SuncorTextInputLayout input, Editable s) {
        if (!binding.getRoot().isLaidOut() || TextUtils.isEmpty(s))
            return;

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
            viewModel.getPasswordField().setHasFocus(hasFocus);
        }
        if (view == binding.emailInput) {
            viewModel.getEmailInputField().setHasFocus(hasFocus);
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
