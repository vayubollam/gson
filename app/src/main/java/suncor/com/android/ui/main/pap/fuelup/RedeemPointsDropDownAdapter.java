package suncor.com.android.ui.main.pap.fuelup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.databinding.OtherAmountBinding;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;

public class RedeemPointsDropDownAdapter extends DropDownAdapter {

    private ChildViewListener listener;
    private final Context mContext;
    private String redeemCaps;

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int MANUAL_DROP_DOWN_LAYOUT = 2;
    public static final String TAG = "RedeemPointsAdapter";
    public final HashMap<String, String> redeemPoints;

    private final int petroPoints;
    private final NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private final NumberFormat numberInstance = NumberFormat.getNumberInstance(Locale.getDefault());

    private int selectedPos = 0;
    private int selectedPosition;
    private String points;
    private String off;
    private String preAuthValue = null;
    private String dollarOffValue;
    private String resultantValue;
    private double roundOffValue;
    private final RedeemPointsCallback redeemPointsCallback;
    private double amountInDouble;
    private boolean isPreAuthChanges;
    private EditText otherAmountEditText;
    private double zeroInDouble = 0.0;
    private boolean selectedAmountOtherThanZero;

    RedeemPointsDropDownAdapter(final Context context, HashMap<String, String> redeemPoints, int petroPoints, RedeemPointsCallback redeemPointsCallback) {

        this.mContext = context;
        this.redeemPoints = redeemPoints;
        this.petroPoints = petroPoints;
        this.redeemPointsCallback = redeemPointsCallback;
        formatter.setMinimumFractionDigits(0);
    }

