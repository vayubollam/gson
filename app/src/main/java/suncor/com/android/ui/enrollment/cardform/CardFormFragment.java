package suncor.com.android.ui.enrollment.cardform;


import static suncor.com.android.analytics.enrollment.CardFormAnalytics.SCREEN_NAME_ACTIVATE_MATCH_CARD;
import static suncor.com.android.analytics.enrollment.EnrollmentAnalyticsKt.FORM_NAME_ACTIVATE_PETRO_POINTS_CARD;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import suncor.com.android.R;
import suncor.com.android.analytics.Errors;
import suncor.com.android.analytics.enrollment.CardFormAnalytics;
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
import suncor.com.android.utilities.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFormFragment extends BaseFragment {


    private static final String SCREEN_CLASS_NAME = "CardFormFragment";
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

                    CardFormAnalytics.logErrorEvent(requireContext()
                            , Errors.INVALID_CARD, FORM_NAME_ACTIVATE_PETRO_POINTS_CARD,"");

                    dialog.setTitle(getString(R.string.enrollment_cardform_invalid_card_dialog_title))
                            .setMessage(getString(R.string.enrollment_cardform_invalid_card_dialog_message))
                            .setFormName(FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
                            .setRightButton(getString(R.string.enrollment_cardform_invalid_card_dialog_try_again), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.enrollment_cardform_invalid_card_dialog_try_again)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                );

                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_cardform_invalid_card_dialog_callus), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.enrollment_cardform_invalid_card_dialog_callus)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                );

                                callCostumerSupport(getString(R.string.customer_support_number));
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                }
                else if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE)) {
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);

                    CardFormAnalytics.logErrorEvent(
                            requireContext(),
                            Errors.PETRO_POINTS_ALREADY_USED,
                            FORM_NAME_ACTIVATE_PETRO_POINTS_CARD,"");

                    dialog.setTitle(getString(R.string.enrollment_cardform_existing_card_dialog_title))
                            .setMessage(getString(R.string.enrollment_cardform_existing_card_dialog_message))
                            .setFormName(FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
                            .setRightButton(getString(R.string.enrollment_cardform_existing_card_dialog_sign_in), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.enrollment_cardform_existing_card_dialog_sign_in)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                );

                                getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_cardform_existing_card_use_different_card), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.enrollment_cardform_existing_card_use_different_card)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                );

                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                }
                else if(cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ERR_CARD_PENDING_EMAIL_VALIDATION)){
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);

                    CardFormAnalytics.logErrorEvent(
                            requireContext(),
                            Errors.VERIFY_EMAIL_ADDRESS,
                            FORM_NAME_ACTIVATE_PETRO_POINTS_CARD,"");

                    dialog.setTitle(getString(R.string.verify_your_email_address_title))
                            .setMessage(getString(R.string.verify_your_email_address_description))
                            .setFormName(FORM_NAME_ACTIVATE_PETRO_POINTS_CARD)
                            .setRightButton(getString(R.string.verify_your_email_address_call_us), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.verify_your_email_address_call_us)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                        );

                                callCostumerSupport(getString(R.string.customer_support_number));
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.sign_enable_fb_negative_button), (v) -> {
                                CardFormAnalytics.logAlertDialogInteraction(requireContext()
                                        ,dialog.getAnalyticsTitle()
                                        ,getString(R.string.sign_enable_fb_negative_button)
                                        ,FORM_NAME_ACTIVATE_PETRO_POINTS_CARD
                                );
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                }
                else {
                    Dialog dialog = Alerts.prepareGeneralErrorDialog(getContext(), FORM_NAME_ACTIVATE_PETRO_POINTS_CARD);
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
        CardFormAnalytics.logScreenNameClass(requireActivity(),SCREEN_NAME_ACTIVATE_MATCH_CARD,SCREEN_CLASS_NAME);
        CardFormAnalytics.logFormStart(requireContext(),FORM_NAME_ACTIVATE_PETRO_POINTS_CARD);
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
        CardFormAnalytics.logTapToCall(requireContext(),phoneNumber);
    }
}
