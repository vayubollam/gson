package suncor.com.android.ui.main.pap.fuelup;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.uicomponents.R;
import suncor.com.android.databinding.ManualLimitDropDownItemBinding;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;


public class FuelLimitDropDownAdapter extends DropDownAdapter {

    private static final String TAG = FuelLimitDropDownAdapter.class.getSimpleName();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int MANUAL_DROP_DOWN_LAYOUT = 2;

    private  HashMap<String,String> childList;
    private int selectedPos = 0;
    private ChildViewListener listener;
    private FuelUpLimitCallbacks callbackListener;
    private final int otherLimitMaxLimit;
    private final int otherLimitMinLimit;
    private final Context mContext;
    private double manualValue = -1;
    private String inputFieldText;

    FuelLimitDropDownAdapter(final Context context, final HashMap<String,String> data, final FuelUpLimitCallbacks callbackListener, final int otherLimitMaxLimit,
                             final int otherLimitMinLimit) {
        this.childList = data;
        this.otherLimitMaxLimit = otherLimitMaxLimit;
        this.otherLimitMinLimit = otherLimitMinLimit;
        this.mContext = context;
        this.callbackListener = callbackListener;

        formatter.setMinimumFractionDigits(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MANUAL_DROP_DOWN_LAYOUT){
            return new ManualLimitViewHolder(ManualLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ChildDropDownViewHolder(FuelUpLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position != childList.size()-1){
            ((ChildDropDownViewHolder)holder).setDataOnView(childList.get(String.valueOf(position + 1)));
        } else {
            ((ManualLimitViewHolder)holder).setDataOnView(childList.get(String.valueOf(position + 1)));
        }
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position != childList.size()-1 ? DROP_DOWN_LAYOUT : MANUAL_DROP_DOWN_LAYOUT;
    }

    public void findLastFuelUpTransaction(Double lastFuelupTransaction){
        if(lastFuelupTransaction != null){
            childList.forEach((position, value)-> {
                if(Integer.parseInt(position) != childList.size() && lastFuelupTransaction.intValue() == Integer.parseInt(value) ){
                    selectedPos =  Integer.parseInt(position) - 1;
                    if(listener != null) {
                        listener.onSelectValue(formatter.format(Double.valueOf(value)), null);
                    }
                    callbackListener.onPreAuthChanged(formatter.format(Double.valueOf(value)));
                }
            });
            if(selectedPos == 0){
                manualValue = lastFuelupTransaction.intValue();
                selectedPos = childList.size() -1 ;
                if(listener != null) {
                    listener.onSelectValue(formatter.format(manualValue), null);
                }
                callbackListener.onPreAuthChanged(formatter.format(manualValue));
            }
        } else {
            callbackListener.onPreAuthChanged(getSelectedValue());
        }
    }

    @Override
    public String getSelectedValue(){
            if(selectedPos < childList.size() - 1){
                return formatter.format(Double.parseDouble(childList.get(String.valueOf(selectedPos + 1))));
            } else if(manualValue <  otherLimitMinLimit) {
                manualValue =  otherLimitMinLimit;
            } else if (manualValue > otherLimitMaxLimit)  {
                manualValue =  otherLimitMaxLimit;
             }
     	return formatter.format(manualValue);
    }

    @Override
    public String getSelectedSubValue() {
        return null;
    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;
    }

    public void setSelectedPosfromValue(double value) {
        int index = 0;

        for (String price : childList.values()) {
            try {
                if (Double.parseDouble(price) == value) {
                    selectedPos = index;
                    break;
                }
            } catch (NumberFormatException ignored){}
            index++;
        }

        if (index >= childList.size() - 1) {
            selectedPos = childList.size() - 1;
            manualValue = value;

        }

        notifyDataSetChanged();
    }

    //fixed limit listing
     class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        FuelUpLimitDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setDataOnView(String price){
                double value = Double.parseDouble(price);
                try {
                    binding.title.setText(formatter.format(value));
                }catch (NullPointerException ex){
                    Timber.e(TAG,  "Error on inflating data , " + ex.getMessage());
                }
                binding.container.setSelected(selectedPos== getAdapterPosition());

                binding.container.setOnClickListener(v -> {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    manualValue = -1;
                    if(Objects.nonNull(listener)) {
                        listener.onSelectValue(formatter.format(value), null);
                        callbackListener.onPreAuthChanged(formatter.format(value));

                        listener.expandCollapse();
                    }
                });

            }
        }

	//Manual limit
     class ManualLimitViewHolder extends RecyclerView.ViewHolder {
        ManualLimitDropDownItemBinding binding;

        ManualLimitViewHolder(@NonNull ManualLimitDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.inputField.setFilters(new InputFilter[]{new InputFilter.LengthFilter( 3 + String.valueOf(otherLimitMaxLimit).length())});
        }

        public void setDataOnView(String value){
            binding.container.setSelected(selectedPos == getAdapterPosition());
            binding.manualLimit.setText(String.format(mContext.getString(R.string.fuel_manual_price_limit), formatter.format(otherLimitMinLimit), formatter.format(otherLimitMaxLimit)));
            binding.prefixCurrency.setText((selectedPos == getAdapterPosition()) ? mContext.getString(R.string.currency_dollar) : value);
            binding.inputField.setText(manualValue > 0 ? formatter.format(manualValue).replace("$", "") : "");
            inputFieldText = binding.inputField.getText().toString().replaceAll("\\s+", "");
            binding.inputField.setText(inputFieldText);
            binding.inputField.setVisibility(selectedPos == getAdapterPosition() ? View.VISIBLE : View.GONE);
            binding.manualLimit.setVisibility((manualValue == -1) ?  View.VISIBLE : View.GONE);
            binding.edit.setVisibility(manualValue > 0 ?  View.VISIBLE : View.GONE);
            binding.inputField.setEnabled(!(manualValue > 0));
            binding.container.setOnClickListener(v -> {

                if(selectedPos != getAdapterPosition()){
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    binding.inputField.setText(manualValue > 0 ? formatter.format(manualValue).replace("$","") : "");
                    inputFieldText = binding.inputField.getText().toString().replaceAll("\\s+", "");
                    binding.inputField.setText(inputFieldText);
                    binding.inputField.setVisibility(selectedPos == getAdapterPosition() ? View.VISIBLE : View.GONE);
                    binding.manualLimit.setVisibility((manualValue == -1) ?  View.VISIBLE : View.GONE);
                    binding.edit.setVisibility(manualValue > 0 ?  View.VISIBLE : View.GONE);
                    binding.inputField.setEnabled(!(manualValue > 0));
                } else if(binding.edit.getVisibility() == View.VISIBLE){
                   binding.edit.setVisibility(View.GONE);
                   binding.manualLimit.setVisibility(View.VISIBLE);
                   binding.inputField.setEnabled(true);
                   binding.inputField.requestFocus();
                   binding.inputField.setSelection(binding.inputField.getText().length());
                   binding.inputField.setFocusableInTouchMode(true);
                   InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                   imm.showSoftInput(binding.inputField, InputMethodManager.SHOW_FORCED);
                } else {
                    binding.edit.setVisibility(View.VISIBLE);
                    binding.manualLimit.setVisibility(View.GONE);
                    binding.inputField.setEnabled(false);
                    binding.inputField.setText(manualValue > 0 ? Double.valueOf(manualValue).toString() : "");
               }

               /* binding.inputField.setVisibility((selectedPos == getAdapterPosition()) ? View.VISIBLE : View.GONE);
                binding.edit.setVisibility(manualValue > 0 ?  View.VISIBLE : View.GONE);
                binding.manualLimit.setVisibility((binding.edit.getVisibility() == View.VISIBLE) ?  View.GONE : View.VISIBLE);
                binding.prefixCurrency.setText(mContext.getString(R.string.currency_dollar));

               binding.edit.setVisibility(binding.edit.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
               binding.manualLimit.setVisibility(binding.edit.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
               binding.inputField.setEnabled(binding.edit.getVisibility() == View.GONE);
               binding.inputField.requestFocus();
               binding.inputField.setSelection(binding.inputField.getText().length());*/
            });

            binding.inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        manualValue = editable.toString().trim().length() > 0 ? Double.valueOf(editable.toString()) : 0;
                        if (Objects.nonNull(listener) && selectedPos == childList.size() - 1 && manualValue >= 0) {
                            listener.onSelectValue(formatter.format(manualValue), null);
                            callbackListener.onPreAuthChanged(formatter.format(manualValue));
                        }
                    } catch (NumberFormatException ex){
                        Timber.e(TAG, "enter invalid number");
                    }
                }
            });


            binding.inputField.setOnKeyListener((view,  keyCode, event) -> {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        if(manualValue > 0) {
                            binding.inputField.setText(formatter.format(manualValue).replace("$",""));
                            inputFieldText = binding.inputField.getText().toString().replaceAll("\\s+", "");
                            binding.inputField.setText(inputFieldText);
                            binding.edit.setVisibility(manualValue > 0 ? View.VISIBLE : View.GONE);
                            binding.manualLimit.setVisibility((selectedPos == getAdapterPosition() && manualValue <= 0) ? View.VISIBLE : View.GONE);
                            binding.inputField.setEnabled(!(manualValue > 0));
                        }
                        return true;
                    }
                    return false;
            });
        }
    }

    interface FuelUpLimitCallbacks {
        void onPreAuthChanged(String value);
    }
}


