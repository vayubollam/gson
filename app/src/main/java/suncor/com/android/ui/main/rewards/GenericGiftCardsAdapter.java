package suncor.com.android.ui.main.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.databinding.EgiftCardItemBinding;
import suncor.com.android.databinding.RewardsListItemBinding;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;
import suncor.com.android.utilities.Consumer;


public class GenericGiftCardsAdapter extends RecyclerView.Adapter< RecyclerView.ViewHolder> {

    private static Consumer<GenericEGiftCard> clickListener = null;
    private static Consumer<GenericEGiftCard> clickListenerForFurtherUse = null;
    private ArrayList<GenericEGiftCard> genericEGiftCards;

    public static final int REWARDS_LAYOUT = 3;
    public static final int MERCHANT_LAYOUT= 4;

    public static boolean isEligible;

    public GenericGiftCardsAdapter(ArrayList<GenericEGiftCard> genericEGiftCards, Consumer<GenericEGiftCard> clickListener, boolean isEligible) {
        this.genericEGiftCards = genericEGiftCards;
        this.clickListener = clickListener;
        this.clickListenerForFurtherUse = clickListener;
        this.isEligible = isEligible;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MERCHANT_LAYOUT){
            return new MerchantCardViewHolder(EgiftCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }else{
            return new RewardsViewHolder(RewardsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {
        if(genericEGiftCards.get(position).isDataDynamic()){
            ((MerchantCardViewHolder)holder).setDataInView(genericEGiftCards.get(position));
        }else{
            ((RewardsViewHolder)holder).setDataInView(genericEGiftCards.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return genericEGiftCards.size();
    }

    @Override
    public int getItemViewType(int position) {
        return genericEGiftCards.get(position).isDataDynamic() ? MERCHANT_LAYOUT : REWARDS_LAYOUT;
    }

    public static class RewardsViewHolder extends RecyclerView.ViewHolder {
        private RewardsListItemBinding binding;

        public RewardsViewHolder(@NonNull RewardsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataInView(GenericEGiftCard giftCard){
            Context context = binding.getRoot().getContext();
            int imageId = context.getResources().getIdentifier(giftCard.getLargeImage(), "drawable", context.getPackageName());
            binding.setGiftCard(giftCard);
            binding.setImage(context.getDrawable(imageId));

            binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListenerForFurtherUse != null){
                        clickListenerForFurtherUse.accept(giftCard);
                    }
                }
            });
            binding.executePendingBindings();
        }
    }

    public static class MerchantCardViewHolder extends RecyclerView.ViewHolder {
        EgiftCardItemBinding binding;

        public MerchantCardViewHolder(@NonNull EgiftCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataInView(GenericEGiftCard giftCard){
            Context context = binding.getRoot().getContext();
            int imageId = context.getResources().getIdentifier(giftCard.getLargeImage(), "drawable", context.getPackageName());
            binding.setImage(context.getDrawable(imageId));
            binding.setGiftCard(giftCard);

            if(!isEligible){
                clickListener = null;
                binding.constraintLayout.setDisabled(true);
                binding.constraintLayout.setAlpha(0.5f);
                binding.title.setAlpha(0.5f);
                binding.redemptionUnavailableText.setVisibility(View.VISIBLE);
            }else{
                clickListener = clickListenerForFurtherUse;
                binding.constraintLayout.setDisabled(false);
                binding.constraintLayout.setAlpha(1f);
                binding.title.setAlpha(1f);
                binding.redemptionUnavailableText.setVisibility(View.GONE);
            }

            binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickListener != null){
                        clickListener.accept(giftCard);
                    }
                }
            });

            binding.executePendingBindings();
        }
    }
}
