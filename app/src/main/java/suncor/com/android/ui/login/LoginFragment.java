package suncor.com.android.ui.login;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import suncor.com.android.BuildConfig;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentLoginBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.FingerprintManager;
import suncor.com.android.utilities.KeyStoreStorage;

public class LoginFragment extends BaseFragment {

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    FingerprintManager fingerPrintManager;
    @Inject
    SessionManager sessionManager;

    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;
    private String email = "";
    private String password = "";

    @Inject
    KeyStoreStorage keyStoreStorage;

    private static final String CREDENTIALS_KEY = "credentials";

    public LoginFragment() {

    }

    public static LoginFragment newInstance(boolean fromEnrollment) {
        Bundle args = new Bundle();
        args.putBoolean(LoginActivity.LOGIN_FROM_ENROLLMENT_EXTRA, fromEnrollment);
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        boolean fromEnrollment = getArguments().getBoolean(LoginActivity.LOGIN_FROM_ENROLLMENT_EXTRA, false);
        viewModel.setLoginFromEnrollment(fromEnrollment);
        viewModel.getLoginFailedEvent().observe(this, (event) -> {
            LoginViewModel.LoginFailResponse response = event.getContentIfNotHandled();
            if (response != null) {
                AlertDialog.Builder dialog = createAlert(response);
                dialog.show();
            }
        });

        viewModel.getLoginSuccessEvent().observe(this, event -> {
                    if (event.getContentIfNotHandled() != null) {
                        if (fingerPrintManager.isFingerPrintExistAndEnrolled() && !fingerPrintManager.isFingerprintActivated()) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle(R.string.sign_enable_fp_title)
                                    .setMessage(R.string.sign_enable_fb_message)
                                    .setPositiveButton(R.string.sign_enable_fb_possitive_button, (dialog, which) -> {
                                        fingerPrintManager.activateFingerprint();
                                        getActivity().finish();
                                    })
                                    .setNegativeButton(R.string.sign_enable_fb_negative_button, (dialog, which) -> {
                                        fingerPrintManager.deactivateFingerprint();
                                        getActivity().finish();
                                    })
                                    .create()
                                    .show();
                        } else {
                            getActivity().finish();
                        }

                        fingerPrintManager.activateAutoLogin();
                        AnalyticsUtils.logEvent(getContext(), "login", new Pair<>("retailID", viewModel.sessionManager.getProfile().getRetailIdDevQAOnly()));
                        getActivity().finish();

                    }
                }
        );

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.emailLayout.getEditText().clearFocus();
                binding.passwordLayout.getEditText().clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            }
        });

        viewModel.getPasswordResetEvent().observe(this, (event -> {
            String resetPasswordPath = Locale.getDefault().getLanguage().equalsIgnoreCase("fr") ? "fr/personnel/mot-de-passe-oublie" : "en/personal/forgot-password";
            String url = BuildConfig.SUNCOR_WEBSITE.concat(resetPasswordPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }));

        viewModel.getCallCustomerService().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                String customerSupportNumber = getString(R.string.customer_support_number);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + customerSupportNumber));
                startActivity(intent);

                AnalyticsUtils.logEvent(getContext(), "tap_to_call", new Pair<>("phoneNumberTapped", customerSupportNumber));
            }
        });

        viewModel.getCreatePasswordEvent().observe(this, event -> {
            String encryptedEmail = event.getContentIfNotHandled();
            if (encryptedEmail != null) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, CreatePasswordFragment.newInstance(viewModel.getEmailInputField().getText(), encryptedEmail));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        viewModel.getNavigateToHomeEvent().observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                getActivity().finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(getActivity()).setCurrentScreen(getActivity(), "login", getActivity().getClass().getSimpleName());
        new Handler().postDelayed(() -> {
            if (fingerPrintManager.isFingerPrintExistAndEnrolled() && fingerPrintManager.isFingerprintActivated()) {
                String savedCredentials = keyStoreStorage.retrieve(CREDENTIALS_KEY);

                if (savedCredentials != null) try {
                    JSONObject credentials = new JSONObject(savedCredentials);
                    email = credentials.getString("email");
                    password = credentials.getString("password");
                } catch (JSONException e) {
                    return;
                }
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle(getResources().getString(R.string.login_fingerprint_alert_title))
                        .setSubtitle(email)
                        .setDescription(getResources().getString(R.string.login_fingerprint_alert_desc))
                        .setNegativeButtonText(getResources().getString(R.string.login_fingerprint_alert_negative_button)).build();
                Executor executor = Executors.newSingleThreadExecutor();
                BiometricPrompt biometricPrompt = new BiometricPrompt(getActivity(), executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        viewModel.fingerPrintConfirmed(email, password);

                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
                biometricPrompt.authenticate(promptInfo);
            }
        }, 100);


    }

    private AlertDialog.Builder createAlert(LoginViewModel.LoginFailResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message;
        AnalyticsUtils.logEvent(getContext(), "error_log", new Pair<>("errorMessage", getString(response.title)));
        if (response.message.args != null) {
            message = getString(response.message.content, response.message.args);
        } else {
            message = getString(response.message.content);
        }
        builder.setMessage(message)
                .setTitle(response.title);
        builder.setPositiveButton(response.positiveButtonTitle, ((dialog, which) -> {
            if (response.positiveButtonCallback != null) {
                response.positiveButtonCallback.call();
            }
            dialog.dismiss();
        }));

        if (response.negativeButtonTitle != 0) {
            builder.setNegativeButton(response.negativeButtonTitle, (i, w) -> {
                if (response.negativeButtonCallBack != null) {
                    response.negativeButtonCallBack.call();
                }
                i.dismiss();
            });
        }

        return builder;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.appBar.setNavigationOnClickListener((v) -> getActivity().finish());
        binding.emailLayout.getEditText().requestFocus();
        return binding.getRoot();
    }
}
