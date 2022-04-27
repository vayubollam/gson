package suncor.com.android.ui.main.profile.account

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.DeleteAccountCheckboxItemBinding
import suncor.com.android.utilities.Timber


class BeforeLeavingListAdapter(val context: Context,
                               private val ackFeedbackList: ArrayList<AckFeedbackList>,
                               private var validateDataListner: ValidateDataListner
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
        lateinit var mValidateDataListner: ValidateDataListner
    }

    override fun onBindViewHolder(holder: BeforeLeavingListHolder, position: Int) {
        mValidateDataListner = validateDataListner

        holder.binding.checkAckFeedback.text = ackFeedbackList[holder.bindingAdapterPosition].name

        holder.binding.checkAckFeedback.setOnClickListener { v ->
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.custom_checkbox)
            ackFeedbackList[holder.bindingAdapterPosition].setError = false
            if (holder.binding.checkAckFeedback.isChecked) {
                ackFeedbackList[holder.bindingAdapterPosition].checked = true
                Timber.d(holder.bindingAdapterPosition.toString() + "--" + ackFeedbackList[holder.bindingAdapterPosition].name + "--" + ackFeedbackList[holder.bindingAdapterPosition].checked)
                validateDataListner.setFeedbackChecks(
                    ackFeedbackList.size,ackFeedbackList
                )
            } else {
                ackFeedbackList[holder.bindingAdapterPosition].checked = false
                Timber.d(holder.bindingAdapterPosition.toString() + "--" + ackFeedbackList[holder.bindingAdapterPosition].name + "--" + ackFeedbackList[holder.bindingAdapterPosition].checked)

            }
        }

        if (ackFeedbackList[holder.bindingAdapterPosition].setError) {
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.checkbox_error)
        } else {
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.custom_checkbox)
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

    interface ValidateDataListner {
        fun setFeedbackChecks(size: Int, ackFeedbackList: ArrayList<AckFeedbackList>)
    }
}
