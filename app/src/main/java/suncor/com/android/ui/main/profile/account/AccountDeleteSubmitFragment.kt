package suncor.com.android.ui.main.profile.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import suncor.com.android.R
import suncor.com.android.databinding.FragmentAccountDeleteSubmitBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.common.cards.CardFormatUtils
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.profile.ProfileSharedViewModel
import javax.inject.Inject

class AccountDeleteSubmitFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentAccountDeleteSubmitBinding
    private lateinit var profileSharedViewModel: ProfileSharedViewModel

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileSharedViewModel =
                ViewModelProviders.of(requireActivity())[ProfileSharedViewModel::class.java]
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDeleteSubmitBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataMapFromProfile()
        binding.backToHomeButton.setOnClickListener { view-> goBack() }
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    private fun dataMapFromProfile(){
        binding.deleteAccountUnusedPointsTextview.text = String.format(getString(R.string.account_deletion_account_unused_points),
            CardFormatUtils.formatBalance(sessionManager!!.profile.pointsBalance))
    }

}