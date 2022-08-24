package suncor.com.android.ui.enrollment.form;


import static suncor.com.android.analytics.BaseAnalytics.BUTTON_TEXT_CANCEL;
import static suncor.com.android.analytics.BaseAnalytics.BUTTON_TEXT_OK;
import static suncor.com.android.analytics.Errors.PLEASE_ENTER_DIFFERENT_EMAIL;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.METHOD_ACTIVATION;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.METHOD_SIGN_UP;

import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_25;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_5;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_50;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_75;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_95;

import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.SCREEN_NAME_ACTIVATE_I_DO_NOT_HAVE_CARD;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.SCREEN_NAME_ACTIVATE_I_HAVE_CARD;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.SCREEN_NAME_ACTIVATE_SUCCESS;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.SCREEN_NAME_SIGNUP_SUCCESS;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.STEP_NAME_ADDRESS;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.STEP_NAME_COMPLETE_SIGNUP;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalytics.STEP_NAME_PERSONAL_INFORMATION;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalyticsKt.FORM_NAME_ACTIVATE_PETRO_POINTS_CARD;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalyticsKt.FORM_NAME_JOIN_PETRO_POINTS;

import static suncor.com.android.mfp.ErrorCodes.ERR_RESTRICTED_DOMAIN;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.analytics.Errors;
import suncor.com.android.analytics.enrollment.EnrollmentAnalytics;
import suncor.com.android.databinding.FragmentEnrollmentFormBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.uicomponents.ExtendedNestedScrollView;
import suncor.com.android.uicomponents.SuncorSelectInputLayout;
import suncor.com.android.uicomponents.SuncorTextInputLayout;
import suncor.com.android.utilities.SuncorPhoneNumberTextWatcher;

