package suncor.com.android.ui.main.rewards

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.common.collect.Sets
import com.google.gson.Gson
import suncor.com.android.R
import suncor.com.android.databinding.FragmentRedeemReceiptBinding
import suncor.com.android.di.viewmodel.ViewModelFactory
import suncor.com.android.mfp.SessionManager
import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct
import suncor.com.android.model.redeem.response.OrderResponse
import suncor.com.android.model.redeem.response.Program
import suncor.com.android.ui.common.OnBackPressedListener
import suncor.com.android.ui.common.cards.CardFormatUtils
import suncor.com.android.ui.main.MainViewModel
import suncor.com.android.ui.main.common.MainActivityFragment
import suncor.com.android.utilities.DateUtils
import suncor.com.android.utilities.MerchantsUtil
import suncor.com.android.utilities.NavigationConsentAlerts
import suncor.com.android.utilities.Timber
import java.util.*
import javax.inject.Inject


class RedeemSummaryFragment : MainActivityFragment(), OnBackPressedListener {

    @Inject
    lateinit var factory: ViewModelFactory

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var sessionManager: SessionManager

    private var viewModel: MainViewModel? = null
    private lateinit var binding: FragmentRedeemReceiptBinding
    private var orderResponse: OrderResponse? = null
    private var isMerchant = false
    private var isLinkToAccount = false
    private var isDonate: Boolean = false
    private var product: PetroCanadaProduct? = null
    private var program: Program?= null
    private var donationPoints: Int = 0

    private var observableBoolean: ObservableBoolean = ObservableBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, factory)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRedeemReceiptBinding.inflate(inflater, container, false)
        binding.buttonDone.setOnClickListener { goBack() }

        arguments.let {
            if (it != null){

                isDonate = RedeemSummaryFragmentArgs.fromBundle(it).isDonate
                donationPoints = RedeemSummaryFragmentArgs.fromBundle(it).donatedPoints
                binding.isDonate  = observableBoolean

                if(isDonate){
                 program = RedeemSummaryFragmentArgs.fromBundle(it).program
                 observableBoolean.set(true)

                }else{
                    observableBoolean.set(false)
                    orderResponse = RedeemSummaryFragmentArgs.fromBundle(it).orderResponse
                    isMerchant = RedeemSummaryFragmentArgs.fromBundle(it).isMerchant
                    isLinkToAccount = RedeemSummaryFragmentArgs.fromBundle(it).isLinkToAccount
                    product = RedeemSummaryFragmentArgs.fromBundle(it).petroCanadaProduct
                    binding.response = orderResponse
                }
            }
        }
        return initView()
    }

    private fun initView(): View{
        var imageId = 0
        try{
             context?.let { it ->
                 if(isDonate){
                program?.let {program ->
                    imageId = it.resources.getIdentifier(program.smallImage.toString(), "drawable", it.packageName)
                    sessionManager.profile.petroPointsNumber = getNewBalance()
                    binding.apply {
                        newBalanceValue.text = getString(R.string. points_redeemed_value, getNewBalance())
                        pointsRedeemedValue.text = getString(R.string.points_redeemed_value ,CardFormatUtils.formatBalance(donationPoints))
                        subtitle.text = program.info.message
                        emailSentToValue.text = sessionManager.profile.email
                        cardValue.text = getString(R.string.egift_card_value_in_dollar_generic, donationPoints/1000)
                        dateValue.text = DateUtils.getDate()
                        navigationButton.setOnClickListener {

                            activity?.let { activity ->

                                NavigationConsentAlerts.createAlert(activity,
                                    getString(R.string.leaving_app_alert_title),
                                    getString(R.string.leaving_app_alert_message),
                                    getString(R.string.leaving_app_alert_button),
                                    getString(R.string.cancel),
                                    program.info.url,
                                     this@RedeemSummaryFragment :: redirectToUrl)
                            }
                        }
                    }
                }
            }else{
                if(isMerchant){
                    sessionManager.profile.pointsBalance = orderResponse!!.transaction.transactionAmount.petroPoints.petroPointsRemaining
                    binding.apply {
                        newBalanceValue.text = getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(
                                orderResponse!!.transaction.transactionAmount.petroPoints.petroPointsRemaining
                            )
                        )
                       pointsRedeemedValue.text = getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(
                                orderResponse!!.transaction.transactionAmount.petroPoints.petroPointsRedeemed
                            )
                        )
                        valueTitle.setText(R.string.egift_card_value_title)
                        subtitle.setText(R.string.redeem_receipt_body)
                        cardValue.text = getString(R.string.egift_card_value_in_dollar_generic, orderResponse!!.shoppingCart.geteGift().value)
                        dateValue.text = DateUtils.getFormattedDate(orderResponse!!.transaction.transactionDate)
                        emailSentToValue.text = orderResponse!!.shipping.emailSentTo
                    }
                    imageId = it.resources.getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse!!.shoppingCart.geteGift().merchantId), "drawable", it.packageName)

                } else{
                    val remainingPoints = sessionManager.profile.pointsBalance - product!!.pointsPrice
                    sessionManager.profile.pointsBalance = remainingPoints
                    binding.apply {
                       newBalanceValue.text = getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(remainingPoints))
                       pointsRedeemedValue.text = getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(product!!.pointsPrice))
                       valueTitle.text = getString(R.string.single_ticket_receipt_quantity_title)
                       subtitle.text = getString(R.string.single_ticket_receipt_subtitle)
                       cardValue.text = it.resources.getQuantityString(R.plurals.single_ticket_receipt_quantity, product!!.units, product!!.units)
                       emailSentToValue.text = sessionManager.profile.email
                       dateValue.text = DateUtils.getFormattedDate(Calendar.getInstance().time)
                    }
                    imageId = it.resources.getIdentifier(MerchantsUtil.getRewardSmallImage(product!!.category), "drawable", it.packageName)

                }
            }
        }

        binding.image = requireContext().getDrawable(imageId)
        binding.redeemReceiptCardviewTitle.text = String.format(getString(R.string.thank_you), sessionManager.profile.firstName)
        }catch (e: Exception){
            e.let {
                Timber.d( it)
            }
        }
        return  binding.root
    }

    private fun redirectToUrl(url: String){

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun getNewBalance(): String{
        return CardFormatUtils.formatBalance(sessionManager.profile.pointsBalance - donationPoints)
    }

    private fun goBack() {
        if (!isMerchant && isLinkToAccount) {
            viewModel?.isLinkedToAccount = true
            orderResponse?.let {
                if (orderResponse != null) {
                    viewModel?.singleTicketNumber = Sets.newHashSet(*orderResponse!!.productsDelivered)
                }
            }
        }
        Navigation.findNavController(requireView()).popBackStack()
    }

    override fun onBackPressed() {
        goBack()
    }
}