package suncor.com.android.ui.main.rewards.redeem;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
    private Float itemHeight;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 600;
    private boolean itemsExpanded = true;
    private boolean isViewAnimating = false;



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
        ColorStateList currentTint= holder.binding.valueRb.getButtonTintList();
        holder.binding.valueRb.setButtonTintList(position == selectedItem?ColorStateList.valueOf(holder.itemView.getContext().getColor(R.color.colorAccent)):currentTint);

        holder.binding.valueRb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isViewAnimating && isChecked) {
                buttonView.setChecked(false);
            }
        });
        holder.binding.valueRb.setOnClickListener(v -> holder.itemView.callOnClick());
        holder.itemView.setOnClickListener(v -> {
            if (eGifts.get(position).getPetroPointsRequired() > petroPoints || !itemsExpanded || isViewAnimating) {
                return;
            }
            if (itemHeight == null) {
                setItemHeight(holder.itemView.getMeasuredHeight());
            }
            callBack.accept(position);
            if (selectedItem == position) {
                holder.binding.valueRb.setChecked(true);
                shouldHideTheRest = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetChanged();
                selectedItem = position;
                shouldHideTheRest = true;
            }
        });
        if (shouldHideTheRest) {
            itemsExpanded = false;
            ObjectAnimator animationY = ObjectAnimator.ofFloat(holder.itemView, "translationY", -getItemHeight() * position);
            ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 1f, 0f);
            ObjectAnimator animationValueRBAlpha = ObjectAnimator.ofFloat(holder.binding.valueRb, "alpha", 1f, 0f);
            ObjectAnimator animationTxtSelectionAlpha = ObjectAnimator.ofFloat(holder.binding.txtSelectedCardGift, "alpha", 0f, 1f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(ANIM_DURATION / 2);
            if (position == selectedItem) {
                animatorSet.play(animationY).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            } else {
                animatorSet.play(animationY).with(animationAlpha).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            }
            animatorSet.start();
            holder.binding.itemDivider.setVisibility(View.INVISIBLE);
            if (position == eGifts.size() - 1) {
                shouldHideTheRest = false;
            }
        }
        if (shouldShowValues) {
            itemsExpanded = true;
            ObjectAnimator animationY = ObjectAnimator.ofFloat(holder.itemView, "translationY", 0f);
            ObjectAnimator animationItemViewAlpha = ObjectAnimator.ofFloat(holder.itemView, "alpha", 0f, 1f);
            ObjectAnimator animationValueRBAlpha = ObjectAnimator.ofFloat(holder.binding.valueRb, "alpha", 0f, 1f);
            ObjectAnimator animationTxtSelectionAlpha = ObjectAnimator.ofFloat(holder.binding.txtSelectedCardGift, "alpha", 1f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(ANIM_DURATION / 2);
            animatorSet.play(animationY).with(animationItemViewAlpha).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            animatorSet.start();
            holder.binding.itemDivider.setVisibility(View.VISIBLE);
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
