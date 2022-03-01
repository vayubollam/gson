package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import suncor.com.android.databinding.ItemMoreEGiftCardSubCategoryBinding;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardSubCategory;

interface OnCardClickListener {
    void onCardClicked(ThirdPartyGiftCardSubCategory subCategory);
}

public class MoreEGiftCardSubCategoryAdapter extends RecyclerView.Adapter<MoreEGiftCardSubCategoryAdapter.MoreEGiftCardSubCategoryViewHolder> {

    private static OnCardClickListener clickListener;
    private final Context context;
    List<ThirdPartyGiftCardSubCategory> subCategoryList;

    public MoreEGiftCardSubCategoryAdapter(Context context, List<ThirdPartyGiftCardSubCategory> subCategoryList, OnCardClickListener clickListener) {
        this.subCategoryList = subCategoryList;
        this.context = context;
        this.clickListener = clickListener;
    }


    @NonNull
    @NotNull
    @Override
    public MoreEGiftCardSubCategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMoreEGiftCardSubCategoryBinding binding = ItemMoreEGiftCardSubCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoreEGiftCardSubCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MoreEGiftCardSubCategoryViewHolder holder, int position) {

        holder.setDataInView(context, subCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public static class MoreEGiftCardSubCategoryViewHolder extends RecyclerView.ViewHolder {

        ItemMoreEGiftCardSubCategoryBinding binding;

        public MoreEGiftCardSubCategoryViewHolder(@NonNull ItemMoreEGiftCardSubCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataInView(Context cont, ThirdPartyGiftCardSubCategory subcategory) {

            binding.textView.setText(subcategory.getSubcategoryName());
            int imageId = cont.getResources().getIdentifier(subcategory.getSmallIcon(), "drawable", cont.getPackageName());
            binding.setImage(cont.getDrawable(imageId));

            binding.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onCardClicked(subcategory);
                    }
                }
            });
        }
    }

}
