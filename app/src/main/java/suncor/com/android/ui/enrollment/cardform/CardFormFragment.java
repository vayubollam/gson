package suncor.com.android.ui.enrollment.cardform;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardFormBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.model.account.CardStatus;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.BaseFragment;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.common.input.CardNumberFormattingTextWatcher;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.Timber;

import static suncor.com.android.utilities.Constants.ACTIVATE_PETRO_POINTS_CARD;
import static suncor.com.android.utilities.Constants.ACTIVATE_PETRO_POINTS_SIGN_UP;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFormFragment extends BaseFragment {


    private CardFormViewModel viewModel;
    @Inject
    ViewModelFactory viewModelFactory;
    FragmentCardFormBinding binding;

    public CardFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardFormViewModel.class);

        viewModel.verifyCard.observe(this, cardStatusResource -> {
            if (cardStatusResource.status == Resource.Status.LOADING) {
                hideKeyBoard();
            }
            if (cardStatusResource.status == Resource.Status.SUCCESS) {
                Timber.d("cards status : success");
                CardStatus cardStatus = cardStatusResource.data;
                CardFormFragmentDirections.ActionCardFormFragmentToEnrollmentFormFragment action = CardFormFragmentDirections.actionCardFormFragmentToEnrollmentFormFragment().setCardStatus(cardStatus);
                if (getView() != null) {
                    getView().postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 500);
                }
            } else if (cardStatusResource.status == Resource.Status.ERROR) {
                if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_INVALID_CARD_ERROR_CODE) || cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_USER_INFO_NOT_MATCHED)) {
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error, new Pair<>(AnalyticsUtils.Param.errorMessage,getString(R.string.enrollment_cardform_invalid_card_dialog_title)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, ACTIVATE_PETRO_POINTS_CARD));

                    dialog.setTitle(getString(R.string.enrollment_cardform_invalid_card_dialog_title))
                            .setMessage(getString(R.string.enrollment_cardform_invalid_card_dialog_message))
                            .setRightButton(getString(R.string.enrollment_cardform_invalid_card_dialog_try_again), (v) -> {
                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_cardform_invalid_card_dialog_callus), (v) -> {
                                callCostumerSupport(getString(R.string.customer_support_number));
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE)) {
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.error, new Pair<>(AnalyticsUtils.Param.errorMessage,getString(R.string.enrollment_cardform_existing_card_dialog_title)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, ACTIVATE_PETRO_POINTS_CARD));

                    dialog.setTitle(getString(R.string.enrollment_cardform_existing_card_dialog_title))
                            .setMessage(getString(R.string.enrollment_cardform_existing_card_dialog_message))
                            .setRightButton(getString(R.string.enrollment_cardform_existing_card_dialog_sign_in), (v) -> {
                                getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_cardform_existing_card_use_different_card), (v) -> {
                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else if(cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_CARD_PENDING_EMAIL_VALIDATION)){
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);

                    dialog.setTitle(getString(R.string.verify_your_email_address_title))
                            .setMessage(getString(R.string.verify_your_email_address_description))
                            .setRightButton(getString(R.string.verify_your_email_address_call_us), (v) -> {
                                callCostumerSupport(getString(R.string.customer_support_number));
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.sign_enable_fb_negative_button), (v) -> {
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_MAX_ATTEMPT_BLOCK_CARD_ERROR_CODE)) {

                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    dialog.setMessage(getString(R.string.enrollment_cardform_max_attempts_block_card_dialog_message))
                            .setRightButton(getString(R.string.login_conflict_alert_positive_button), (v) -> {
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_MAX_REGISTER_BLOCK_CARD_ERROR_CODE)) {

                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    dialog.setMessage(getString(R.string.enrollment_cardform_max_register_block_card_dialog_message))
                            .setRightButton(getString(R.string.login_conflict_alert_positive_button), (v) -> {
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else {
                    Dialog dialog = Alerts.prepareGeneralErrorDialog(getContext(), ACTIVATE_PETRO_POINTS_SIGN_UP);

                    dialog.show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardFormBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener((v) -> {
            Navigation.findNavController(getView()).navigateUp();
        });

        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());
        binding.cardInput.getEditText().addTextChangedListener(new CardNumberFormattingTextWatcher(binding.cardInput.getEditText(), CardFormatUtils.PPTS_FORMAT));
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "activate-match-card");
        AnalyticsUtils.logEvent(getContext(), "form_start", new Pair<>("formName", ACTIVATE_PETRO_POINTS_CARD));
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void callCostumerSupport(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);

        AnalyticsUtils.logEvent(getContext(), "tap_to_call", new Pair<>("phoneNumberTapped", phoneNumber));
    }
}
