package suncor.com.android.ui.main.carwash.reload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.model.carwash.reload.TransactionProduct;
import suncor.com.android.ui.main.wallet.cards.details.ExpandedCardItem;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;


public class CardsDropDownAdapter extends DropDownAdapter {

    private static final String TAG = CardReloadValuesDropDownAdapter.class.getSimpleName();
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    private static final int DROP_DOWN_LAYOUT = 1;

    private List<ExpandedCardItem> childList;
    private int selectedPos = 0;
    private ChildViewListener listener;
    private CardCallbacks callbackListener;
    private String cardNumber;

    private final Context mContext;


    CardsDropDownAdapter(final Context context, List<ExpandedCardItem> cards, final CardCallbacks callbackListener, String cardNumber) {
        this.childList = cards;
        this.mContext = context;
        this.callbackListener = callbackListener;
        this.cardNumber = cardNumber;
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
        for(ExpandedCardItem card: childList){
            if( card.getCardNumber().equals(cardNumber)){
                selectedPos = position;
                listener.onSelectValue(card.getCardName(), card.getCardNumber());
                notifyDataSetChanged();
                return;
            }
            position++;
        }
    }



    @Override
    public String getSelectedValue(){
     	return childList.get(selectedPos).getCardName();
    }

    @Override
    public String getSelectedSubValue() {
        return childList.get(selectedPos).getCardNumber();
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

            public void setDataOnView(ExpandedCardItem cardDetail){
                binding.title.setText(cardDetail.getCardName());
                binding.subTitle.setText(cardDetail.getCardType().equals("SP") ? String.format(mContext.getString(R.string.cards_days), String.valueOf(cardDetail.getCardDetail().getBalance())) :
                        String.format(mContext.getString(R.string.cards_washes), String.valueOf(cardDetail.getCardDetail().getBalance())));
                binding.subheader.setText(cardDetail.getCardNumber());

                binding.container.setSelected(selectedPos== getAdapterPosition());
                binding.container.setOnClickListener(v -> {
                    notifyItemChanged(selectedPos);
                    selectedPos = getAdapterPosition();
                    notifyItemChanged(selectedPos);
                    if(Objects.nonNull(listener)) {
                        listener.onSelectValue(cardDetail.getCardName(), cardDetail.getCardNumber());
                        listener.expandCollapse();
                        callbackListener.onSelectCardChanged(cardDetail.getCardNumber(), cardDetail.getCardNumber());
                    }
                });

            }
        }

    interface CardCallbacks {
        void onSelectCardChanged(String cardId, String cardNumber);
    }


}

