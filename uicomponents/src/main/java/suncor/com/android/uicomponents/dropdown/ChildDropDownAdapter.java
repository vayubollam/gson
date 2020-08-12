package suncor.com.android.uicomponents.dropdown;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.Objects;

import suncor.com.android.uicomponents.R;
import suncor.com.android.uicomponents.databinding.ChildDropDownItemBinding;
import suncor.com.android.uicomponents.databinding.ManualLimitDropDownItemBinding;


public class ChildDropDownAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChildDropDownAdapter.class.getSimpleName();

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int MANUAL_DROP_DOWN_LAYOUT = 2;

        private  HashMap<String,String> childList;
        private int selectedPos = 0;
        private ChildViewListener listener;
        private final int otherLimitMaxLimit;
        private final int otherLimitMinLimit;
        private final Context mContext;
        private int manualValue = -1;


        ChildDropDownAdapter(final Context context, final HashMap<String,String> data, ChildViewListener listener, final int otherLimitMaxLimit,
                             final int otherLimitMinLimit, Double lastFuelUpTransaction) {
            this.childList = data;
            this.listener = listener;
            this.otherLimitMaxLimit = otherLimitMaxLimit;
            this.otherLimitMinLimit = otherLimitMinLimit;
            this.mContext = context;
            findLastFuelUpTransaction(lastFuelUpTransaction);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == MANUAL_DROP_DOWN_LAYOUT){
                return new ManualLimitViewHolder(ManualLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
            } else {
                return new ChildDropDownViewHolder(ChildDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
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

    private void findLastFuelUpTransaction(Double lastFuelupTransaction){
            if(lastFuelupTransaction != null){
                childList.forEach((position, value)-> {
                    if(Integer.parseInt(position) != childList.size() && lastFuelupTransaction.intValue() == Integer.parseInt(value) ){
                        selectedPos =  Integer.parseInt(position) -1;
                        listener.onSelectFuelUpLimit(Integer.parseInt(value));
                    }
                });
                if(selectedPos == 0){
                    manualValue = lastFuelupTransaction.intValue();
                    selectedPos = childList.size() -1 ;
                    listener.onSelectFuelUpLimit(manualValue);
                }
            }
    }

    protected int getSelectedValue(){
            if(selectedPos < childList.size() - 1){
                return Integer.parseInt(childList.get(String.valueOf(selectedPos + 1)));
            } else if(manualValue <  otherLimitMinLimit) {
                manualValue =  otherLimitMinLimit;
            } else if (manualValue > otherLimitMaxLimit)  {
                manualValue =  otherLimitMaxLimit;
             }
     	return manualValue;
	
    }

//fixed limit listing
     class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
            ChildDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull ChildDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setDataOnView(String value){
                try {
                    binding.title.setText(String.format("$%s", value));
                }catch (NullPointerException ex){
                    Log.e(TAG,  "Error on inflating data , " + ex.getMessage());
                }
                binding.container.setSelected(selectedPos== getAdapterPosition());

                binding.radioBtn.setOnClickListener(v -> {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    manualValue = -1;
                    if(Objects.nonNull(listener)) {
                        listener.onSelectFuelUpLimit(Integer.parseInt(value));
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
            binding.inputField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(String.valueOf(otherLimitMaxLimit).length())});
        }

        public void setDataOnView(String value){
            binding.container.setSelected(selectedPos == getAdapterPosition());
            binding.manualLimit.setText(String.format(mContext.getString(R.string.fuel_manual_price_limit), String.format("$%s", otherLimitMinLimit), String.format("$%s", otherLimitMaxLimit)));
            binding.inputField.setVisibility((selectedPos == getAdapterPosition()) ? View.VISIBLE : View.GONE);
            binding.manualLimit.setVisibility((selectedPos == getAdapterPosition() && manualValue <= 0) ?  View.VISIBLE : View.GONE);
            binding.prefixCurrency.setText((selectedPos == getAdapterPosition()) ? mContext.getString(R.string.currency_dollar) : value);
            binding.inputField.setText(manualValue > 0 ? String.valueOf(manualValue) : "");
            binding.edit.setVisibility(manualValue > 0 ?  View.VISIBLE : View.GONE);

            binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                binding.manualLimit.setVisibility(View.VISIBLE);
                binding.inputField.requestFocus();
                binding.inputField.setSelection(binding.inputField.getText().length());
            }
            });

            binding.radioBtn.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);
                binding.inputField.setVisibility(View.VISIBLE);
                binding.manualLimit.setVisibility(View.VISIBLE);
                binding.prefixCurrency.setText(mContext.getString(R.string.currency_dollar));
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
                    manualValue = editable.length() > 0 ? Integer.parseInt(editable.toString()) : 0;
                    if(Objects.nonNull(listener) ) {
                        listener.onSelectFuelUpLimit(manualValue);
                    }
                }
            });
        }
    }
}
