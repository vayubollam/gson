package suncor.com.android.ui.main.rewards.donatepetropoints

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.databinding.ItemMoreEGiftCardSubCategoryBinding
import suncor.com.android.model.redeem.response.Program
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardSubCategory
import suncor.com.android.utilities.Consumer

interface OnCardClickListener {
    fun onCardClicked(program: Program)
}

class DonateProgramsAdapter(
    private val context: Context,
    private var programs: List<Program>,
    private val listener: OnCardClickListener
) : RecyclerView.Adapter<DonateProgramsAdapter.DonateProgramsViewHolder>() {

    class DonateProgramsViewHolder(var binding: ItemMoreEGiftCardSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setDataInView(cont: Context, programs: Program, listener: OnCardClickListener) {
            binding.textView.text = programs.info.title
            val imageId = cont.resources.getIdentifier(
                programs.smallImage.toString(),
                "drawable",
                cont.packageName
            )
            binding.image = cont.getDrawable(imageId)

            binding.cardView.setOnClickListener {
              listener.onCardClicked(programs)
            }
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
        holder.setDataInView(context, programs[position], listener)
    }

    override fun getItemCount(): Int {
        return programs.size
    }
}