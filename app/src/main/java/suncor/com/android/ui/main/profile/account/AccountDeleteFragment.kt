package suncor.com.android.ui.main.profile.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import suncor.com.android.R
import suncor.com.android.databinding.FragmentDeleteAccountBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.model.Resource
import suncor.com.android.model.account.DeleteAccountRequest
import suncor.com.android.model.account.Profile
import suncor.com.android.ui.common.Alerts
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.main.AcknowledgementList
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.profile.AcknowledgementListAdapter
import suncor.com.android.ui.main.profile.BeforeLeavingListAdapter
import javax.inject.Inject

class AccountDeleteFragment : MainActivityFragment(), OnBackPressedListener {

    companion object {
        const val DELETE_ACCOUNT_FRAGMENT = "delete_account_fragment"
    }
    private lateinit var binding: FragmentDeleteAccountBinding
    private val acknowledgementList = ArrayList<AcknowledgementList>()
    private val beforeLeavingList = ArrayList<AcknowledgementList>()
    private var viewModel: AccountDeleteViewModel? = null

    @Inject lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory)[AccountDeleteViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.startDeletionButton.setOnClickListener { v ->
            // navigation for account submit fragment
        }
        binding.appBar.setNavigationOnClickListener { v -> goBack() }

        binding.appBar.setTitle(resources.getString(R.string.account_details_delete_button))
        dataMapFromProfile()
        setAckRecycler()
        setBeforeLeavingRecycler()
        binding.startDeletionButton.setOnClickListener { v -> deleteAccountApiCall() }
    }

    /**
     * deleteion form Data mapping from profile
     */
    private fun dataMapFromProfile(){
        var profile: Profile = viewModel!!.getProfile()
        binding.firstNameInput.text = profile.firstName
        binding.lastNameInput.text = profile.lastName
        binding.phoneNoInput.text = profile.phone
        binding.addressInput.text = profile.streetAddress
        binding.emailInput.text = profile.email
        binding.petroPointsInput.text = profile.petroPointsNumber
    }

    /**
     * delete account api call
     */
    private fun deleteAccountApiCall(){
        var profile: Profile = viewModel!!.getProfile()
        var deleteAccountRequest = DeleteAccountRequest(profile.petroPointsNumber,
            profile.firstName, profile.lastName, profile.email, profile.streetAddress, profile.city,
           profile.province, profile.postalCode, binding.phoneNoInput.text.trim().toString(), true,false, false,"" )
        viewModel!!.deleteApi(deleteAccountRequest).observe(viewLifecycleOwner) { result ->
            if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(context, "").show()
            } else if (result.status == Resource.Status.SUCCESS) {
                val action: NavDirections = AccountDeleteFragmentDirections.actionAccountDeleteToAccountDeleteSubmitFragment()
                Navigation.findNavController(requireView()).navigate(action)
                goBack()
            }
        }
    }

    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        Navigation.findNavController(requireView()).popBackStack()
    }

    private fun setAckRecycler() {
        val ackArray = resources.getStringArray(R.array.acknowledgment_list)
        for (ackName in ackArray) {
            acknowledgementList.add(AcknowledgementList(ackName))
        }

        val ackAdapter = AcknowledgementListAdapter(acknowledgementList)
        binding.acknoledgementRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.acknoledgementRecyclerview.adapter = ackAdapter

    }

    private fun setBeforeLeavingRecycler() {
        val beforeLeavingArray = resources.getStringArray(R.array.before_leaving_list)
        for (blName in beforeLeavingArray) {
            beforeLeavingList.add(AcknowledgementList(blName))
        }
        val beforeLeavingListAdapter = BeforeLeavingListAdapter(beforeLeavingList)
        binding.leaveMessageRecyclerview.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.leaveMessageRecyclerview.adapter = beforeLeavingListAdapter
    }


}