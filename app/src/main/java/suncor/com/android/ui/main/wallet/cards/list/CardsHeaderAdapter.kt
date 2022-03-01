package suncor.com.android.ui.main.wallet.cards.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.ItemCardHeadBinding

class CardsHeaderAdapter(val callBack: () -> Unit) : RecyclerView.Adapter<CardsHeaderAdapter.CardsHeaderViewHolder>() {

    var list: List<PetroPointsCard> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    inner class CardsHeaderViewHolder(private val viewBinding: ItemCardHeadBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        fun onBind(item: PetroPointsCard) {
            viewBinding.pptsCard = item
            viewBinding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsHeaderViewHolder {
        val viewBinding = DataBindingUtil.inflate<ItemCardHeadBinding>(LayoutInflater.from(parent.context), R.layout.item_card_head, parent, false)
        viewBinding.root.setOnClickListener { _ -> callBack.invoke() }
        return CardsHeaderViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CardsHeaderViewHolder, position: Int) {
        holder.onBind(list[position]);
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_card_head
    }
}