public class EnrollmentFormFragment extends BaseFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory viewModelFactory;

    private FragmentEnrollmentFormBinding binding;
    private ArrayList<SuncorTextInputLayout> requiredFields = new ArrayList<>();
    private EnrollmentFormViewModel viewModel;
    private boolean isExpanded = true;
    private AddressAutocompleteAdapter addressAutocompleteAdapter;
    private String formName;
    private String screenName;
    private boolean isLoadedFirstTime = false;

    private boolean scroll20 = false, scroll40 = false, scroll60 = false, scroll80 = false, scroll100 = false;

    private boolean scroll5 = false, scroll25 = false, scroll50 = false, scroll75 = false, scroll95 = false;


    // By Default it remain JOIN_NO_CARD will check if the card is provided i.e JOIN_YES_CARD.
    private String sign_up_method = METHOD_SIGN_UP;

    public EnrollmentFormFragment() {
        //do nothing
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(EnrollmentFormViewModel.class);
        viewModel.setProvincesList(((EnrollmentActivity) requireActivity()).getProvinces());

        addressAutocompleteAdapter = new AddressAutocompleteAdapter(viewModel::addressSuggestionClicked);

        if (getArguments() != null) {
            CardStatus cardStatus = EnrollmentFormFragmentArgs.fromBundle(getArguments()).getCardStatus();
            if (cardStatus != null) {
                sign_up_method = METHOD_ACTIVATION;
            }
            viewModel.setCardStatus(cardStatus);
        }

        boolean joinWithCard = viewModel.getCardStatus() != null;
        if (joinWithCard) {
            screenName = SCREEN_NAME_ACTIVATE_I_HAVE_CARD;
            formName = FORM_NAME_ACTIVATE_PETRO_POINTS_CARD;
        } else {
            screenName = SCREEN_NAME_ACTIVATE_I_DO_NOT_HAVE_CARD;
            formName = FORM_NAME_JOIN_PETRO_POINTS;
        }

        isLoadedFirstTime = true;

        viewModel.getShowDuplicateEmailEvent().observe(this, (r) -> {
            showDuplicateEmailAlert();
        });

        if (viewModel.getCardStatus() == null) {
            EnrollmentAnalytics.logFormStart(requireContext(), formName);
        } else {
            EnrollmentAnalytics.logFormStep(requireContext(), formName, STEP_NAME_PERSONAL_INFORMATION);
        }

        //enrollments api call result
        viewModel.joinLiveData.observe(this, (r) -> {

            if (r.status == Resource.Status.SUCCESS) {
                //Log success events
                String screenName;
                if (viewModel.getCardStatus() != null) {
                    screenName = SCREEN_NAME_ACTIVATE_SUCCESS;
                } else {
                    screenName = SCREEN_NAME_SIGNUP_SUCCESS;
                }

                EnrollmentAnalytics.logScreenNameClass(requireActivity(), screenName, this.getClass().getSimpleName());

                String optionsChecked = "";
                if (binding.emailOffersCheckbox.isChecked()) {
                    optionsChecked += binding.emailOffersCheckbox.getText().toString();
                }
                if (binding.smsOffersCheckbox.isChecked()) {
                    optionsChecked += binding.smsOffersCheckbox.getText().toString();
                }

                binding.emailAddress.setText(viewModel.getEmailInputField().getText());

                EnrollmentAnalytics.logFormStep(requireContext(), formName, STEP_NAME_COMPLETE_SIGNUP);

                // Log method of signup to Analytics
                EnrollmentAnalytics.logSignupEvent(requireContext(), sign_up_method);

            } else if (r.status == Resource.Status.ERROR && !EnrollmentFormViewModel.LOGIN_FAILED.equals(r.message)) {

                if (ErrorCodes.ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE.equals(r.message)) {
                    showDuplicateEmailAlert();
                } else if (ERR_RESTRICTED_DOMAIN.equals(r.message)) {

                    EnrollmentAnalytics.logFormErrorEvent(requireContext(), PLEASE_ENTER_DIFFERENT_EMAIL, formName);

                    EnrollmentAnalytics.logAlertDialogShown(requireContext(),
                            Errors.PLEASE_ENTER_DIFFERENT_EMAIL,
                            formName
                    );

                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                    dialog.setTitle(R.string.enrollment_email_restricted_alert_title);
                    dialog.setPositiveButton(R.string.ok, (d, w) -> {
                        EnrollmentAnalytics.logAlertDialogInteraction(requireContext(),
                                Errors.PLEASE_ENTER_DIFFERENT_EMAIL,
                                BUTTON_TEXT_OK, formName);
                        d.dismiss();
                        focusOnItem(binding.emailInput);
                    });

                    dialog.show();
                } else if (ErrorCodes.ERR_CARD_PENDING_EMAIL_VALIDATION.equals(r.message)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
                    dialog.setTitle(R.string.verify_your_email_address_title);
                    dialog.setMessage(R.string.verify_your_email_address_description);
                    dialog.setPositiveButton(R.string.verify_your_email_address_call_us, (d, w) -> {
                        callCostumerSupport(getString(R.string.customer_support_number));
                        d.dismiss();
                    });

                    dialog.setNegativeButton(R.string.sign_enable_fb_negative_button, (d, w) -> {
                        d.dismiss();
                    });
                    dialog.show();
                } else if (ErrorCodes.ERR_EMAIL_VALIDATION_INVALID_PUBWEB.equals(r.message)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(R.string.enrollment_email_restricted_error);
                    dialog.setPositiveButton(R.string.ok, (d, w) -> {
                        focusOnItem(binding.emailInput);
                        binding.emailInput.setError(true);
                        d.dismiss();
                    });
                    dialog.show();
                } else {
                    Alerts.prepareGeneralErrorDialog(getActivity(), formName).show();
                }
            }
        });


        //show and hide autocomplete layout
        viewModel.showAutocompleteLayout.observe(this, (show) -> {
            if (getActivity() == null || binding.appBar.isExpanded()) {
                return;
            }
            if (show) {
                if (isLoadedFirstTime) {
                    EnrollmentAnalytics.logScreenNameClass(requireContext()
                            , EnrollmentAnalytics.SCREEN_NAME_CANADA_POST_SEARCH_DESCRIPTIVE_SCREEN_NAME, this.getClass().getSimpleName());

                    isLoadedFirstTime = false;
                }
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.black_40));
                binding.appBar.setOnClickListener((v) -> viewModel.hideAutoCompleteLayout());
                binding.streetAutocompleteBackground.setVisibility(View.VISIBLE);
                binding.streetAutocompleteOverlay.setIsVisible(true);
                ViewCompat.setElevation(binding.appBar, 0);
                binding.scrollView.setScrollEnabled(false);
                requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_40_transparent));
            } else {
                binding.appBar.setOnClickListener(null);
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.white));
                binding.streetAutocompleteBackground.setVisibility(View.GONE);
                binding.streetAutocompleteOverlay.setIsVisible(false);
                ViewCompat.setElevation(binding.appBar, 8);
                binding.scrollView.setScrollEnabled(true);
                requireActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));
            }
        });

        //binding autocomplete results to adapter
        viewModel.getAutocompleteResults().observe(this, (resource ->
        {
            if (resource.status == Resource.Status.SUCCESS && resource.data.length != 0) {
                addressAutocompleteAdapter.setSuggestions(resource.data);
                binding.streetAutocompleteOverlay.autocompleteList.scrollToPosition(0);
            }
        }));

        viewModel.getAutocompleteRetrievalStatus().observe(this, resource ->
        {
            hideKeyBoard();
            binding.streetAddressInput.getEditText().clearFocus();
        });

        viewModel.getNavigateToLogin().observe(this, event ->
        {
            if (event.getContentIfNotHandled() != null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra(LoginActivity.LOGIN_FROM_ENROLLMENT_EXTRA, true);
                startActivity(intent);
                requireActivity().finish();
            }
        });
        viewModel.showBiometricAlert.observe(this, booleanEvent -> {

            EnrollmentAnalytics.logAlertDialogShown(requireContext(),
                    EnrollmentAnalytics.ALERT_TITLE_ENABLE_FINGERPRINT
                    , formName
            );


            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.sign_enable_fp_title)
                    .setMessage(R.string.sign_enable_fb_message)
                    .setPositiveButton(R.string.sign_enable_fb_possitive_button, (dialog, which) -> {
                        EnrollmentAnalytics.logAlertDialogInteraction(requireContext(),
                                EnrollmentAnalytics.ALERT_TITLE_ENABLE_FINGERPRINT,
                                getString(R.string.sign_enable_fb_possitive_button),
                                formName
                        );

                        viewModel.proccedToJoin(true);
                    })
                    .setNegativeButton(R.string.sign_enable_fb_negative_button, (dialog, which) -> {

                        EnrollmentAnalytics.logAlertDialogInteraction(requireContext(),
                                EnrollmentAnalytics.ALERT_TITLE_ENABLE_FINGERPRINT,
                                BUTTON_TEXT_CANCEL,
                                formName
                        );

                        viewModel.proccedToJoin(false);
                    })
                    .create()
                    .show();
        });
    }

    private void showDuplicateEmailAlert() {
        ModalDialog dialog = new ModalDialog();
        dialog.setFormName(formName);
        dialog.setCancelable(false);
        EnrollmentAnalytics.logFormErrorEvent(requireContext(), Errors.THE_EMAIL_HAS_ACCOUNT, formName);
        EnrollmentAnalytics.logAlertDialogShown(requireContext(), getString(R.string.enrollment_email_already_exists_title)
                        + "(" + getString(R.string.enrollment_email_already_exists_description) + ")"
                , formName);

        dialog.setTitle(getString(R.string.enrollment_email_already_exists_title))
                .setMessage(getString(R.string.enrollment_email_already_exists_description))
                .setRightButton(getString(R.string.enrollment_invalid_email_dialog_sign_in), (v) -> {
                    EnrollmentAnalytics.logAlertDialogInteraction(requireContext(), getString(R.string.enrollment_email_already_exists_title)
                                    + "(" + getString(R.string.enrollment_email_already_exists_description) + ")"
                            , getString(R.string.enrollment_invalid_email_dialog_sign_in)
                            , formName);

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setCenterButton(getString(R.string.enrollment_invalid_email_dialog_diff_email), (v) -> {

                    EnrollmentAnalytics.logAlertDialogInteraction(requireContext(), getString(R.string.enrollment_email_already_exists_title)
                                    + "(" + getString(R.string.enrollment_email_already_exists_description) + ")"
                            , getString(R.string.enrollment_invalid_email_dialog_diff_email)
                            , formName);

                    binding.emailInput.setText("");
                    dialog.dismiss();
                    focusOnItem(binding.emailInput);
                })
                .show(getFragmentManager(), ModalDialog.TAG);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEnrollmentFormBinding.inflate(inflater, container, false);
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
        requiredFields.add(binding.securityQuestionInput);
        requiredFields.add(binding.securityAnswerInput);
        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());

        binding.phoneInput.getEditText().addTextChangedListener(new SuncorPhoneNumberTextWatcher());
        binding.phoneInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.phoneInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard();
                return true;
            }
            return false;
        });

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

        binding.streetAutocompleteOverlay.autocompleteList.setAdapter(addressAutocompleteAdapter);
        binding.streetAutocompleteOverlay.autocompleteList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        dividerDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_divider));
        binding.streetAutocompleteOverlay.autocompleteList.addItemDecoration(dividerDecoration);
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
        EnrollmentAnalytics.logScreenNameClass(requireActivity(), screenName, this.getClass().getSimpleName());
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        ExtendedNestedScrollView scroller = binding.scrollView;

        if (scroller != null) {

            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        double scrollViewHeight = v.getChildAt(0).getBottom() - v.getHeight();
                        double getScrollY = v.getScrollY();
                        double scrollPosition = (getScrollY / scrollViewHeight) * 100d;
                        int percentage = (int) scrollPosition;
                        if (percentage > 5 && !scroll5) {
                            scroll5 = true;
                            EnrollmentAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_5);
                        } else if (percentage > 25 && !scroll25) {
                            scroll25 = true;
                            EnrollmentAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_25);
                        } else if (percentage > 50 && !scroll50) {
                            scroll50 = true;
                            EnrollmentAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_50);
                        } else if (percentage > 75 && !scroll75) {
                            scroll75 = true;
                            EnrollmentAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_75);
                        } else if (percentage > 95 && !scroll95) {
                            scroll95 = true;
                            EnrollmentAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_95);
                        }
                    }

                }
            });
        }
    }


    @SuppressWarnings({"PointlessBooleanExpression"})
    @Override
    public void onBackPressed() {
        hideKeyBoard();
        if (viewModel.isUserCameToValidationScreen()) {
            requireActivity().finish();

            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }
        if (viewModel.showAutocompleteLayout.getValue() != null && viewModel.showAutocompleteLayout.getValue()) {
            viewModel.hideAutoCompleteLayout();
        } else if (viewModel.isOneItemFilled()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            EnrollmentAnalytics.logAlertDialogShown(requireContext(),
                    EnrollmentAnalytics.ALERT_TITLE_LEAVE_SIGNUP,
                    formName
            );

            alertDialog.setTitle(R.string.enrollment_leave_alert_title);
            alertDialog.setMessage(R.string.enrollment_leave_alert_message);
            alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {

                EnrollmentAnalytics.logAlertDialogInteraction(requireContext(),
                        EnrollmentAnalytics.ALERT_TITLE_LEAVE_SIGNUP,
                        BUTTON_TEXT_OK,
                        formName
                );
                requireActivity().finish();
            });
            alertDialog.setNegativeButton(R.string.cancel, (d, w) -> {

                EnrollmentAnalytics.logAlertDialogInteraction(requireContext(),
                        EnrollmentAnalytics.ALERT_TITLE_LEAVE_SIGNUP,
                        BUTTON_TEXT_CANCEL,
                        formName
                );

            });
            alertDialog.show();
        } else {
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(requireView().getWindowToken(), 0);
    }

    public void joinButtonClicked() {
        hideKeyBoard();

        int itemWithError = viewModel.validateAndJoin();
        if (itemWithError != -1) {
            focusOnItem(requiredFields.get(itemWithError));
        }
    }

    public void navigateToMainActivity() {
        onBackPressed();
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
        if (view == binding.postalcodeInput) {
            viewModel.getPostalCodeField().setHasFocus(hasFocus);
        }
        if (view == binding.streetAddressInput) {
            viewModel.getStreetAddressField().setHasFocus(hasFocus);
        }
        if (view == binding.cityInput) {
            viewModel.getCityField().setHasFocus(hasFocus);
        }
        if (view == binding.phoneInput) {
            viewModel.getPhoneField().setHasFocus(hasFocus);
        }

        //log form steps
        if (hasFocus) {
            if (view == binding.firstNameInput) {
                EnrollmentAnalytics.logFormStep(requireContext(), formName,  STEP_NAME_PERSONAL_INFORMATION);
            } else if (view == binding.streetAddressInput) {
                EnrollmentAnalytics.logFormStep(requireContext(), formName,  STEP_NAME_ADDRESS);
            }
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
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input.getEditText(), InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void callCostumerSupport(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
        EnrollmentAnalytics.logTapToCall(requireContext(), phoneNumber);
    }


}
