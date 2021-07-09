package suncor.com.android.ui.main.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.databinding.EgiftCardItemBinding;
import suncor.com.android.utilities.Consumer;

public class EGiftsCardAdapter extends RecyclerView.Adapter<EGiftsCardAdapter.CardHolder> {
    private ArrayList<MerchantItem> merchantItems;
    private Consumer<MerchantItem> callback;

    public EGiftsCardAdapter(ArrayList<MerchantItem> merchantItems, Consumer<MerchantItem> callback) {
        this.merchantItems = merchantItems;
        this.callback = callback;
    }

    @NonNull
    @Override
    public EGiftsCardAdapter.CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        EgiftCardItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.egift_card_item, parent, false);
        return new CardHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EGiftsCardAdapter.CardHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        int imageId = context.getResources().getIdentifier(merchantItems.get(position).getMerchantLargeImage(), "drawable", context.getPackageName());
        holder.binding.setImage(context.getDrawable(imageId));
 //       holder.binding.setMerchantItem(merchantItems.get(position));
        holder.binding.executePendingBindings();
        holder.itemView.setOnClickListener(v -> callback.accept(merchantItems.get(position)));
    }

    @Override
    public int getItemCount() {
        return merchantItems.size();
    }

    public class CardHolder extends RecyclerView.ViewHolder {
        EgiftCardItemBinding binding;

        public CardHolder(@NonNull EgiftCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
