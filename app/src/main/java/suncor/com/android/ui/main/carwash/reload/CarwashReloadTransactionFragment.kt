package suncor.com.android.ui.main.carwash.reload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.view.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import suncor.com.android.BuildConfig
import suncor.com.android.LocationLiveData
import suncor.com.android.R
import suncor.com.android.databinding.FragmentCarwashTransactionBinding
import suncor.com.android.databinding.FragmentFuelUpBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.googlepay.GooglePayUtils
import suncor.com.android.mfp.ErrorCodes
import suncor.com.android.model.Resource
import suncor.com.android.model.SettingsResponse
import suncor.com.android.model.SettingsResponse.Pap
import suncor.com.android.model.pap.ActiveSession
import suncor.com.android.model.pap.P97StoreDetailsResponse
import suncor.com.android.model.pap.PayResponse
import suncor.com.android.model.payments.PaymentDetail
import suncor.com.android.ui.common.Alerts
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.ui.main.pap.fuelup.*
import suncor.com.android.ui.main.pap.selectpump.SelectPumpAdapter
import suncor.com.android.ui.main.pap.selectpump.SelectPumpHelpDialogFragment
import suncor.com.android.ui.main.pap.selectpump.SelectPumpViewModel
import suncor.com.android.ui.main.wallet.payments.list.PaymentListItem
import suncor.com.android.utilities.AnalyticsUtils
import suncor.com.android.utilities.FingerprintManager
import suncor.com.android.utilities.Timber
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.annotation.Nullable
import javax.inject.Inject


class CarwashReloadTransactionFragment : MainActivityFragment() {

    // Arbitrarily-picked constant integer you define to track a request for payment data activity.
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991
    private val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    private var binding: FragmentCarwashTransactionBinding? = null
    private var viewModel: CarwashReloadTransactionViewModel? = null
    private val isLoading = ObservableBoolean(false)
    private var lastTransactionFuelUpLimit: Double? = null
    var mPapData: Pap? = null
    var paymentDropDownAdapter: PaymentDropDownAdapter? = null

    private var pumpNumber: String? = null
    private var storeId: String? = null
    private var preAuth: String? = null
    private var userPaymentId: String? = null

    // A client for interacting with the Google Pay API.
    private var paymentsClient: PaymentsClient? = null

    @Inject
    var fingerPrintManager: FingerprintManager? = null

