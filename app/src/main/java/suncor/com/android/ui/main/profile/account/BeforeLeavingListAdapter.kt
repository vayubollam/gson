package suncor.com.android.ui.main.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.DeleteAccountCheckboxItemBinding
import suncor.com.android.ui.main.AckFeedbackList
import suncor.com.android.utilities.Timber


class BeforeLeavingListAdapter(val context: Context,
                               private val ackFeedbackList: ArrayList<AckFeedbackList>,
                               var validateDataListner: checkReasonListner
) :
    RecyclerView.Adapter<BeforeLeavingListAdapter.BeforeLeavingListHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeforeLeavingListHolder {
        val binding = DeleteAccountCheckboxItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeforeLeavingListHolder(binding)
    }

    companion object {
        lateinit var mValidateDataListner: checkReasonListner
    }

    override fun onBindViewHolder(holder: BeforeLeavingListHolder, position: Int) {
        mValidateDataListner = validateDataListner

        holder.binding.checkAckFeedback.text = ackFeedbackList[holder.adapterPosition].name

        holder.binding.checkAckFeedback.setOnClickListener { v ->
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.custom_checkbox)
            ackFeedbackList[holder.adapterPosition].setError = false
            if (holder.binding.checkAckFeedback.isChecked) {
                ackFeedbackList[holder.adapterPosition].checked = true
                Timber.d(holder.adapterPosition.toString() + "--" + ackFeedbackList[holder.adapterPosition].name + "--" + ackFeedbackList[holder.adapterPosition].checked)
                validateDataListner.setFeebackChecks(
                    ackFeedbackList.size,ackFeedbackList
                )
            } else {
                ackFeedbackList[holder.adapterPosition].checked = false
                Timber.d(holder.adapterPosition.toString() + "--" + ackFeedbackList[holder.adapterPosition].name + "--" + ackFeedbackList[holder.adapterPosition].checked)

            }
        }
        //   ackFeedbackList[holder.adapterPosition]==ackFeedbackList[ackFeedbackList.size-1]&&

        if (ackFeedbackList[holder.adapterPosition].setError==true) {
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.checkbox_error)
        } else {
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.custom_checkbox)
        }
        if (position == ackFeedbackList.size - 1) {
            holder.binding.bottomDivider.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return ackFeedbackList.size
    }

    inner class BeforeLeavingListHolder(var binding: DeleteAccountCheckboxItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    interface checkReasonListner {
        fun setFeebackChecks(size: Int, ackFeedbackList: ArrayList<AckFeedbackList>)
    }
}
