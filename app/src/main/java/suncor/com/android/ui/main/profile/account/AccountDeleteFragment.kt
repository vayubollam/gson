package suncor.com.android.ui.main.profile.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.ObservableBoolean
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
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.SuncorPhoneNumberTextWatcher
import suncor.com.android.utilities.Timber
import javax.inject.Inject

class AccountDeleteFragment : MainActivityFragment(), OnBackPressedListener,
    AcknowledgementListAdapter.ValidateDataListner, BeforeLeavingListAdapter.ValidateDataListner {

    companion object {
        const val DELETE_ACCOUNT_FRAGMENT = "delete_account_fragment"
    }
    private lateinit var binding: FragmentDeleteAccountBinding
    private var viewModel: AccountDeleteViewModel? = null

    lateinit var beforeLeavingListAdapter: BeforeLeavingListAdapter
    lateinit var acknowledgementListAdapter: AcknowledgementListAdapter

    private val acknowledgementList = ArrayList<AckFeedbackList>()
    private val beforeLeavingList = ArrayList<AckFeedbackList>()
    private var isLoading = ObservableBoolean(false)


    @Inject lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory)[AccountDeleteViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDeleteAccountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.isLoading = isLoading
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.appBar.setNavigationOnClickListener { v -> goBack() }
        binding.appBar.setTitle(resources.getString(R.string.account_details_delete_button))
        dataMapFromProfile()
        setAckRecycler()
        setBeforeLeavingRecycler()
        binding.startDeletionButton.setOnClickListener { v -> deleteAccountApiCall() }
        binding.phoneNoInput.editText.addTextChangedListener(SuncorPhoneNumberTextWatcher())
        binding.phoneNoInput.editText.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.phoneNoInput.editText.setOnEditorActionListener { view, actionId, event ->
            if (actionId === EditorInfo.IME_ACTION_DONE) {
                hideKeyBoard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun hideKeyBoard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    /**
     * deletion form Data mapping from profile
     */
    private fun dataMapFromProfile(){
        var profile: Profile = viewModel!!.getProfile()
        binding.deleteAccountTitle.text = String.format(getString(R.string.account_deletion_title), profile.firstName)
        binding.firstNameInput.text = profile.firstName
        binding.lastNameInput.text = profile.lastName
        binding.phoneNoInput.text = profile.phone
        binding.addressInput.text = profile.formattedAddress
        binding.emailInput.text = profile.email
        binding.petroPointsInput.text = profile.petroPointsNumber
    }

    /**
     * delete account api call
     */
    private fun deleteAccountApiCall(){
        if (binding.phoneNoInput.text.isEmpty()) {
            binding.phoneNoInput.setError(getString(R.string.account_deletion_phone_field_required))
            binding.phoneNoInput.requestFocus()
            return
        }
        binding.phoneNoInput.setError(null)
        if(!viewModel!!.isPhoneNumberValid(binding.phoneNoInput.text.toString())){
            binding.phoneNoInput.setError(R.string.profile_personnal_informations_phone_field_invalid_format)
            binding.phoneNoInput.requestFocus()
            return
        }
        binding.phoneNoInput.setError(null)

        if (!ackListValidate()) {
            Timber.d("All Acknowledgement points did not select.")
            return
        }
        if (!beforeListValidate()) {
            Timber.d("Before leaving options did not select reason.")
            return
        }
        binding.acknowledgementValidationTextview.visibility = View.GONE
        binding.differentReasonValidationTextview.visibility = View.GONE
        Timber.d("Validations Passed !")

        var profile: Profile = viewModel!!.getProfile()
        var deleteAccountRequest = DeleteAccountRequest(profile.petroPointsNumber,
            profile.firstName, profile.lastName, profile.email, profile.streetAddress, profile.city,
           profile.province, profile.postalCode, binding.phoneNoInput.text.trim().toString(), beforeLeavingList[0].checked, beforeLeavingList[1].checked,
            beforeLeavingList[2].checked, binding.differentReasonEditText.text.toString() )
        viewModel!!.deleteApi(deleteAccountRequest).observe(viewLifecycleOwner) { result ->
            when(result.status) {
                Resource.Status.LOADING -> {
                    isLoading.set(true)
                }
                Resource.Status.ERROR -> {
                    isLoading.set(false)
                    Alerts.prepareGeneralErrorDialog(context, "Delete Account").show()
                }
                Resource.Status.SUCCESS -> {
                    viewModel!!.refreshProfile()
                    val action: NavDirections = AccountDeleteFragmentDirections.actionAccountDeleteToAccountDeleteSubmitFragment()
                    Navigation.findNavController(requireView()).navigate(action)
                }
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
            acknowledgementList.add(AckFeedbackList(ackName, false, false))
        }

        acknowledgementListAdapter =
            AcknowledgementListAdapter(requireContext(), acknowledgementList, this)
        binding.acknoledgementRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.acknoledgementRecyclerview.adapter = acknowledgementListAdapter

    }

    private fun setBeforeLeavingRecycler() {
        val beforeLeavingArray = resources.getStringArray(R.array.before_leaving_list)
        for (blName in beforeLeavingArray) {
            beforeLeavingList.add(AckFeedbackList(blName, false, false))
        }
        beforeLeavingListAdapter =
            BeforeLeavingListAdapter(requireContext(), beforeLeavingList, this)
        binding.leaveMessageRecyclerview.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.leaveMessageRecyclerview.adapter = beforeLeavingListAdapter
    }

    override fun setAckListChecks(
        size: Int,
        ackFeedbackList: ArrayList<AckFeedbackList>
    ) {
        for (ackListItem in 0 until this.acknowledgementList.size) {
            for (checkAckListItem in 0 until ackFeedbackList.size) {
                if (this.acknowledgementList[ackListItem] == ackFeedbackList[checkAckListItem]) {
                    this.acknowledgementList[ackListItem].checked =
                        ackFeedbackList[checkAckListItem].checked
                }
            }
        }

        Timber.d("Check For size: $size")
    }

    private fun ackListValidate(): Boolean {
        var isValidate = false

        for (i in 0 until acknowledgementList.size) {
            when (acknowledgementList[i].checked) {
                true -> {
                    isValidate = true
                    binding.acknowledgementValidationTextview.visibility = View.GONE
                }
                false -> {
                    isValidate = false
                    binding.acknowledgementValidationTextview.visibility = View.VISIBLE
                    acknowledgementList[i].setError = true
                    acknowledgementListAdapter.notifyDataSetChanged()
                }

            }

        }
        return isValidate
    }

    private fun beforeListValidate(): Boolean {
        var isValidate = false
        if (beforeLeavingList[3].checked && binding.differentReasonEditText.text?.isEmpty() == true) {
            binding.differentReasonValidationTextview.visibility = View.VISIBLE
            return isValidate
        }
        for (i in 0 until beforeLeavingList.size) {
            if (beforeLeavingList[i].checked) {
                isValidate = true
                break
            } else if(!isValidate){
                isValidate = false
                beforeLeavingList[i].setError = true
            }
        }
        beforeLeavingListAdapter.notifyDataSetChanged()
        return isValidate
    }

    override fun setFeedbackChecks(size: Int, ackFeedbackList: ArrayList<AckFeedbackList>) {

        for (ackListItem in 0 until this.beforeLeavingList.size) {
            for (checkAckListItem in 0 until ackFeedbackList.size) {
                if (this.beforeLeavingList[ackListItem] == ackFeedbackList[checkAckListItem]) {
                    this.beforeLeavingList[ackListItem].checked =
                        ackFeedbackList[checkAckListItem].checked
                }
            }
        }
        Timber.d("Check For size: $size")

    }

}