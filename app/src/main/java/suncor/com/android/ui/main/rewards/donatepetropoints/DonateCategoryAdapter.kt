package suncor.com.android.ui.main.rewards.donatepetropoints

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.databinding.ItemMoreEGiftCardCategoriesBinding
import suncor.com.android.model.redeem.response.Category

class DonateCategoryAdapter(
    private val context: Context,
    private var categoriesList: List<Category>
): RecyclerView.Adapter<DonateCategoryAdapter.DonateCategoryViewHolder>() {

    class DonateCategoryViewHolder(var binding: ItemMoreEGiftCardCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setDataInView(context: Context?, category: Category) {
            binding.titleTextView.text = category.info.title
            val adapter = context?.let {
                DonateProgramsAdapter(
                    it,
                    category.programs)
            }
            binding.childRecycler.adapter = adapter
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateCategoryViewHolder {
        val binding = ItemMoreEGiftCardCategoriesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DonateCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonateCategoryViewHolder, position: Int) {
        holder.setDataInView(context, categoriesList[position])
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }
}