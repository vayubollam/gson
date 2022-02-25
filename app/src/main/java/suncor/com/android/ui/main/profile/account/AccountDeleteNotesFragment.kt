package suncor.com.android.ui.main.profile.account

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import suncor.com.android.R
import suncor.com.android.databinding.FragmentAccountDeleteNotesBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.account.Profile
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.AnalyticsUtils
import suncor.com.android.utilities.DateUtils
import javax.inject.Inject

class AccountDeleteNotesFragment : MainActivityFragment(), OnBackPressedListener {

    companion object {
        const val ACCOUNT_DELETE_NOTES_FRAGMENT = "account_delete_notes_fragment"
    }
    private lateinit var binding: FragmentAccountDeleteNotesBinding

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDeleteNotesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataMapFromProfile()
        binding.deleteAccountBodyCustomerService.setOnClickListener { v ->
            AnalyticsUtils.logEvent(
                context, AnalyticsUtils.Event.intersite,
                Pair(
                    AnalyticsUtils.Param.intersiteURL,
                    getString(R.string.customer_service_url)
                )
            )
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(getString(R.string.customer_service_url))
            )
            startActivity(browserIntent)
        }
        binding.appBar.setNavigationOnClickListener { v -> goBack() }
    }

    private fun dataMapFromProfile(){
        var profile: Profile = sessionManager!!.profile
        binding.deleteAccountTitle.text = String.format(getString(R.string.account_deletion_notes_title), profile.firstName)
        binding.deleteAccountBodyDateRequested.text =  String.format(getString(R.string.account_deletion_requested), DateUtils.getFormattedDate(profile.accountDeleteDateTime), profile.accountDeleteDaysLeft)
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        Navigation.findNavController(requireView()).popBackStack()
    }

}