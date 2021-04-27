package suncor.com.android.ui.main.wallet.cards.add;

import android.content.Context;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentAddCardBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class AddCardFragment extends MainActivityFragment {

    private FragmentAddCardBinding binding;
    private AddCardViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;
    @Inject
    MainViewModel mainViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddCardViewModel.class);
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.formStart, new Pair<>(AnalyticsUtils.Param.formName, "add card"));

        viewModel.addCardApiResult.observe(this, result -> {
            if (result.status == Resource.Status.LOADING) {
                hideKeyBoard();
                AnalyticsUtils.setCurrentScreenName(getActivity() , "my-petro-points-wallet-saving-perto-card");
            } else if (result.status == Resource.Status.ERROR) {
                if (ErrorCodes.ERR_LIKING_CARD_FAILED.equals(result.message)) {
                    String analyticsName = getContext().getString(R.string.cards_add_fragment_invalid_card_title)
                            + "(" + getContext().getString(R.string.cards_add_fragment_invalid_card_message) +")";
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alert,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                            new Pair<>(AnalyticsUtils.Param.formName, "Add Card")
                    );
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.cards_add_fragment_invalid_card_title)
                            .setMessage(R.string.cards_add_fragment_invalid_card_message)
                            .setPositiveButton(R.string.ok, (dialog, which) -> {
                                AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                        new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsName),
                                        new Pair<>(AnalyticsUtils.Param.alertSelection, getContext().getString(R.string.ok)),
                                        new Pair<>(AnalyticsUtils.Param.formName, "Add Card")
                                );
                                dialog.dismiss();
                            })
                            .show();
                } else {
                    Alerts.prepareGeneralErrorDialog(getContext(), "Add Card").show();
                }
            }
        });

        viewModel.card.observe(this, cardDetail -> {
            mainViewModel.setNewCardAdded(true);
            mainViewModel.setNewAddedCard(cardDetail);
            String screenName = "my-petro-points-wallet-save-" + cardDetail.getCardName() + "-success";
            String optionsChecked = "";
            AnalyticsUtils.logEvent(
                    getContext(),
                    AnalyticsUtils.Event.formComplete,
                    new Pair<>(AnalyticsUtils.Param.formSelection, optionsChecked),
                    new Pair<>(AnalyticsUtils.Param.formName, "Add Card")
            );
            AnalyticsUtils.setCurrentScreenName(getActivity(), screenName);
            goBack();
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCardBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.helpButton.setOnClickListener(v -> showCvvHelp());
        binding.appBar.setNavigationOnClickListener(v -> goBack());

        binding.cardInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        binding.cardInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.continueButtonClicked();
                binding.cvvInput.getEditText().requestFocus();

                return true;
            }
            return false;
        });

        binding.cvvInput.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        binding.cvvInput.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.continueButtonClicked();
                return true;
            }
            return false;
        });
        binding.cardInput.requestFocus();
        showKeyBoard();
        if (binding.cardInput.hasFocus()) {
            AnalyticsUtils.logEvent(getContext(), "form_step", new Pair<>("formName", "Add Card"), new Pair<>("stepName", "Card number"));
        }
        viewModel.showCvvField.observe(this, result -> {
            if (viewModel.showCvvField.getValue().booleanValue())
                binding.cvvInput.getEditText().requestFocus();
            AnalyticsUtils.logEvent(getContext(), "form_step", new Pair<>("formName", "Add Card"), new Pair<>("stepName", "CVV"));

        });


        return binding.getRoot();
    }

    private void showCvvHelp() {
        AnalyticsUtils.setCurrentScreenName(getActivity(), "card-security-code-info");
        AnalyticsUtils.logEvent(getActivity().getApplicationContext(), AnalyticsUtils.Event.alert,
                new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.cards_add_fragment_help_dialog_title)+"("+")"),
                new Pair<>(AnalyticsUtils.Param.formName, "Add Card")
        );
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.cards_add_fragment_help_dialog_title)
                .setView(getLayoutInflater().inflate(R.layout.cvv_help_layout, null))
                .setPositiveButton(R.string.ok, (dialog, which)-> {
                    AnalyticsUtils.logEvent(getActivity().getApplicationContext(),  AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, getString(R.string.cards_add_fragment_help_dialog_title)+"("+")"),
                            new Pair<>(AnalyticsUtils.Param.alertSelection,getString(R.string.ok)),
                                    new Pair<>(AnalyticsUtils.Param.formName, "Add Card")
                    );
                })
                .show();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-wallet-add-petro-card";
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void showKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void goBack() {
        hideKeyBoard();
        Navigation.findNavController(getView()).getPreviousBackStackEntry().getSavedStateHandle().set("fromPayment", false);
        Navigation.findNavController(getView()).popBackStack();
    }
}
