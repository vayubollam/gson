package suncor.com.android.ui.enrollment.form;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Pair;
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

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentEnrollmentFormBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
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
import suncor.com.android.utilities.AnalyticsUtils;
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
    private static final String CANADAPOST_SEARCH_DESCRIPTIVE_SCREEN_NAME = "canadapost-search-address";
    private boolean scroll20 = false, scroll40 = false, scroll60 = false, scroll80 = false, scroll100 = false;

    public EnrollmentFormFragment() {
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(EnrollmentFormViewModel.class);
        viewModel.setProvincesList(((EnrollmentActivity) getActivity()).getProvinces());

        addressAutocompleteAdapter = new AddressAutocompleteAdapter(viewModel::addressSuggestionClicked);

        if (getArguments() != null) {
            viewModel.setCardStatus(EnrollmentFormFragmentArgs.fromBundle(getArguments()).getCardStatus());
        }

        boolean joinWithCard = viewModel.getCardStatus() != null;
        if (joinWithCard) {
            screenName = "activate-i-have-a-card";
            formName = "Activate Petro-Points Card";
        } else {
            screenName = "sign-up-i-dont-have-a-card";
            formName = "Join Petro-Points";
        }

        isLoadedFirstTime = true;

        viewModel.getShowDuplicateEmailEvent().observe(this, (r) -> {
            showDuplicateEmailAlert();
        });

        AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);
        if (viewModel.getCardStatus() == null) {
            AnalyticsUtils.logEvent(getContext(), "form_start", new Pair<>("formName", formName));
        } else {
            AnalyticsUtils.logEvent(getContext(), "form_step", new Pair<>("formName", formName), new Pair<>("stepName", "Personal Information"));
        }
        
        //enrollments api call result
        viewModel.joinLiveData.observe(this, (r) -> {
            if (r.status == Resource.Status.SUCCESS) {
                getView().postDelayed(() -> {
                    if (getActivity() != null) {
                        //Go to main screen to show the welcome message
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }, 1000);
                //Log success events
                String screenName;
                if (viewModel.getCardStatus() != null) {
                    screenName = "activate-success";
                } else {
                    screenName = "sign-up-success";
                }
                AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);

                String optionsChecked = "";
                if (binding.emailOffersCheckbox.isChecked()) {
                    optionsChecked += binding.emailOffersCheckbox.getText().toString();
                }
                if (binding.smsOffersCheckbox.isChecked()) {
                    optionsChecked += binding.smsOffersCheckbox.getText().toString();
                }

                AnalyticsUtils.logEvent(
                        getContext(),
                        "form_complete",
                        new Pair<>("formName", formName),
                        new Pair<>("formSelection", optionsChecked)
                );
                AnalyticsUtils.logEvent(
                        getContext(),
                        "form_sign_up_success",
                        new Pair<>("formName", formName),
                        new Pair<>("formSelection", optionsChecked)
                );
                AnalyticsUtils.logEvent(getContext(), "sign_up");

            } else if (r.status == Resource.Status.ERROR && !EnrollmentFormViewModel.LOGIN_FAILED.equals(r.message)) {
                if (ErrorCodes.ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE.equals(r.message)) {
                    showDuplicateEmailAlert();
                } else if (ErrorCodes.ERR_RESTRICTED_DOMAIN.equals(r.message)) {
                    AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage", getString(R.string.enrollment_email_restricted_alert_title)));
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert", new Pair<>("alertTitle", getString(R.string.enrollment_email_restricted_alert_title) + "(" + ")"));
                    dialog.setTitle(R.string.enrollment_email_restricted_alert_title);
                    dialog.setPositiveButton(R.string.ok, (d, w) -> {
                        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert_interaction",
                                new Pair<>("alertTitle", getString(R.string.enrollment_email_restricted_alert_title) + "(" + ")"),
                                new Pair<>("alertSelection", getString(R.string.ok))
                        );
                        binding.emailInput.setText("");
                        d.dismiss();
                        focusOnItem(binding.emailInput);
                    });
                    dialog.show();
                } else {
                    Alerts.prepareGeneralErrorDialog(getActivity()).show();
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
                    AnalyticsUtils.setCurrentScreenName(getActivity(), CANADAPOST_SEARCH_DESCRIPTIVE_SCREEN_NAME);
                    isLoadedFirstTime = false;
                }
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.black_40));
                binding.appBar.setOnClickListener((v) -> viewModel.hideAutoCompleteLayout());
                binding.streetAutocompleteBackground.setVisibility(View.VISIBLE);
                binding.streetAutocompleteOverlay.setIsVisible(true);
                ViewCompat.setElevation(binding.appBar, 0);
                binding.scrollView.setScrollEnabled(false);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_40_transparent));
            } else {
                binding.appBar.setOnClickListener(null);
                binding.appBar.setBackgroundColor(getResources().getColor(R.color.white));
                binding.streetAutocompleteBackground.setVisibility(View.GONE);
                binding.streetAutocompleteOverlay.setIsVisible(false);
                ViewCompat.setElevation(binding.appBar, 8);
                binding.scrollView.setScrollEnabled(true);
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.white));
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
                getActivity().finish();
            }
        });
        viewModel.showBiometricAlert.observe(this, booleanEvent -> {
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert",
                    new Pair<>("alertTitle", getString(R.string.sign_enable_fp_title) + "(" + getString(R.string.sign_enable_fb_message) + ")")
            );
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.sign_enable_fp_title)
                    .setMessage(R.string.sign_enable_fb_message)
                    .setPositiveButton(R.string.sign_enable_fb_possitive_button, (dialog, which) -> {
                        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert_interaction",
                                new Pair<>("alertTitle", getString(R.string.sign_enable_fp_title) + "(" + getString(R.string.sign_enable_fb_message) + ")"),
                                new Pair<>("alertSelection", getString(R.string.sign_enable_fb_possitive_button))
                        );
                        viewModel.proccedToJoin(true);
                    })
                    .setNegativeButton(R.string.sign_enable_fb_negative_button, (dialog, which) -> {
                        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert_interaction",
                                new Pair<>("alertTitle", getString(R.string.sign_enable_fp_title) + "(" + getString(R.string.sign_enable_fb_message) + ")"),
                                new Pair<>("alertSelection", getString(R.string.sign_enable_fb_negative_button))
                        );
                        viewModel.proccedToJoin(false);
                    })
                    .create()
                    .show();
        });
    }

    private void showDuplicateEmailAlert() {
        ModalDialog dialog = new ModalDialog();
        dialog.setCancelable(false);
        AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage", getString(R.string.enrollment_invalid_email_title)));

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
        DividerItemDecoration dividerDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerDecoration.setDrawable(getResources().getDrawable(R.drawable.horizontal_divider));
        binding.streetAutocompleteOverlay.autocompleteList.addItemDecoration(dividerDecoration);
        return binding.getRoot();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public String getScreenName() {
        return screenName;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
                        if (percentage > 20 && !scroll20) {
                            scroll20 = true;
                            AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "20"));
                        } else if (percentage > 40 && !scroll40) {
                            scroll40 = true;
                            AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "40"));
                        } else if (percentage > 60 && !scroll60) {
                            scroll60 = true;
                            AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "60"));
                        } else if (percentage > 80 && !scroll80) {
                            scroll80 = true;
                            AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "80"));
                        } else if (percentage > 100 && !scroll100) {
                            scroll100 = true;
                            AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "100"));
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
        if (viewModel.showAutocompleteLayout.getValue() != null && viewModel.showAutocompleteLayout.getValue()) {
            viewModel.hideAutoCompleteLayout();
        } else if (viewModel.isOneItemFilled()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert",
                    new Pair<>("alertTitle", getString(R.string.enrollment_leave_alert_title) + "(" + getString(R.string.enrollment_leave_alert_message) + ")")
            );
            alertDialog.setTitle(R.string.enrollment_leave_alert_title);
            alertDialog.setMessage(R.string.enrollment_leave_alert_message);
            alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {
                AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert_interaction",
                        new Pair<>("alertTitle", getString(R.string.enrollment_leave_alert_title) + "(" + getString(R.string.enrollment_leave_alert_message) + ")"),
                        new Pair<>("alertSelection", getString(R.string.ok))
                );
                getActivity().finish();
            });
            alertDialog.setNegativeButton(R.string.cancel, (d, w) -> {
                AnalyticsUtils.logEvent(getActivity().getApplicationContext(), "alert_interaction",
                        new Pair<>("alertTitle", getString(R.string.enrollment_leave_alert_title) + "(" + getString(R.string.enrollment_leave_alert_message) + ")"),
                        new Pair<>("alertSelection", getString(R.string.cancel))
                );
            });
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
        hideKeyBoard();

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
                AnalyticsUtils.logEvent(
                        getContext(),
                        AnalyticsUtils.Event.formStep,
                        new Pair<>(AnalyticsUtils.Param.formName, formName),
                        new Pair<>(AnalyticsUtils.Param.stepName, "Personal Information")
                );
            } else if (view == binding.streetAddressInput) {
                AnalyticsUtils.logEvent(
                        getContext(),
                        AnalyticsUtils.Event.formStep,
                        new Pair<>(AnalyticsUtils.Param.formName, formName),
                        new Pair<>(AnalyticsUtils.Param.stepName, "Address")
                );
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
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(input.getEditText(), InputMethodManager.SHOW_IMPLICIT);
        }
    }


}
