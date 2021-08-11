package suncor.com.android.ui.main.carwash.reload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.model.carwash.reload.TransactionProduct;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;


public class CardReloadValuesDropDownAdapter extends DropDownAdapter {

    private static final String TAG = CardReloadValuesDropDownAdapter.class.getSimpleName();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private static final int DROP_DOWN_LAYOUT = 1;

    private  List<TransactionProduct> childList;
    private int selectedPos = 0;
    private ChildViewListener listener;
    private CardReloadValuesCallbacks callbackListener;

    private final Context mContext;
    private String cardType;


    CardReloadValuesDropDownAdapter(final Context context, final List<TransactionProduct> data, final CardReloadValuesCallbacks callbackListener,
                                    String cardType) {
        this.childList = data;
        this.mContext = context;
        this.callbackListener = callbackListener;
        this.cardType = cardType;
        formatter.setMinimumFractionDigits(0);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ChildDropDownViewHolder(FuelUpLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((ChildDropDownViewHolder)holder).setDataOnView(childList.get(position));

    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return DROP_DOWN_LAYOUT ;
    }

    public void  setDefautValue(){
        int position = 0;
        for(TransactionProduct product: childList){
            if(cardType.equals("SP") && product.getUnits().equals("90")){
              selectedPos = position;
                listener.onSelectValue( String.format(mContext.getString(R.string.cards_days), product.getUnits()),
                        formatter.format(Double.valueOf(product.getPrice())));
                return;
            } else if(cardType.equals("WAG") && product.getUnits().equals("5")){
                listener.onSelectValue( String.format(mContext.getString(R.string.cards_washes), product.getUnits()),
                        formatter.format(Double.valueOf(product.getPrice())));
                return;
            }
            position++;
        }
    }


    @Override
    public String getSelectedValue(){
        return  cardType.equals("SP") ? String.format(mContext.getString(R.string.cards_days), childList.get(selectedPos).getUnits()): String.format(mContext.getString(R.string.cards_washes), childList.get(selectedPos).getUnits());
    }

    @Override
    public String getSelectedSubValue() {
        if(selectedPos < childList.size()){
            return formatter.format(Double.parseDouble(childList.get(selectedPos).getPrice()));
        }
        return "";
    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;
        setDefautValue();
    }


    //fixed limit listing
     class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        FuelUpLimitDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }

            public void setDataOnView(TransactionProduct product){
                binding.title.setText(cardType.equals("SP") ? String.format(mContext.getString(R.string.cards_days), product.getUnits()): String.format(mContext.getString(R.string.cards_washes), product.getUnits()));
                try {
                    binding.subTitle.setText(formatter.format(Double.valueOf(product.getPrice())));
                }catch (NullPointerException ex){
                    Timber.e(TAG,  "Error on inflating data , " + ex.getMessage());
                }

                binding.container.setSelected(selectedPos== getAdapterPosition());
                binding.container.setOnClickListener(v -> {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    if(Objects.nonNull(listener)) {
                        listener.onSelectValue(cardType.equals("SP") ? String.format(mContext.getString(R.string.cards_days), product.getUnits()): String.format(mContext.getString(R.string.cards_washes), product.getUnits()),
                                formatter.format(Double.valueOf(product.getPrice())));
                        listener.expandCollapse();
                    }
                });
            }
        }

}

interface CardReloadValuesCallbacks {
    void onValueChanged(String value);
}
