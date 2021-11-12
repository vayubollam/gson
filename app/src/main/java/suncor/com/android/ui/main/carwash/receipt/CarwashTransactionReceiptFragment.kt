package suncor.com.android.ui.main.carwash.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.navigation.Navigation
import suncor.com.android.R
import suncor.com.android.databinding.FragmentCarwashTransactionReceiptBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.carwash.reload.TransactionReceipt
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.DateUtils
import java.util.*
import javax.inject.Inject


class CarwashTransactionReceiptFragment : MainActivityFragment() {

    private lateinit var binding: FragmentCarwashTransactionReceiptBinding
    private val isLoading = ObservableBoolean(false)

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarwashTransactionReceiptBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.isLoading = isLoading
        setReceiptData()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonDone.setOnClickListener {
            Navigation.findNavController(it).popBackStack()
        }
    }

    fun setReceiptData(){
        val cardType = CarwashTransactionReceiptFragmentArgs.fromBundle(requireArguments()).cardType
        val totalAmount = CarwashTransactionReceiptFragmentArgs.fromBundle(requireArguments()).totalAmount
        val name = CarwashTransactionReceiptFragmentArgs.fromBundle(requireArguments()).userName
        var transactionReceipt = TransactionReceipt("600", "600", "11,000", DateUtils.getFromattedDate(Calendar.getInstance().timeInMillis, "MMM dd, yyyy"), "Visa****7676", totalAmount)
        binding.transactionGreetings.text = String.format(getString(R.string.thank_you), name);
        binding.transaction = transactionReceipt
        binding.receiptTvDescription.text = if (cardType == "SP") getString(R.string.sp_washes_added_msg) else getString(R.string.wng_washes_added_msg)
        binding.cardImage.setImageResource(if (cardType == "SP") R.drawable.seasons_pass_card else  R.drawable.wag_card)
    }
}