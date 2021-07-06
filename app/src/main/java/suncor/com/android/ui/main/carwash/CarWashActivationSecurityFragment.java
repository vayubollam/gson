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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.data.settings.SettingsApi;
import suncor.com.android.databinding.FragmentCarwashSecurityBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.Resource;
import suncor.com.android.model.SettingsResponse;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.SuncorButton;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class CarWashActivationSecurityFragment extends CarwashLocation implements OnBackPressedListener {

    private final static int VERIFICATION_PIN_LENGTH = 3;
    private AppCompatEditText pinText1, pinText2, pinText3;
    private InputMethodManager inputMethodManager;
    private CarWashSharedViewModel viewModel;

    private CardDetail cardDetail;
    private SuncorButton confirmButton;

    @Inject
    SettingsApi settingsApi;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(CarWashSharedViewModel.class);

        viewModel.securityKey = sessionManager.getCarWashKey();

        if (viewModel.securityKey != null) {
            viewModel.getMobileCode();
        } else {
            settingsApi.retrieveSettings().observe(this, resource -> {
                if (resource.status == Resource.Status.SUCCESS) {
                    SettingsResponse settingsResponse = resource.data;
                    sessionManager.setCarWashKey(settingsResponse.getSettings().getCarwash().getKey());
                    viewModel.securityKey = sessionManager.getCarWashKey();
                    viewModel.getMobileCode();
                }
            });
        }

        viewModel.getReEnter().observe(this, reEnter -> {
            if (reEnter) {
                clearText();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

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
        confirmButton = binding.confirmButton;
        return binding.getRoot();
    }

    private View.OnClickListener confirmListener = v -> {
        String pin = isPinEntered();
        if (pin != null && pin.length() == VERIFICATION_PIN_LENGTH) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            viewModel.setSecurityPin(pin);
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            if (carWashCardViewModel.getNearestStation().getValue() != null
                    && carWashCardViewModel.getNearestStation().getValue().data != null
                    && carWashCardViewModel.getNearestStation().getValue().data.getStation() != null) {

                String storeId = carWashCardViewModel.getNearestStation().getValue().data.getStation().getId();

                viewModel.activateCarwash(storeId).observe(getViewLifecycleOwner(), resource -> {
                    if (resource.status == Resource.Status.LOADING) {
                    } else if (resource.status == Resource.Status.ERROR) {
                        navigateToCarwashActivated();
                        //navigateToBarcode();
                    } else if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                        navigateToCarwashActivated();
                        /*if (!resource.data.getResultCode().equals("ok")) {
                            if (resource.data.getResultSubcode().equals("incorrectPin")) {
                                confirmButton.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                        .setTitle(R.string.carwash_activation_pin_error_title)
                                        .setMessage(R.string.carwash_activation_pin_error_message)
                                        .setPositiveButton(R.string.ok, (dialog, which) -> {
                                            dialog.dismiss();
                                            confirmButton.setEnabled(true);
                                        }).create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                            } else {
                                navigateToBarcode();
                            }
                        } else {
                            navigateToCarwashActivated();
                        }*/
                    }
                });
            } else {
                navigateToBarcode();
            }
        } else {
            confirmButton.setEnabled(false);
            String analyticsTitle = getContext().getString(R.string.carwash_activation_pin_error_title) + "(" + getContext().getString(R.string.carwash_activation_pin_error_message) + ")";
            AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alert,
                    new Pair<>(AnalyticsUtils.Param.alertTitle,analyticsTitle),
                    new Pair<>(AnalyticsUtils.Param.formName,"Activate Wash by Wash & Go card")
            );
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle(R.string.carwash_activation_pin_error_title)
                    .setMessage(R.string.carwash_activation_pin_error_message)
                    .setPositiveButton(R.string.ok, (dialog, which) -> {
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticsTitle),
                                new Pair<>(AnalyticsUtils.Param.alertSelection, getContext().getString(R.string.ok)),
                                new Pair<>(AnalyticsUtils.Param.formName,"Activate Wash by Wash & Go card")
                        );
                        dialog.dismiss();
                        confirmButton.setEnabled(true);
                    }).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    };

    private void navigateToBarcode() {
        boolean loadFromCarWash = CarWashActivationSecurityFragmentArgs.fromBundle(getArguments()).getIsCardFromCarWash();

        CarWashActivationSecurityFragmentDirections.ActionCarWashActivationSecurityFragmentToCarWashBarCodeFragment
                action = CarWashActivationSecurityFragmentDirections.actionCarWashActivationSecurityFragmentToCarWashBarCodeFragment(loadFromCarWash);
        AnalyticsUtils.logCarwashActivationEvent(getContext(), AnalyticsUtils.Event.formStep, "Generate Barcode");
        Navigation.findNavController(getView()).navigate((NavDirections) action);
    }

    private void navigateToCarwashActivated() {
        NavDirections action = CarWashActivationSecurityFragmentDirections.actionCarWashActivationSecurityFragmentToCarWashActivatedFragment();
        AnalyticsUtils.logCarwashActivationEvent(getContext(), AnalyticsUtils.Event.formStep, "Activate Carwash");
        Navigation.findNavController(getView()).navigate(action);
    }

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

    @Override
    public void onResume() {
        super.onResume();

        AnalyticsUtils.setCurrentScreenName(getActivity(), "car-wash-security-code");
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
            } else if (i == KeyEvent.KEYCODE_ENTER) {
                confirmButton.performClick();
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
