package suncor.com.android.ui.main.profile.account

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.DeleteAccountCheckboxItemBinding
import suncor.com.android.utilities.Timber


class AcknowledgementListAdapter(
    val context: Context,
    private val ackFeedbackList: ArrayList<AckFeedbackList>,
    var validateDataListner: ValidateDataListner
) :
    RecyclerView.Adapter<AcknowledgementListAdapter.AcknowledgementHolder>() {

    companion object {
        lateinit var mvalidateDataListner: ValidateDataListner
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcknowledgementHolder {
        val binding = DeleteAccountCheckboxItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AcknowledgementHolder(binding)
    }

    override fun onBindViewHolder(holder: AcknowledgementHolder, position: Int) {
        mvalidateDataListner = validateDataListner
        holder.binding.checkAckFeedback.text = ackFeedbackList[holder.adapterPosition].name

        holder.binding.checkAckFeedback.setOnClickListener { v ->
            holder.binding.checkAckFeedback.buttonDrawable =
                context.getDrawable(R.drawable.custom_checkbox)
            ackFeedbackList[holder.bindingAdapterPosition].setError = false
            if (holder.binding.checkAckFeedback.isChecked) {
                ackFeedbackList[holder.bindingAdapterPosition].checked = true
                Timber.d(holder.bindingAdapterPosition.toString() + "--" + ackFeedbackList[holder.bindingAdapterPosition].name + "--" + ackFeedbackList[holder.bindingAdapterPosition].checked)
                validateDataListner.setAckListChecks(
                    ackFeedbackList.size,
                    ackFeedbackList
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

        if (position == ackFeedbackList.size - 1) {
            holder.binding.bottomDivider.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return ackFeedbackList.size
    }

    inner class AcknowledgementHolder(var binding: DeleteAccountCheckboxItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    interface ValidateDataListner {
        fun setAckListChecks(size: Int, ackFeedbackList: ArrayList<AckFeedbackList>)
    }
}

