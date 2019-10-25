package suncor.com.android.ui.main.carwash;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarwashSecurityBinding;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class CarWashActivationSecurityFragment extends MainActivityFragment implements OnBackPressedListener {

    private final static int VERIFICATION_PIN_LENGTH = 3;
    private AppCompatEditText pinText1, pinText2, pinText3;
    private InputMethodManager inputMethodManager;
    private CarWashSharedViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(CarWashSharedViewModel.class);
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
            boolean loadFromCarWash = CarWashActivationSecurityFragmentArgs.fromBundle(getArguments()).getIsCardFromCarWash();
            viewModel.setClickedCardIndex(clickedCardIndex);
            viewModel.setIsFromCarWash(loadFromCarWash);
        }
        FragmentCarwashSecurityBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_carwash_security, container, false);
        binding.appBar.setNavigationOnClickListener(v -> goBack());
        pinText1 = binding.pinNum1;
        pinText2 = binding.pinNum2;
        pinText3 = binding.pinNum3;

        binding.getRoot().post(() -> {
            pinText1.requestFocus();
            pinText1.addTextChangedListener(new mInputTextWatcher(1));
            pinText2.addTextChangedListener(new mInputTextWatcher(2));
            pinText3.addTextChangedListener(new mInputTextWatcher(3));
            inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(pinText1, InputMethodManager.SHOW_IMPLICIT);

        });
        binding.confirmButton.setOnClickListener(confirmListener);
        return binding.getRoot();
    }

    private View.OnClickListener confirmListener = v -> {
        String pin = isPinEntered();
        if (pin != null && pin.length() == VERIFICATION_PIN_LENGTH) {
            viewModel.setSecurityPin(pin);
            Navigation.findNavController(getView()).
                    navigate(R.id.action_carWashActivationSecurityFragment_to_carWashBarCodeFragment);
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.carwash_activation_pin_error_title)
                    .setMessage(R.string.carwash_activation_pin_error_message)
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
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
        int id;

        mInputTextWatcher(int id) {
            this.id = id;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            switch (id) {
                case 1:
                    if (pinText1.getText() != null && pinText1.getText().toString().length() != 0) {
                        pinText1.clearFocus();
                        pinText2.requestFocus();
                    }
                    break;
                case 2:
                    if (pinText2.getText() != null && pinText2.getText().toString().length() != 0) {
                        pinText2.clearFocus();
                        pinText3.requestFocus();
                    }
                    break;
                case 3:
                    if (pinText3.getText() != null && pinText3.getText().toString().length() != 0) {
                        pinText3.clearFocus();
                        inputMethodManager.hideSoftInputFromWindow(pinText3.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    break;
            }

        }


        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}
