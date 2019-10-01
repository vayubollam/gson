package suncor.com.android.ui.main.rewards.redeem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import suncor.com.android.R;
import suncor.com.android.databinding.GiftCardValueItemBinding;
import suncor.com.android.model.merchants.EGift;
import suncor.com.android.utilities.Consumer;

public class GiftCardValueAdapter extends RecyclerView.Adapter<GiftCardValueAdapter.ValueViewHolder> {
    private List<EGift> eGifts;
    private int petroPoints;
    private int selectedItem = -1;
    private boolean shouldHideTheRest = false;
    private boolean shouldShowValues = false;
    private Consumer<Integer> callBack;
    private float itemHeight;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 300;
    private boolean itemsExpanded = true;


    public GiftCardValueAdapter(List<EGift> eGifts, int petroPoints, Consumer<Integer> callBack) {
        this.eGifts = eGifts;
        this.petroPoints = petroPoints;
        this.callBack = callBack;
        animInterpolator = new DecelerateInterpolator(3f);
    }

    @NonNull
    @Override
    public GiftCardValueAdapter.ValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        GiftCardValueItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.gift_card_value_item, parent, false);
        return new ValueViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GiftCardValueAdapter.ValueViewHolder holder, int position) {
        holder.binding.setEgift(eGifts.get(position));
        holder.binding.setPetroPoints(petroPoints);
        if (position != eGifts.size() - 1) {
            if (eGifts.get(position).getPetroPointsRequired() > petroPoints) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.binding.itemDivider.getLayoutParams();
                p.setMargins(holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.gift_card_value_disable_start_margin), 0, 0, 0);
                holder.binding.itemDivider.setLayoutParams(p);
            } else {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.binding.itemDivider.getLayoutParams();
                p.setMargins(holder.itemView.getContext().getResources().getDimensionPixelSize(R.dimen.gift_card_value_enable_start_margin), 0, 0, 0);
                holder.binding.itemDivider.setLayoutParams(p);
            }
        }
        holder.binding.executePendingBindings();
        holder.binding.valueRb.setChecked(position == selectedItem);
        holder.binding.valueRb.setOnClickListener(v -> holder.itemView.callOnClick());
        holder.itemView.setOnClickListener(v -> {
            if (eGifts.get(position).getPetroPointsRequired() > petroPoints || !itemsExpanded) {
                return;
            }
            setItemHeight(holder.itemView.getHeight());
            callBack.accept(position);
            if (selectedItem == position) {
                holder.binding.valueRb.setChecked(true);
                notifyDataSetChanged();
                shouldHideTheRest = true;
            } else {
                selectedItem = position;
                notifyDataSetChanged();
                shouldHideTheRest = true;

            }
        });
        if (shouldHideTheRest) {
            itemsExpanded = false;
            if (position != selectedItem) {
                holder.binding.getRoot().animate()
                        .translationY(-holder.binding.getRoot().getHeight() * position)
                        .alpha(0.0f)
                        .setInterpolator(animInterpolator)
                        .setDuration(ANIM_DURATION);
            } else {
                holder.binding.getRoot().animate().translationY(-holder.binding.getRoot().getHeight() * position)
                        .setInterpolator(animInterpolator)
                        .setDuration(ANIM_DURATION);
                holder.binding.itemDivider.setVisibility(View.INVISIBLE);
            }
            if (position == eGifts.size() - 1) {
                shouldHideTheRest = false;
            }

        }
        if (shouldShowValues) {
            itemsExpanded = true;
            if (position != selectedItem) {
                holder.binding.getRoot().setVisibility(View.VISIBLE);
                holder.binding.getRoot().animate()
                        .translationY(-(holder.binding.getRoot().getTranslationY() + (holder.binding.getRoot().getHeight() * position)))
                        .alpha(1.0f)
                        .setInterpolator(animInterpolator)
                        .setDuration(ANIM_DURATION);
            } else {
                holder.itemView.animate().translationY(-(holder.binding.getRoot().getTranslationY() + (holder.binding.getRoot().getHeight() * position)))
                        .setInterpolator(animInterpolator)
                        .setDuration(ANIM_DURATION);
                holder.binding.itemDivider.setVisibility(View.VISIBLE);
            }
            if (position == eGifts.size() - 1) {
                shouldShowValues = false;
            }

        }


    }


    @Override
    public int getItemCount() {
        return eGifts.size();
    }

    public void showValues() {
        shouldShowValues = true;
        notifyDataSetChanged();
    }

    public class ValueViewHolder extends RecyclerView.ViewHolder {
        GiftCardValueItemBinding binding;

        public ValueViewHolder(@NonNull GiftCardValueItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public float getItemHeight() {
        return itemHeight;
    }

    private void setItemHeight(float itemHeight) {
        this.itemHeight = itemHeight;
    }
}
