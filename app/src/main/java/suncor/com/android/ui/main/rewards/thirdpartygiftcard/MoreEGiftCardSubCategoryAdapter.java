package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import suncor.com.android.databinding.ItemMoreEGiftCardSubCategoryBinding;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardSubCategory;

public class MoreEGiftCardSubCategoryAdapter extends RecyclerView.Adapter<MoreEGiftCardSubCategoryAdapter.MoreEGiftCardSubCategoryViewHolder> {

    private final Context context;
    List<ThirdPartyGiftCardSubCategory> subCategoryList;

    public MoreEGiftCardSubCategoryAdapter(Context context, List<ThirdPartyGiftCardSubCategory> subCategoryList) {
        this.subCategoryList = subCategoryList;
        this.context = context;
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

        //holder.binding.imageView2.layout(0, 0, 0, 0);
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

            //binding.imageView2.layout(0, 0, 0, 0);
            binding.textView.setText(subcategory.getSubcategoryName());
            int imageId = cont.getResources().getIdentifier(subcategory.getSmallIcon(), "drawable", cont.getPackageName());
            Glide.with(binding.imageView2.getContext())
                    .load(cont.getDrawable(imageId))
                    .into(binding.imageView2);
            //binding.setImage(cont.getDrawable(imageId));
        }
    }


}
