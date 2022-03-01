package suncor.com.android.ui.main.wallet.cards.list

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R

class CardItemDecorator2(private val mSpace: Int, private val dividerSpace: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
       val position = parent.getChildAdapterPosition(view)
       val viewType = parent.adapter?.getItemViewType(position)
        when {
            position == 0 -> {
                outRect.bottom = dividerSpace
            }
            viewType == R.layout.item_card_list_title -> {
                outRect.top = dividerSpace
                outRect.bottom = dividerSpace
            }
            else -> {
                outRect.top = mSpace
            }
        }
    }
}
