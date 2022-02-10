package suncor.com.android.ui.main.profile.account

import android.content.DialogInterface
import android.os.Bundle
import android.util.Pair
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.room.util.StringUtil
import suncor.com.android.R
import suncor.com.android.databinding.FragmentAccountDetailsBinding
import suncor.com.android.mfp.SessionManager
import suncor.com.android.ui.common.Event
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.common.SuncorToast
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.profile.ProfileSharedViewModel
import suncor.com.android.ui.main.profile.address.AddressFragment
import suncor.com.android.ui.main.profile.info.PersonalInfoFragment
import suncor.com.android.utilities.AnalyticsUtils
import suncor.com.android.utilities.Constants
import javax.inject.Inject

class AccountDetailsFragment : MainActivityFragment(), OnBackPressedListener {

    private lateinit var binding: FragmentAccountDetailsBinding
    private lateinit var profileSharedViewModel: ProfileSharedViewModel
    private var appBarElevation = 0f

    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, resources.displayMetrics)
        profileSharedViewModel = ViewModelProviders.of(requireActivity())[ProfileSharedViewModel::class.java]

        profileSharedViewModel.alertObservable.observe(requireActivity()) { event: Event<ProfileSharedViewModel.Alert?> ->
            val alert = event.contentIfNotHandled
            if (alert != null) {
                val dialog = AlertDialog.Builder(requireActivity())
                if (alert.title != -1) {
                    dialog.setTitle(alert.title)
                    AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.error, Pair(
                        AnalyticsUtils.Param.errorMessage, getString(alert.title)),
                        Pair(AnalyticsUtils.Param.FORMNAME, Constants.MY_PETRO_POINTS_ACCOUNT_NAVIGATION_LIST))
                }
                if (alert.message != -1) {
                    dialog.setMessage(alert.message)
                }
                if (alert.positiveButton != -1) {
                    dialog.setPositiveButton(alert.positiveButton) { i: DialogInterface, w: Int ->
                        if (alert.positiveButtonClick != null) {
                            alert.positiveButtonClick.run()
                        }
                        i.dismiss()
                    }
                }
                if (alert.negativeButton != -1) { dialog.setNegativeButton(alert.negativeButton) { i: DialogInterface, w: Int ->
                        if (alert.negativeButtonClick != null) {
                            alert.negativeButtonClick.run()
                        }
                        i.dismiss()
                    }
                }
                dialog.show()
            }
        }

        profileSharedViewModel.toastObservable.observe(this
        ) { event: Event<Int?> ->
            val message = event.contentIfNotHandled
            if (message != null) {
                SuncorToast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sessionManager?.profile == null) {
            return
        }
        binding.emailOutput.text = sessionManager?.profile?.email
        accountDeleteText()

        binding.personalInformationsButton.setOnClickListener { v -> AnalyticsUtils.logEvent(context,
                AnalyticsUtils.Event.FORMSTART, Pair(AnalyticsUtils.Param.FORMNAME, Constants.UPDATE_PERSONAL_INFORMATION)
            )
            if ( profileSharedViewModel.ecryptedSecurityAnswer != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_account_details_to_personalInfoFragment)
            } else {
                val action: AccountDetailsFragmentDirections.ActionAccountDetailsToSecurityQuestionValidationFragment2 =
                    AccountDetailsFragmentDirections.actionAccountDetailsToSecurityQuestionValidationFragment2(PersonalInfoFragment.PERSONAL_INFO_FRAGMENT)
                Navigation.findNavController(requireView()).navigate(action)
            }
        }

        binding.preferencesButton.setOnClickListener { v ->
            AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.FORMSTART, Pair(AnalyticsUtils.Param.FORMNAME, Constants.CHANGES_PREFERENCES))
            Navigation.findNavController(requireView())
                .navigate(R.id.action_account_details_to_preferencesFragment)
        }

        binding.addressButton.setOnClickListener { v ->
            AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.FORMSTART, Pair(AnalyticsUtils.Param.FORMNAME, Constants.UPDATE_ADDRESS))
            if (profileSharedViewModel.ecryptedSecurityAnswer != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_account_details_to_addressFragment)
            } else {
                val action: AccountDetailsFragmentDirections.ActionAccountDetailsToSecurityQuestionValidationFragment2 =
                    AccountDetailsFragmentDirections.actionAccountDetailsToSecurityQuestionValidationFragment2(AddressFragment.ADDRESS_FRAGMENT)
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
        binding.deleteAccountButton.setOnClickListener { v ->
            AnalyticsUtils.logEvent(context,
                AnalyticsUtils.Event.FORMSTART, Pair(AnalyticsUtils.Param.FORMNAME, Constants.DELETE_ACCOUNT)
            )
            val action: AccountDetailsFragmentDirections.ActionAccountDetailsToSecurityQuestionValidationFragment2 =
                    AccountDetailsFragmentDirections.actionAccountDetailsToSecurityQuestionValidationFragment2(DeleteAccountFragment.DELETE_ACCOUNT_FRAGMENT)
                Navigation.findNavController(requireView()).navigate(action)
        }
        binding.appBar.setNavigationOnClickListener { v -> goBack() }
    }
    private fun accountDeleteText(){
        if(sessionManager.profile != null && sessionManager.profile.accountDeleteDateTime != null){
            var deleteAccountText = String.format(getString(R.string.account_details_delete_alert_title), sessionManager.profile.accountDeleteDaysLeft)
            binding.deleteAccountAlertText.text = deleteAccountText
            binding.deleteAccountAlertText.visibility = View.VISIBLE
        }
    }


    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        profileSharedViewModel.ecryptedSecurityAnswer = null
        Navigation.findNavController(requireView()).popBackStack()
    }

}