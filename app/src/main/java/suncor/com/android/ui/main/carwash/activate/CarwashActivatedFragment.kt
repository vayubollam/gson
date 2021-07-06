package suncor.com.android.ui.main.carwash.activate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.navigation.Navigation
import suncor.com.android.R
import suncor.com.android.databinding.FragmentCarwashActivatedBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject

class CarwashActivatedFragment: MainActivityFragment(), OnBackPressedListener {
    private lateinit var binding: FragmentCarwashActivatedBinding
    private val isLoading = ObservableBoolean(false)

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarwashActivatedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.isLoading = isLoading
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transactionGreetings.text = String.format(getString(R.string.thank_you), sessionManager.profile.firstName)
        binding.buttonDone.setOnClickListener {
            goBack()
        }
        binding.buttonClose.setOnClickListener {
            goBack()
        }
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        Navigation.findNavController(requireView()).popBackStack(R.id.carWashCardFragment, false)
    }
}