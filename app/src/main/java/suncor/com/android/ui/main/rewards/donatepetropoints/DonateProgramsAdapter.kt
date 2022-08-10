package suncor.com.android.ui.main.rewards.donatepetropoints

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.databinding.ItemMoreEGiftCardSubCategoryBinding
import suncor.com.android.model.redeem.response.Program

class DonateProgramsAdapter(
    private val context: Context,
    private var programs: List<Program>
) : RecyclerView.Adapter<DonateProgramsAdapter.DonateProgramsViewHolder>() {

    class DonateProgramsViewHolder(var binding: ItemMoreEGiftCardSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setDataInView(cont: Context, programs: Program) {
            binding.textView.text = programs.info.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonateProgramsViewHolder {
        val binding = ItemMoreEGiftCardSubCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DonateProgramsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DonateProgramsViewHolder, position: Int) {
        holder.setDataInView(context, programs.get(position))
    }

    override fun getItemCount(): Int {
        return programs.size
    }
}