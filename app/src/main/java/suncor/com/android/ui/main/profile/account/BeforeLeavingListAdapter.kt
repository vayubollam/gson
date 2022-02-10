package suncor.com.android.ui.main.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.databinding.DeleteAccountCheckboxItemBinding
import suncor.com.android.ui.main.AcknowledgementList


class BeforeLeavingListAdapter(private val acknowledgementList: ArrayList<AcknowledgementList>) : RecyclerView.Adapter<BeforeLeavingListAdapter.BeforeLeavingListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeforeLeavingListHolder {
        val binding = DeleteAccountCheckboxItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeforeLeavingListHolder(binding)
    }

    override fun onBindViewHolder(holder: BeforeLeavingListHolder, position: Int) {
        holder.binding.checkProvince.text = acknowledgementList[holder.adapterPosition].name

        holder.binding.checkProvince.setOnClickListener { v ->

        }
        if (position == acknowledgementList.size - 1) {
            holder.binding.bottomDivider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return acknowledgementList.size
    }

    inner class BeforeLeavingListHolder(var binding: DeleteAccountCheckboxItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

}
