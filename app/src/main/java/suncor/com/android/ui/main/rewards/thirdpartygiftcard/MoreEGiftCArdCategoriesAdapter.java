package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import suncor.com.android.databinding.ItemMoreEGiftCardCategoriesBinding;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;

public class MoreEGiftCArdCategoriesAdapter extends RecyclerView.Adapter<MoreEGiftCArdCategoriesAdapter.MoreEGiftCArdCategoriesViewHolder> {

    private final List<ThirdPartyGiftCardCategory> categoriesList;
    private Context context;

    public MoreEGiftCArdCategoriesAdapter(Context context, List<ThirdPartyGiftCardCategory> categoriesList) {
        this.context = context;
        this.categoriesList = categoriesList;

    }

    @NonNull
    @NotNull
    @Override
    public MoreEGiftCArdCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMoreEGiftCardCategoriesBinding binding = ItemMoreEGiftCardCategoriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoreEGiftCArdCategoriesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreEGiftCArdCategoriesViewHolder holder, int position) {

        holder.setDataInView(context, categoriesList.get(position));


    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public static class MoreEGiftCArdCategoriesViewHolder extends RecyclerView.ViewHolder {

        ItemMoreEGiftCardCategoriesBinding binding;

        public MoreEGiftCArdCategoriesViewHolder(@NonNull ItemMoreEGiftCardCategoriesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataInView(Context context, ThirdPartyGiftCardCategory category) {
            binding.titleTextView.setText(category.getCategoryName());


            MoreEGiftCardSubCategoryAdapter adapter = new MoreEGiftCardSubCategoryAdapter(context, category.getThirdPartyGiftCardSubCategory());
            binding.childRecycler.setAdapter(adapter);

            binding.executePendingBindings();

        }
    }
}