    @Override
    public String getSelectedValue() {
        String dollarsToReturn;

        if (isPreAuthChanges) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                return String.format("%s %s ", formatter.format(0), "de rabais");
            } else {
                return String.format("%s %s ", formatter.format(0), "off");
            }
        }
        if (selectedPos == 1) {
            dollarsToReturn = dollarOffValue;
            return dollarsToReturn;
        } else if (selectedPos == 2) {
            return getDollarOffValue(amountInDouble);
        } else {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
                return String.format("%s %s ", formatter.format(0), "de rabais");
            } else {
                return String.format("%s %s ", formatter.format(0), "off");
            }
        }
    }

    @Override
    public String getSelectedSubValue() {
        double resultantValueToReturn;

        if (isPreAuthChanges) {
            resultantValueToReturn = 0;
            return CardFormatUtils.formatBalance((int) resultantValueToReturn) + " " + "points";
        }
        if (selectedPos == 1) {
            resultantValueToReturn = getAmount(roundOffValue);
        } else if (selectedPos == 2) {
            resultantValueToReturn = getAmount(amountInDouble);
        } else {
            resultantValueToReturn = 0;
        }

        return CardFormatUtils.formatBalance((int) resultantValueToReturn) + " " + "points";

    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;

    }

    public void setPreAuthValue(String preAuthValue) {
        this.preAuthValue = preAuthValue;
    }


    private String getDollarOffValue(double amount) {
        return getLocaleDollarOffText(getAmount(amount) / 1000);
    }

    private double getAmount(double amount) {
        if (amount < 10) {
            return 0;
        } else if (amount % 10 > 0) {
            return (amount - amount % 10);
        }
        return amount;
    }

    private String getLocaleDollarOffText(double amt) {
        if (amt == 0.0) {
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")){
                return String.format("%s %s %s", 0, "$", off);
            }else{
                return String.format("%s %s %s", "$", 0, off);
            }
        }
        DecimalFormat df;
        if (amt < 1000) {
            df = new DecimalFormat("0.00");
        } else {
            df = new DecimalFormat("#,###.00");
        }
        if (Locale.getDefault().getLanguage().equalsIgnoreCase("fr")) {
            return String.format("%s %s %s", df.format(amt), "$", off);
        } else {
            return String.format("%s %s %s", "$", df.format(amt), off);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        redeemCaps = parent.getResources().getString(R.string.redeem_caps);
        points = parent.getResources().getString(R.string.points);
        off = parent.getResources().getString(R.string.off);


        if (viewType == MANUAL_DROP_DOWN_LAYOUT) {
            return new OtherAmountViewHolder(OtherAmountBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ChildDropDownViewHolder(FuelUpLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    public void collapseIfPreAuthChanges(int selectedPos) {
        isPreAuthChanges = true;
        this.selectedPos = selectedPos;
        listener.onSelectValue(formatter.format(0), formatter.format(0) + points, true, false);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        selectedPosition = position;

        if (position != redeemPoints.size() - 1) {
            ((ChildDropDownViewHolder) holder).setDataOnView(redeemPoints.get(String.valueOf(position + 1)));
        } else {
            ((OtherAmountViewHolder) holder).setDataOnView();
        }
    }


    @Override
    public int getItemCount() {
        return redeemPoints.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 2 ? MANUAL_DROP_DOWN_LAYOUT : DROP_DOWN_LAYOUT;
    }

    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        final FuelUpLimitDropDownItemBinding binding;


        ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataOnView(String price) {

            try {
                getRoundOffValue();
                if (selectedPosition == 1) {
                    binding.dollarOff.setVisibility(View.VISIBLE);
                    dollarOffValue = getDollarOffValue(roundOffValue);
                    binding.dollarOff.setText(dollarOffValue);
                    binding.title.setText(String.format("%s %s %s", redeemCaps, CardFormatUtils.formatBalance((int) getAmount(roundOffValue)), points));
                } else {
                    binding.title.setText(price);
                    binding.dollarOff.setVisibility(View.GONE);
                }

            } catch (NullPointerException ex) {
                Timber.e(TAG, "Error on inflating data , " + ex.getMessage());
            } catch (Exception e) {
                Timber.e(TAG, "Error, " + e.getMessage());
            }

            binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.container.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                if (Objects.nonNull(listener)) {
                    if (selectedPos == 1) {
                        isPreAuthChanges = false;
                        if(roundOffValue == 0){
                            selectedAmountOtherThanZero = false;
                        }else{
                            selectedAmountOtherThanZero = true;
                        }
                        if (redeemPointsCallback != null) {
                            redeemPointsCallback.onRedeemPointsChanged(String.valueOf(Double.valueOf(getAmount(roundOffValue)).intValue()), "Maximum Redemption", selectedAmountOtherThanZero);
                        }
                        listener.onSelectValue(dollarOffValue, getAmount(roundOffValue) + points, !selectedAmountOtherThanZero, true);
                    } else {

                        if (redeemPointsCallback != null) {
                            isPreAuthChanges = false;
                            redeemPointsCallback.onRedeemPointsChanged("0", "No Redemption", false);
                        }

                        listener.onSelectValue(formatter.format(0), formatter.format(0) + points, true, true);
                    }
                    listener.expandCollapse();
                }
            });
        }
    }

    private String replaceChars(String str) {
        str = str.replace("$", "");
        return str;
    }

    private String getFormattedPoints(double amount) {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
    }

    @Override
    public void showUpdatePreAuthPopup() {

    }

    class OtherAmountViewHolder extends RecyclerView.ViewHolder {
        final OtherAmountBinding binding;

        OtherAmountViewHolder(@NonNull OtherAmountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            setTextListener();
        }

        public void setTextListener(){
            binding.inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    if (!binding.inputField.getText().toString().isEmpty()) {
                        binding.inputField.setCursorVisible(true);
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {

                    try {
                        if (!s.toString().isEmpty()) {
                            otherAmountEditText.removeTextChangedListener(this);
                            amountInDouble = numberInstance.parse(replaceChars(s.toString())).doubleValue();
                            otherAmountEditText.setText(getFormattedPoints(amountInDouble));
                            otherAmountEditText.addTextChangedListener(this);
                            otherAmountEditText.setSelection(otherAmountEditText.getText().length());
                            binding.dollarOffText.setVisibility(View.VISIBLE);
                            binding.dollarOffText.setText(getDollarOffValue(amountInDouble));

                            try {
                                if(binding.inputField.getText().toString().isEmpty() || amountInDouble == 0.0){
                                    amountInDouble = 0.0;
                                    selectedAmountOtherThanZero = false;
                                }else{
                                    selectedAmountOtherThanZero = true;
                                }
                                getRoundOffValue();
                                if (amountInDouble > roundOffValue) {
                                    amountInDouble = roundOffValue;
                                }
                                isPreAuthChanges = false;
                                listener.onSelectValue(getDollarOffValue(amountInDouble), getAmount(amountInDouble) + points, false, true);
                                if (redeemPointsCallback != null) {
                                    redeemPointsCallback.onRedeemPointsChanged(String.valueOf(Double.valueOf(getAmount(amountInDouble)).intValue()), "Manual Redemption", selectedAmountOtherThanZero);
                                }
                                //listener.expandCollapse();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            binding.dollarOffText.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            });

            //binding.inputField.setOnKeyListener((view,  keyCode, event) -> {
            binding.inputField.setOnEditorActionListener((v, actionId, event) -> {
                //if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (Objects.nonNull(listener)) {
                        try {
                            if(binding.inputField.getText().toString().isEmpty() || amountInDouble == 0.0){
                                amountInDouble = 0.0;
                                selectedAmountOtherThanZero = false;
                            }else{
                                selectedAmountOtherThanZero = true;
                            }
                            getRoundOffValue();
                            if (amountInDouble > roundOffValue) {
                                amountInDouble = roundOffValue;
                            }
                            isPreAuthChanges = false;
                            String selectedValue = getDollarOffValue(amountInDouble);
                            if(selectedValue.equals("$ 0 off")){
                                selectedAmountOtherThanZero = false;
                            }else if (selectedValue.equals("0 $ de rabais")){
                                selectedAmountOtherThanZero = false;
                            }
                            listener.onSelectValue(getDollarOffValue(amountInDouble), getAmount(amountInDouble) + points, !selectedAmountOtherThanZero, true);
                            if (redeemPointsCallback != null) {
                                redeemPointsCallback.onRedeemPointsChanged(String.valueOf(Double.valueOf(getAmount(amountInDouble)).intValue()), "Manual Redemption", selectedAmountOtherThanZero);
                            }
                            listener.expandCollapse();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                return false;
            });

        }

        public void setDataOnView() {
            otherAmountEditText = binding.inputField;
            binding.preAuthTip.setVisibility(View.VISIBLE);

            binding.radioBtn.setSelected(selectedPos == getAdapterPosition());

            if (binding.radioBtn.isSelected()) {
                otherAmountEditText.setHint("");
                otherAmountEditText.setEnabled(true);
                binding.dollarOffText.setVisibility(View.VISIBLE);
                binding.dollarOffText.setVisibility(View.VISIBLE);

                if (!otherAmountEditText.getText().toString().isEmpty()) {
                    otherAmountEditText.setText(String.valueOf(getAmount(amountInDouble)));
                    binding.dollarOffText.setText(getDollarOffValue(amountInDouble));
                    otherAmountEditText.setCursorVisible(false);
                } else {
                    binding.dollarOffText.setText(getDollarOffValue(zeroInDouble));
                }

            } else {
                otherAmountEditText.setEnabled(false);
                otherAmountEditText.setText("");
                otherAmountEditText.setHint(mContext.getString(R.string.other_amount));
                binding.dollarOffText.setVisibility(View.GONE);
            }
            binding.radioBtn.setOnClickListener(v -> {
                if (selectedPos != getAdapterPosition()) {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    isPreAuthChanges = false;
                }
                otherAmountEditText.setEnabled(true);
            });

            otherAmountEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    hideKeyBoard();
                }
            });
        }
    }

    private void getRoundOffValue() throws ParseException {
        preAuthValue = replaceChars(preAuthValue);
        double selectedFuelValue = numberInstance.parse(preAuthValue).doubleValue();
        if ((selectedFuelValue * 1000) < petroPoints) {
            DecimalFormat df = new DecimalFormat("###.#");
            resultantValue = df.format(1000 * selectedFuelValue);
        } else {
            resultantValue = CardFormatUtils.formatBalance(petroPoints);
        }
        roundOffValue = numberInstance.parse(resultantValue).doubleValue();
    }

    interface RedeemPointsCallback {
        void onRedeemPointsChanged(String redeemPoints, String selectedRadioButton, boolean isRedemptionChanged);
    }
}
