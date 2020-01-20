package suncor.com.android.ui.main.carwash;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashSecurityBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class CarWashActivationSecurityFragment extends MainActivityFragment implements OnBackPressedListener {

    private final static int VERIFICATION_PIN_LENGTH = 3;
    private AppCompatEditText pinText1, pinText2, pinText3;
    private InputMethodManager inputMethodManager;
    private CarWashSharedViewModel viewModel;
    private CardDetail cardDetail;
    @Inject
    ViewModelFactory viewModelFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CarWashSharedViewModel.class);
        viewModel.getReEnter().observe(this, reEnter -> {
            if (reEnter) {
                clearText();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            int clickedCardIndex = CarWashActivationSecurityFragmentArgs.fromBundle(getArguments()).getCardIndex();
            String cardNumber = CarWashActivationSecurityFragmentArgs.fromBundle(getArguments()).getCardNumber();
            viewModel.setClickedCardIndex(clickedCardIndex);
            viewModel.setCardNumber(cardNumber);
        }
        FragmentCarwashSecurityBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_security, container, false);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        pinText1 = binding.pinNum1;
        pinText2 = binding.pinNum2;
        pinText3 = binding.pinNum3;

        binding.getRoot().post(() -> {
            pinText1.addTextChangedListener(new mInputTextWatcher(PIN_ID.FIRST));
            pinText2.addTextChangedListener(new mInputTextWatcher(PIN_ID.SECOND));
            pinText3.addTextChangedListener(new mInputTextWatcher(PIN_ID.THIRD));
            pinText1.setOnKeyListener(new mKeyBoardListener(PIN_ID.FIRST));
            pinText2.setOnKeyListener(new mKeyBoardListener(PIN_ID.SECOND));
            pinText3.setOnKeyListener(new mKeyBoardListener(PIN_ID.THIRD));
            inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (viewModel.getIsBackFromBarCode().getValue() != null && viewModel.getIsBackFromBarCode().getValue()
                    && viewModel.getReEnter().getValue() != null && !viewModel.getReEnter().getValue()) {
                pinText3.requestFocus();
                inputMethodManager.showSoftInput(pinText3, InputMethodManager.SHOW_IMPLICIT);
                viewModel.setIsBackFromBarCode(false);
            } else {
                pinText1.requestFocus();
                inputMethodManager.showSoftInput(pinText1, InputMethodManager.SHOW_IMPLICIT);
            }


        });
        binding.confirmButton.setOnClickListener(confirmListener);
        return binding.getRoot();
    }

    private View.OnClickListener confirmListener = v -> {
        String pin = isPinEntered();
        if (pin != null && pin.length() == VERIFICATION_PIN_LENGTH) {
            viewModel.setSecurityPin(pin);
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            boolean loadFromCarWash = CarWashActivationSecurityFragmentArgs.fromBundle(getArguments()).getIsCardFromCarWash();
            CarWashActivationSecurityFragmentDirections.ActionCarWashActivationSecurityFragmentToCarWashBarCodeFragment
                    action = CarWashActivationSecurityFragmentDirections.actionCarWashActivationSecurityFragmentToCarWashBarCodeFragment(loadFromCarWash);
            Navigation.findNavController(getView()).navigate(action);
        } else {
            String analyticsTitle = getContext().getString(R.string.carwash_activation_pin_error_title)+getContext().getString(R.string.carwash_activation_pin_error_message);
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alert,
                    new Pair<>(AnalyticsUtils.Param.alertTitle,analyticsTitle)
            );
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.carwash_activation_pin_error_title)
                    .setMessage(R.string.carwash_activation_pin_error_message)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle,analyticsTitle),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, getContext().getString(R.string.ok))
                        );
                        dialog.dismiss();
                    })
                    .show();
        }
    };


    private void clearText() {
        if (pinText1.getText() != null && pinText2.getText() != null && pinText3.getText() != null) {
            pinText1.getText().clear();
            pinText2.getText().clear();
            pinText3.getText().clear();
        }

    }

    @Nullable
    private String isPinEntered() {
        StringBuilder sb = new StringBuilder();
        if (pinText1.getText() != null && pinText2.getText() != null && pinText3.getText() != null) {
            sb.append(pinText1.getText().toString());
            sb.append(pinText2.getText().toString());
            sb.append(pinText3.getText().toString());
        }
        return sb.toString();
    }

    private void goBack() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        Navigation.findNavController(getView()).popBackStack();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    class mInputTextWatcher implements TextWatcher {
        PIN_ID id;

        mInputTextWatcher(PIN_ID id) {
            this.id = id;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (id) {
                case FIRST:
                    if (pinText1.getText() != null && pinText1.getText().toString().length() != 0) {
                        pinText1.clearFocus();
                        pinText2.requestFocus();
                    }
                    break;
                case SECOND:
                    if (pinText2.getText() != null && pinText2.getText().toString().length() != 0) {
                        pinText2.clearFocus();
                        pinText3.requestFocus();
                    }
                    break;
            }

        }


        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    class mKeyBoardListener implements View.OnKeyListener {
        PIN_ID id;

        mKeyBoardListener(PIN_ID id) {
            this.id = id;
        }

        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return true;

            if (i == KeyEvent.KEYCODE_DEL) {
                switch (id) {
                    case FIRST:
                        //do nothing
                        break;
                    case SECOND:
                        if (pinText2.getText() != null && pinText2.getText().length() == 0) {
                            pinText2.clearFocus();
                            pinText1.requestFocus();
                            pinText1.getText().clear();
                        }
                        break;
                    case THIRD:
                        if (pinText3.getText() != null && pinText3.getText().length() == 0) {
                            pinText3.clearFocus();
                            pinText2.requestFocus();
                            pinText2.getText().clear();
                        }
                        break;
                }
            }
            return false;
        }
    }

    enum PIN_ID {
        FIRST,
        SECOND,
        THIRD
    }

}
