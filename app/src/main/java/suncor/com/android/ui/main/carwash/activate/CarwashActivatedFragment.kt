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
import suncor.com.android.model.carwash.ActivateCarwashResponse
import suncor.com.android.model.carwash.CarwashConfigurationType
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.common.cards.CardFormatUtils
import suncor.com.android.ui.main.carwash.CarWashActivationSecurityFragmentArgs
import suncor.com.android.ui.main.common.MainActivityFragment
import javax.inject.Inject

class CarwashActivatedFragment: MainActivityFragment(), OnBackPressedListener {
    private lateinit var binding: FragmentCarwashActivatedBinding
    private var carwashResponse: ActivateCarwashResponse? = null
    private val isLoading = ObservableBoolean(false)

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarwashActivatedBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.isLoading = isLoading

        carwashResponse = CarwashActivatedFragmentArgs.fromBundle(requireArguments()).carwashResponse

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.transactionGreetings.text = String.format(getString(R.string.thank_you_carwash), sessionManager.profile.firstName)

        binding.remainingTextView.text = when (carwashResponse?.configurationType) {
            CarwashConfigurationType.TBO ->  {
                if (carwashResponse?.getDaysLeft() ?: 0 == 0) {
                    binding.remainingReloadTextView.visibility = View.VISIBLE
                    binding.remainingTextView.visibility = View.GONE
                    getString(R.string.carwash_zero_days)
                } else
                    getString(
                        R.string.carwash_remaining_copy,
                        context?.resources?.getQuantityString(
                            R.plurals.cards_days_balance,
                            carwashResponse?.getDaysLeft() ?: 0,
                            CardFormatUtils.formatBalance(carwashResponse?.getDaysLeft() ?: 0)
                        )
                    )
            }

            CarwashConfigurationType.UBO ->  {
                if (carwashResponse?.estimatedWashesRemaining ?: 0 == 0) {
                    binding.remainingReloadDaysTextView.visibility = View.VISIBLE
                    binding.remainingTextView.visibility = View.GONE
                    getString(R.string.carwash_zero_washes)
                } else
                    getString(
                        R.string.carwash_remaining_copy,
                        context?.resources?.getQuantityString(
                            R.plurals.cards_washes_balance,
                            carwashResponse?.estimatedWashesRemaining ?: 0,
                            CardFormatUtils.formatBalance(carwashResponse?.estimatedWashesRemaining ?: 0)
                        )
                    )
            }

            else ->  {
                if (carwashResponse?.estimatedWashesRemaining ?: 0 == 0 || carwashResponse?.getDaysLeft() ?: 0 == 0) {
                    binding.remainingReloadTextView.visibility = View.VISIBLE
                    binding.remainingTextView.visibility = View.GONE
                    getString(R.string.carwash_zero_washes)
                } else
                    getString(
                        R.string.carwash_remaining_copy,
                        context?.resources?.getQuantityString(
                            R.plurals.cards_washes_balance,
                            carwashResponse?.estimatedWashesRemaining ?: 0,
                            CardFormatUtils.formatBalance(carwashResponse?.estimatedWashesRemaining ?: 0)
                        )
                    )
            }
        }

        binding.buttonDone.setOnClickListener {
            goBack()
        }
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        Navigation.findNavController(requireView()).popBackStack(R.id.cardsDetailsFragment, false)
    }
}