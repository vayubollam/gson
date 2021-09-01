package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import suncor.com.android.databinding.ItemMoreEGiftCardSubCategoryBinding;

public class MoreEGiftCardSubCategoryAdapter extends RecyclerView.Adapter<MoreEGiftCardSubCategoryAdapter.MoreEGiftCardSubCategoryViewHolder> {


    @NonNull
    @NotNull
    @Override
    public MoreEGiftCardSubCategoryViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemMoreEGiftCardSubCategoryBinding binding = ItemMoreEGiftCardSubCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoreEGiftCardSubCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MoreEGiftCardSubCategoryViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MoreEGiftCardSubCategoryViewHolder extends RecyclerView.ViewHolder {

        ItemMoreEGiftCardSubCategoryBinding binding;

        public MoreEGiftCardSubCategoryViewHolder(@NonNull ItemMoreEGiftCardSubCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataInView() {

        }
    }


}
