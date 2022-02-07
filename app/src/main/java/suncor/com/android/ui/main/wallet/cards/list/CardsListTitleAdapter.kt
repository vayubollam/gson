package suncor.com.android.ui.main.wallet.cards.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.ItemCardListTitleBinding

class CardsListTitleAdapter : RecyclerView.Adapter<CardsListTitleAdapter.CardsListTitleViewHolder>() {

    var titleList = listOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsListTitleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = DataBindingUtil.inflate<ItemCardListTitleBinding>(layoutInflater, R.layout.item_card_list_title, parent, false)
        return CardsListTitleViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: CardsListTitleViewHolder, position: Int) {
        holder.bind(titleList[position])
    }

    override fun getItemCount(): Int = titleList.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_card_list_title
    }

    inner class CardsListTitleViewHolder(private val viewBinding: ItemCardListTitleBinding) : RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(title: String) {
            viewBinding.title = title
            viewBinding.executePendingBindings()
        }
    }
}