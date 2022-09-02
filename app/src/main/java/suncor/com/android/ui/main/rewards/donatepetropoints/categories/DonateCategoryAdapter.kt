package suncor.com.android.ui.main.rewards.donatepetropoints.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.databinding.ItemMoreEGiftCardCategoriesBinding
import suncor.com.android.model.redeem.response.Category
import suncor.com.android.model.redeem.response.Program
import suncor.com.android.utilities.Consumer

class DonateCategoryAdapter(
    private val context: Context,
    private var categoriesList: List<Category>,
     val clickListener: Consumer<Program>

): RecyclerView.Adapter<DonateCategoryAdapter.DonateCategoryViewHolder>() {

    class DonateCategoryViewHolder(var binding: ItemMoreEGiftCardCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root){

        fun setDataInView(context: Context?, category: Category, clickListener: Consumer<Program>) {
            binding.titleTextView.text = category.info.title
            val adapter = context?.let {
                DonateProgramsAdapter(
                    it,
                    category.programs,
                    object : OnCardClickListener {
                        override fun onCardClicked(program: Program) {
                           clickListener.accept(program)
                        }
                    })
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
        holder.setDataInView(context, categoriesList[position],clickListener )
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }
}