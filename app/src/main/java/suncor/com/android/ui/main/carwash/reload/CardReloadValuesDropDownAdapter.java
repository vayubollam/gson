package suncor.com.android.ui.main.carwash.reload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;


public class CardReloadValuesDropDownAdapter extends DropDownAdapter {

    private static final String TAG = CardReloadValuesDropDownAdapter.class.getSimpleName();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private static final int DROP_DOWN_LAYOUT = 1;

    private  HashMap<String,String> childList;
    private int selectedPos = 0;
    private ChildViewListener listener;
    private CardReloadValuesCallbacks callbackListener;

    private final Context mContext;
    private double manualValue = -1;


    CardReloadValuesDropDownAdapter(final Context context, final HashMap<String,String> data, final CardReloadValuesCallbacks callbackListener) {
        this.childList = data;
        this.mContext = context;
        this.callbackListener = callbackListener;

        formatter.setMinimumFractionDigits(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChildDropDownViewHolder(FuelUpLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((ChildDropDownViewHolder)holder).setDataOnView(childList.get(String.valueOf(position + 1)));

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DROP_DOWN_LAYOUT ;
    }

    public void findLastFuelUpTransaction(Double lastFuelupTransaction){
        if(lastFuelupTransaction != null){
            childList.forEach((position, value)-> {
                if(Integer.parseInt(position) != childList.size() && lastFuelupTransaction.intValue() == Integer.parseInt(value) ){
                    selectedPos =  Integer.parseInt(position) - 1;
                    if(listener != null) {
                        listener.onSelectValue(formatter.format(Double.valueOf(value)), null);
                    }
                    callbackListener.onValueChanged(formatter.format(Double.valueOf(value)));
                }
            });
            if(selectedPos == 0){
                manualValue = lastFuelupTransaction.intValue();
                selectedPos = childList.size() -1 ;
                if(listener != null) {
                    listener.onSelectValue(formatter.format(manualValue), null);
                }
                callbackListener.onValueChanged(formatter.format(manualValue));
            }
        } else {
            callbackListener.onValueChanged(getSelectedValue());
        }
    }

    @Override
    public String getSelectedValue(){
        /*    if(selectedPos < childList.size() - 1){
                return formatter.format(Double.parseDouble(childList.get(String.valueOf(selectedPos + 1))));
            }*/
     	return "";
    }

    @Override
    public String getSelectedSubValue() {
        return null;
    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;
    }


    //fixed limit listing
     class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        FuelUpLimitDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setDataOnView(String price){
                binding.title.setText("100 days");
            }
                /*double value = Double.parseDouble(price);
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
                        callbackListener.onValueChanged(formatter.format(value));

                        listener.expandCollapse();
                    }
                });

            }*/
        }

}

interface CardReloadValuesCallbacks {
    void onValueChanged(String value);
}
