package suncor.com.android.ui.enrollment.cardform;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import dagger.android.support.DaggerFragment;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardFormBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.ModalDialog;
import suncor.com.android.ui.common.input.CardNumberFormattingTextWatcher;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.utilities.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFormFragment extends DaggerFragment {


    private CardFormViewModel viewModel;
    private boolean isKeyboardShown;
    private float appBarElevation;
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
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCardFormBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener((v) -> {
            Navigation.findNavController(getView()).navigateUp();
        });
        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > binding.appBar.getBottom()) {
                binding.appBar.setTitle(getString(R.string.enrollment_cardform_header));
                ViewCompat.setElevation(binding.appBar, appBarElevation);
            } else {
                binding.appBar.setTitle("");
                ViewCompat.setElevation(binding.appBar, 0);
            }
        });

        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());
        binding.cardInput.getEditText().addTextChangedListener(new CardNumberFormattingTextWatcher(binding.cardInput.getEditText()));
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        viewModel.verifyCard.observe(this, cardStatusResource -> {
            if (cardStatusResource.status == Resource.Status.LOADING) {
                hideKeyBoard();

            }
            if (cardStatusResource.status == Resource.Status.SUCCESS) {
                Timber.d("cards status : success");

                CardFormFragmentDirections.ActionCardFormFragmentToEnrollmentFormFragment action = CardFormFragmentDirections.actionCardFormFragmentToEnrollmentFormFragment().setCardStatus(cardStatusResource.data);
                new Handler(Looper.getMainLooper()).postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 1000);
            } else if (cardStatusResource.status == Resource.Status.ERROR) {
                if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.INVALID_CARD_ERROR_CODE)) {
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    dialog.setTitle(getString(R.string.enrollment_card_form_invalid_title))
                            .setMessage(getString(R.string.enrollment_card_form_invalid_message))
                            .setRightButton(getString(R.string.enrollment_card_form_try_again), (v) -> {
                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_card_form_get_new_card), (v) -> {
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else if (cardStatusResource.message.equalsIgnoreCase(ErrorCodes.ACCOUNT_ALREDY_REGISTERED_ERROR_CODE)) {
                    ModalDialog dialog = new ModalDialog();
                    dialog.setCancelable(false);
                    dialog.setTitle(getString(R.string.enrollment_card_form_existing_title))
                            .setMessage(getString(R.string.enrollment_card_form_exisiting_message))
                            .setRightButton(getString(R.string.enrollment_card_form_sign_in), (v) -> {
                                getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                                dialog.dismiss();
                            })
                            .setCenterButton(getString(R.string.enrollment_card_form_existing_use_different_card), (v) -> {
                                binding.cardInput.getEditText().requestFocus();
                                showKeyBoard();
                                dialog.dismiss();
                            })
                            .show(getFragmentManager(), ModalDialog.TAG);
                } else {
                    Dialog dialog = Alerts.prepareGeneralErrorDialog(getContext());
                    dialog.show();


                }
            }

        });
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

}
