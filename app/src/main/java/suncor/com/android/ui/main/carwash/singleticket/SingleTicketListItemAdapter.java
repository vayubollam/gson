package suncor.com.android.ui.main.carwash.singleticket;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
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
import suncor.com.android.databinding.SingleTicketListitemBinding;
import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.utilities.Consumer;

import static suncor.com.android.utilities.Constants.ALPHA;
import static suncor.com.android.utilities.Constants.TRANS_Y;

public class SingleTicketListItemAdapter extends RecyclerView.Adapter<SingleTicketListItemAdapter.SingleTicketViewHolder> {
    private List<PetroCanadaProduct> singleTicketList;
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
    private boolean isFirstTime = false;
    private Context context;


    public SingleTicketListItemAdapter(List<PetroCanadaProduct> products, int petroPoints, Consumer<Integer> callBack, Context context) {
        this.singleTicketList = products;
        this.petroPoints = petroPoints;
        this.callBack = callBack;
        animInterpolator = new DecelerateInterpolator(3f);
        this.context = context;
    }

    @NonNull
    @Override
    public SingleTicketListItemAdapter.SingleTicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SingleTicketListitemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.single_ticket_listitem, parent, false);
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(binding.singleTicketItemLayout.getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        binding.singleTicketItemLayout.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        itemHeight = (float) binding.singleTicketItemLayout.getMeasuredHeight();
        if (isFirstTime) callBack.accept(0);
        isFirstTime = false;
        return new SingleTicketViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SingleTicketListItemAdapter.SingleTicketViewHolder holder, int position) {
        holder.binding.setSingleTicket(singleTicketList.get(position));
        holder.binding.setPetroPoints(petroPoints);
        if (position != singleTicketList.size() - 1) {
            if (singleTicketList.get(position).getPointsPrice() > petroPoints) {
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
        holder.binding.valueRb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isViewAnimating && isChecked) {
                buttonView.setChecked(false);
            }
        });
        holder.binding.valueRb.setOnClickListener(v -> holder.itemView.callOnClick());
        holder.itemView.setOnClickListener(v -> {
            if (singleTicketList.get(position).getPointsPrice() > petroPoints || !itemsExpanded || isViewAnimating) {
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
            ObjectAnimator animationY = ObjectAnimator.ofFloat(holder.itemView, TRANS_Y, -getItemHeight() * position);
            ObjectAnimator animationAlpha = ObjectAnimator.ofFloat(holder.itemView, ALPHA, 1f, 0f);
            ObjectAnimator animationValueRBAlpha = ObjectAnimator.ofFloat(holder.binding.valueRb, ALPHA, 1f, 0f);
            ObjectAnimator animationTxtSelectionAlpha = ObjectAnimator.ofFloat(holder.binding.txtSelectedCardGift, ALPHA, 0f, 1f);
            holder.binding.txtSelectedCardGift.setText(context.getResources().getQuantityString(R.plurals.single_ticket_receipt_quantity,
                    singleTicketList.get(selectedItem).getUnits(),  singleTicketList.get(selectedItem).getUnits()));
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(ANIM_DURATION / 2);
            if (position == selectedItem) {
                animatorSet.play(animationY).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            } else {
                animatorSet.play(animationY).with(animationAlpha).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            }
            animatorSet.start();
            holder.binding.itemDivider.setVisibility(View.INVISIBLE);
            if (position == singleTicketList.size() - 1) {
                shouldHideTheRest = false;
            }
        }
        if (shouldShowValues) {
            itemsExpanded = true;
            ObjectAnimator animationY = ObjectAnimator.ofFloat(holder.itemView, TRANS_Y, 0f);
            ObjectAnimator animationItemViewAlpha = ObjectAnimator.ofFloat(holder.itemView, ALPHA, 0f, 1f);
            ObjectAnimator animationValueRBAlpha = ObjectAnimator.ofFloat(holder.binding.valueRb, ALPHA, 0f, 1f);
            ObjectAnimator animationTxtSelectionAlpha = ObjectAnimator.ofFloat(holder.binding.txtSelectedCardGift, ALPHA, 1f, 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(ANIM_DURATION / 2);
            animatorSet.play(animationY).with(animationItemViewAlpha).with(animationValueRBAlpha).with(animationTxtSelectionAlpha);
            animatorSet.start();
            holder.binding.itemDivider.setVisibility(View.VISIBLE);
            if (position == singleTicketList.size() - 1) {
                shouldShowValues = false;
            }

        }


    }


    @Override
    public int getItemCount() {
        return singleTicketList.size();
    }

    public void showValues() {
        shouldShowValues = true;
        notifyDataSetChanged();
    }

    public class SingleTicketViewHolder extends RecyclerView.ViewHolder {
        SingleTicketListitemBinding binding;

        public SingleTicketViewHolder(@NonNull SingleTicketListitemBinding binding) {
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

    public void initialLaunch() {
        selectedItem = 0;
        shouldHideTheRest = true;
        notifyDataSetChanged();
        isFirstTime = true;
    }
}


