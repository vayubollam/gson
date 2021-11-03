package suncor.com.android.ui.main.carwash.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import suncor.com.android.databinding.FragmentCarwashTransactionBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject


class CarwahTransactionReceiptFragment : MainActivityFragment() {


    private lateinit var binding: FragmentCarwashTransactionBinding
    private val isLoading = ObservableBoolean(false)

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarwashTransactionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.isLoading = isLoading

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}