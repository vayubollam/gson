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
    private Context mContext;
    private String redeemCaps;

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int MANUAL_DROP_DOWN_LAYOUT = 2;
    public static final String TAG = "RedeemPointsAdapter";
    public HashMap<String, String> redeemPoints;
    private int selectedPos;
    private int selectedPosition;
    private String points;
    private String off;
    private final int petroPoints;
    private String preAuthValue  = null;
    private String dollarOffValue;
    private String resultantValue;
    private long roundOffValue;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    RedeemPointsDropDownAdapter(final Context context, HashMap<String, String> redeemPoints, int petroPoints) {

        this.mContext = context;
        this.redeemPoints = redeemPoints;
        this.petroPoints = petroPoints;
        formatter.setMinimumFractionDigits(0);
    }

    @Override
    public String getSelectedValue() {
        String dollarsToReturn;
         if(selectedPos == 1) {
             dollarsToReturn = dollarOffValue;
             return dollarsToReturn;
        }else{
             if(Locale.getDefault().getLanguage().equalsIgnoreCase("fr")){
                 return String.format("%s %s ", formatter.format(0), "de rabais");
             }else{
                 return String.format("%s %s ",formatter.format(0), "off");
             }
         }
    }

    @Override
    public String getSelectedSubValue() {
        long resultantValueToReturn;
        if(selectedPos == 1) {
            resultantValueToReturn = roundOffValue;
        }else{
            resultantValueToReturn  = 0;
        }

        return CardFormatUtils.formatBalance((int)resultantValueToReturn) + " " + "points";

    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;

    }

    public void setPreAuthValue(String preAuthValue){
        this.preAuthValue = preAuthValue;
    }



    private String getDollarOffValue(int amount){
        if(amount < 10){
            if(Locale.getDefault().getLanguage().equalsIgnoreCase("fr")){
                return String.format("%s %s ",formatter.format(0), off);
            }else{
                return String.format("%s %s ", formatter.format(0), off);
            }
        }else if(amount%10 > 0){
            amount = amount - amount%10;
        }
        amount = amount/1000;
        if(amount >= 1){

            DecimalFormat df = new DecimalFormat("#,###.00");
            if(Locale.getDefault().getLanguage().equalsIgnoreCase("fr")){
                return String.format("%s %s %s",  df.format(amount), "$", off);
            }else{
                return String.format("%s %s ", "$"+ df.format(amount), off);
            }

        }

        if(Locale.getDefault().getLanguage().equalsIgnoreCase("fr")){
            return String.format("%s %s", formatter.format(amount), off);
        }else{
            return String.format("%s %s ", formatter.format(amount), off);
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
    public void showKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyBoard() {
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private long roundingThePetroPointsToNearestTen(int points){
        long pointsToReturn = Math.round(points/10.0) * 10;
        return pointsToReturn;
    }

    class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        FuelUpLimitDropDownItemBinding binding;

        ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataOnView(String price) {

            try {

                if (((Integer.parseInt(preAuthValue.replaceAll("[\\D]" , ""))) * 1000) < petroPoints) {
                    resultantValue = CardFormatUtils.formatBalance((Integer.parseInt(preAuthValue.replaceAll("[\\D]", "")))*1000);

                }else {
                    resultantValue = CardFormatUtils.formatBalance(petroPoints);
                }

                if (selectedPosition == 1) {
                    binding.dollarOff.setVisibility(View.VISIBLE);

                     roundOffValue = roundingThePetroPointsToNearestTen(Integer.parseInt(resultantValue.replaceAll("[\\D]", "")));
                     dollarOffValue = getDollarOffValue((int) roundOffValue);
                    binding.dollarOff.setText(dollarOffValue);


                    binding.title.setText(String.format("%s %s %s", redeemCaps, CardFormatUtils.formatBalance((int)roundOffValue), points));
                    hideKeyBoard();
                } else {
                    binding.title.setText(price);
                    binding.dollarOff.setVisibility(View.GONE);
                }

            }  catch (NullPointerException ex) {
                Timber.e(TAG, "Error on inflating data , " + ex.getMessage());
            }catch (Exception e){
                Timber.e(TAG, "Error, " + e.getMessage());
            }
            binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.container.setOnClickListener(v -> {
                hideKeyBoard();
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                if (Objects.nonNull(listener)) {
                    if(selectedPos == 1){
                        long roundOffValue = roundingThePetroPointsToNearestTen(Integer.parseInt(resultantValue.replaceAll("[\\D]", "")));

                   listener.onSelectValue(dollarOffValue, roundOffValue+ points, false);
                    }else{
                       listener.onSelectValue(formatter.format(0), formatter.format(0)+ points, true);
                    }
                    listener.expandCollapse();
                }
            });

        }
    }

    class OtherAmountViewHolder extends RecyclerView.ViewHolder {
        OtherAmountBinding binding;

        OtherAmountViewHolder(@NonNull OtherAmountBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void setDataOnView() {
            EditText otherAmountEditText = binding.inputField;
            binding.preAuthTip.setVisibility(View.VISIBLE);
            otherAmountEditText.setText("");
            binding.radioBtn.setSelected(selectedPos == getAdapterPosition());
            if(binding.radioBtn.isSelected()){
                otherAmountEditText.requestFocus();
                otherAmountEditText.setHint("");
                binding.dollarOffText.setVisibility(View.VISIBLE);
            binding.dollarOffText.setText(R.string.zero_dollar_off);
            }else{
                otherAmountEditText.setHint(mContext.getString(R.string.other_amount));
                binding.dollarOffText.setVisibility(View.GONE);
            }
            binding.radioBtn.setOnClickListener(v -> {
                if(selectedPos != getAdapterPosition()) {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                }
            });

            otherAmountEditText.setOnEditorActionListener((v, actionId, event) -> {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    if (Objects.nonNull(listener)) {
                        listener.expandCollapse();
                    }
                }
                return false;
            });

            otherAmountEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().isEmpty()) {
                        otherAmountEditText.removeTextChangedListener(this);
                        String amount = s.toString().replaceAll("\\s+", "");
                        amount = amount.replaceAll(",", "");
                        otherAmountEditText.setText(getFormattedPoints(Double.parseDouble(amount)));
                        otherAmountEditText.addTextChangedListener(this);
                        otherAmountEditText.setSelection(otherAmountEditText.getText().length());
                        binding.dollarOffText.setVisibility(View.VISIBLE);
                        binding.dollarOffText.setText(getDollarOffValue(amount));
                    }else{
                        binding.dollarOffText.setVisibility(View.GONE);
                    }
                }
            });
        }

        private String getFormattedPoints(double amount) {
            return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount);
        }

        private String getDollarOffValue(String amount){
            double amt = Double.parseDouble(amount);
            if(amt < 10){
                return "$0 off";
            }else if(amt%10 > 0){
                amt = amt - amt%10;
            }
            amt = amt/1000;
            if(amt >= 1){
                DecimalFormat df = new DecimalFormat("#.00");
                return "$"+df.format(amt)+" off";
            }
            return "$"+amt+" off";
        }
    }
}
