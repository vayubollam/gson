package suncor.com.android.ui.main.cards.add;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentAddCardBinding;
import suncor.com.android.databinding.PetroCanadaExpandedCardItemBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.cards.details.ExpandedCardItem;
import suncor.com.android.ui.main.common.BaseFragment;

public class AddCardFragment extends BaseFragment {

    private FragmentAddCardBinding binding;
    private AddCardViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AddCardViewModel.class);

        viewModel.addCardApiResult.observe(this, result -> {
            if (result.status == Resource.Status.LOADING) {
                hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                if (ErrorCodes.ERR_LIKING_CARD_FAILED.equals(result.message)) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.cards_add_fragment_invalid_card_title)
                            .setMessage(R.string.cards_add_fragment_invalid_card_message)
                            .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                            .show();
                } else {
                    Alerts.prepareGeneralErrorDialog(getContext()).show();
                }
            }
        });

        viewModel.card.observe(this, cardDetail -> {
            binding.cardLayout.setVisibility(View.VISIBLE);
            PetroCanadaExpandedCardItemBinding expandedCardItemBinding = PetroCanadaExpandedCardItemBinding.inflate(getLayoutInflater(), binding.cardLayout, true);
            expandedCardItemBinding.setCard(new ExpandedCardItem(getContext(), cardDetail));
            expandedCardItemBinding.executePendingBindings();
        });

        viewModel.showCard.observe(this, show -> {
            if (show) {
                getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black_4));
            } else {
                setStatusBarColor();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddCardBinding.inflate(inflater, container, false);
        //binding.cardInput.getEditText().addTextChangedListener(new CardNumberFormattingTextWatcher(binding.cardInput.getEditText()));
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.helpButton.setOnClickListener(v -> showCvvHelp());
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.doneButton.setOnClickListener(v -> goBack());

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

        return binding.getRoot();
    }

    private void showCvvHelp() {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.cards_add_fragment_help_dialog_title)
                .setView(getLayoutInflater().inflate(R.layout.cvv_help_layout, null))
                .setPositiveButton(R.string.ok, null)
                .show();
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
        Navigation.findNavController(getView()).popBackStack();
    }
}