    @Inject
    var viewModelFactory: ViewModelFactory? = null

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[CarwashReloadTransactionViewModel::class.java]
        paymentsClient = GooglePayUtils.createPaymentsClient(context)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCarwashTransactionBinding.inflate(inflater!!, container, false)
        binding!!.lifecycleOwner = this
        Window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        binding!!.isLoading = isLoading
        binding!!.appBar.setNavigationOnClickListener { v -> goBack() }
        binding!!.preauthorizeButton.setOnClickListener { v -> handleConfirmAndAuthorizedClick() }
        binding!!.selectPumpLayout.appBar.visibility = View.GONE
        binding!!.selectPumpLayout.layout.visibility = View.GONE
        binding!!.pumpLayout.setOnClickListener { v ->
            AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.infoTab,
                    Pair(AnalyticsUtils.Param.infoText, getString(R.string.pump)))
            binding!!.selectPumpLayout.layout.visibility = if (binding!!.selectPumpLayout.layout.visibility === View.GONE) View.VISIBLE else View.GONE
        }
        paymentDropDownAdapter = PaymentDropDownAdapter(
                context,
                this
        )
        binding!!.paymentExpandable.setDropDownData(paymentDropDownAdapter)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storeId = FuelUpFragmentArgs.fromBundle(arguments).storeId
        if (pumpNumber == null) {
            pumpNumber = FuelUpFragmentArgs.fromBundle(arguments).pumpNumber
        }
        binding!!.pumpNumberText.text = pumpNumber
        binding!!.fuelUpLimit.initListener(this)
        binding!!.paymentExpandable.initListener(this)
        binding!!.selectPumpLayout.helpButton.setOnClickListener { v -> showHelp() }
        selectPumpViewModel!!.getStoreDetails(storeId).observe(viewLifecycleOwner, { storeDetailsResponseResource: Resource<P97StoreDetailsResponse?> ->
            if (storeDetailsResponseResource.status == Resource.Status.SUCCESS && storeDetailsResponseResource.data != null) {
                val storeDetailsResponse = storeDetailsResponseResource.data
                val pumpNumbers = ArrayList<String>()
                var index = 0
                for (pumpStatus in storeDetailsResponse.fuelService.pumpStatuses) {
                    if (pumpStatus.status == "Available") {
                        pumpNumbers.add(pumpStatus.pumpNumber.toString())
                        if (pumpStatus.pumpNumber.toString() == pumpNumber) {
                            adapter!!.setSelectedPos(index)
                        }
                        index++
                    }
                }
                adapter!!.setPumpNumbers(pumpNumbers)
            }
        })
        viewModel!!.activeSession.observe(viewLifecycleOwner, { result: Resource<ActiveSession?> ->
            if (result.status == Resource.Status.LOADING) {
                AnalyticsUtils.setCurrentScreenName(activity, "pay-at-pump-preauthorize-loading")
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(context, "Pump PreAuthorized").show()
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                lastTransactionFuelUpLimit = result.data.lastFuelUpAmount
                initializeFuelUpLimit()
            }
        })
        viewModel!!.getSettingResponse().observe(viewLifecycleOwner, { result: Resource<SettingsResponse?> ->
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                Alerts.prepareGeneralErrorDialog(context, "Pump PreAuthorized").show()
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                mPapData = result.data.settings.pap
                mPapData.getPreAuthLimits()[(mPapData.getPreAuthLimits().size + 1).toString()] = getString(R.string.other_amount)
                initializeFuelUpLimit()
            }
        })
        viewModel!!.getPayments(context).observe(viewLifecycleOwner, { result: Resource<ArrayList<PaymentListItem>?> ->
            if (result.status == Resource.Status.LOADING) {
                //hideKeyBoard();
            } else if (result.status == Resource.Status.ERROR) {
                var payments: List<PaymentListItem>? = result.data
                payments = ArrayList()
                paymentDropDownAdapter!!.addPayments(payments)
                if (userPaymentId == null && payments.size > 0) userPaymentId = payments[0].paymentDetail.id
                paymentDropDownAdapter!!.setSelectedPos(userPaymentId)
                checkForGooglePayOptions()
                Alerts.prepareGeneralErrorDialog(context, "Pump PreAuthorized").show()
            } else if (result.status == Resource.Status.SUCCESS && result.data != null) {
                val payments: List<PaymentListItem>? = result.data
                paymentDropDownAdapter!!.addPayments(payments)
                if (userPaymentId == null && payments!!.size > 0) userPaymentId = payments[0].paymentDetail.id
                paymentDropDownAdapter!!.setSelectedPos(userPaymentId)
                checkForGooglePayOptions()
            }
        })
        binding!!.termsAgreement.setOnClickListener { v ->
            AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.intersite,
                    Pair(AnalyticsUtils.Param.intersiteURL, getString(R.string.privacy_policy_url)))
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_policy_url)))
            startActivity(browserIntent)
        }
        val navController = NavHostFragment.findNavController(this)
        // We use a String here, but any type that can be put in a Bundle is supported
        val liveData = navController.currentBackStackEntry
                .getSavedStateHandle()
                .getLiveData<PaymentDetail>("tempPayment")
        liveData.observe(activity!!, { paymentDetail: PaymentDetail ->
            // Do something with the result.
            paymentDropDownAdapter!!.addPayment(PaymentListItem(context, paymentDetail), true)
            userPaymentId = paymentDetail.id
        })

        // We use a String here, but any type that can be put in a Bundle is supported
        val selectedPaymentLiveData = navController.currentBackStackEntry
                .getSavedStateHandle()
                .getLiveData<String>("selectedPayment")
        selectedPaymentLiveData.observe(activity!!, { userPaymentSourceId: String? ->
            userPaymentId = userPaymentSourceId

            // Do something with the result.
            paymentDropDownAdapter!!.setSelectedPos(userPaymentSourceId)
        })
    }


    private fun initializeFuelUpLimit() {
        if (Objects.nonNull(mPapData) && Objects.nonNull(mPapData!!.preAuthLimits)) {
            // binding.totalAmount.setText(String.format("$%s", mPapData.getPreAuthLimits().get("1")));
            val adapter = FuelLimitDropDownAdapter(
                    context,
                    mPapData!!.preAuthLimits,
                    this,
                    mPapData!!.otherAmountHighLimit,
                    mPapData!!.otherAmountLowLimit
            )
            if (preAuth != null) {
                try {
                    adapter.setSelectedPosfromValue(formatter.parse(preAuth).toDouble())
                } catch (ex: ParseException) {
                    Timber.e(ex.message!!)
                }
            }
            adapter.findLastFuelUpTransaction(lastTransactionFuelUpLimit)
            binding!!.fuelUpLimit.setDropDownData(adapter)
        }
    }

    private fun showHelp() {
        val fragment: DialogFragment = SelectPumpHelpDialogFragment()
        fragment.show(fragmentManager!!, "dialog")
    }

    fun selectPumpNumber(pumpNumber: String?) {
        this.pumpNumber = pumpNumber
        binding!!.pumpNumberText.text = pumpNumber
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.formStep, Pair(AnalyticsUtils.Param.formName, "Pump PreAuthorized"),
                Pair(AnalyticsUtils.Param.formSelection, pumpNumber))
        Handler().postDelayed({ binding!!.pumpLayout.callOnClick() }, 400)
    }

    fun onExpandCollapseListener(isExpand: Boolean, cardTitle: String) {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.infoTab,
                Pair(AnalyticsUtils.Param.infoText, cardTitle))
    }

    private fun goBack() {
        Navigation.findNavController(view).popBackStack()
    }

    fun onPreAuthChanged(value: String?) {
        preAuth = value
        binding!!.totalAmount.text = value
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.formStep, Pair(AnalyticsUtils.Param.formName, "Pump PreAuthorized"),
                Pair(AnalyticsUtils.Param.formSelection, value))
    }

    fun onPaymentChanged(userPaymentId: String) {
        this.userPaymentId = userPaymentId
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.formStep, Pair(AnalyticsUtils.Param.formName, "Pump PreAuthorized"),
                Pair(AnalyticsUtils.Param.formSelection, if (userPaymentId == PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY) PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY else "credit_card"))
    }


    private fun checkForGooglePayOptions() {
        val request = viewModel!!.IsReadyToPayRequestForGooglePay()
        if (Objects.isNull(request)) {
            return
        }
        val task = paymentsClient!!.isReadyToPay(request)
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        task.addOnCompleteListener(requireActivity()) { data: Task<Boolean?> ->
            if (data.isSuccessful && data.result != null && data.result!!) {
                paymentDropDownAdapter!!.addGooglePayOption(userPaymentId)
            } else {
                Log.w("isReadyToPay failed", task.exception)
            }
        }
    }

    private fun handleConfirmAndAuthorizedClick() {
        AnalyticsUtils.logEvent(context, AnalyticsUtils.Event.buttonTap, Pair(AnalyticsUtils.Param.buttonText, getString(R.string.confirm_and_authorized).toLowerCase()))
        if (userPaymentId == null) {
            // select payment type error
            return
        }
        if (userPaymentId == PaymentDropDownAdapter.PAYMENT_TYPE_GOOGLE_PAY) {
            verifyFingerPrints()
        } else {
            try {
                val preAuthPrices = formatter.parse(preAuth).toDouble()
            } catch (ex: ParseException) {
                Timber.e(ex.message!!)
            }
        }
    }


    fun requestGooglePaymentTransaction() {
        try {
            val preAuthPrices = formatter.parse(preAuth).toDouble()
            val request = viewModel!!.createGooglePayInitiationRequest(preAuthPrices,
                    BuildConfig.GOOGLE_PAY_MERCHANT_GATEWAY, mPapData!!.p97TenantID)

            // Since loadPaymentData may show the UI asking the user to select a payment method, we use
            // AutoResolveHelper to wait for the user interacting with it. Once completed,
            // onActivityResult will be called with the result.
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        paymentsClient!!.loadPaymentData(request),
                        requireActivity(), LOAD_PAYMENT_DATA_REQUEST_CODE)
            }
        } catch (ex: ParseException) {
            Timber.e(ex.message!!)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val paymentData = PaymentData.getFromIntent(data!!)
                    val paymentToken = viewModel!!.handlePaymentSuccess(paymentData)
                    try {
                        requestPayByGooglePay(paymentToken)
                    } catch (ex: ParseException) {
                        Timber.e(ex.message!!)
                    }
                }
                Activity.RESULT_CANCELED -> {
                }                      // The user cancelled the payment attempt

                AutoResolveHelper.RESULT_ERROR -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                  }
            }
        }
    }

    private fun verifyFingerPrints() {
        if (fingerPrintManager!!.isFingerPrintExistAndEnrolled) {
            val promptInfo = PromptInfo.Builder()
                    .setTitle(getString(R.string.payment))
                    .setSubtitle(getString(R.string.google_pay))
                    .setDescription(resources.getString(R.string.login_fingerprint_alert_desc))
                    .setNegativeButtonText(resources.getString(R.string.login_fingerprint_alert_negative_button)).build()
            val executor: Executor = Executors.newSingleThreadExecutor()
            val biometricPrompt = BiometricPrompt(context, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, @NonNull errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                }

                override fun onAuthenticationSucceeded(@NonNull result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    requestGooglePaymentTransaction()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }
            })
            biometricPrompt.authenticate(promptInfo)
        } else {
            requestGooglePaymentTransaction()
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @Throws(ParseException::class)
    private fun requestPayByGooglePay(paymentToken: String) {
        val preAuthPrices = formatter.parse(preAuth).toDouble()

    }



}