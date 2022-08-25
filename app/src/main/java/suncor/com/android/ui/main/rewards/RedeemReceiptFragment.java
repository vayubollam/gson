package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.common.collect.Sets;

import java.util.Calendar;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentRedeemReceiptBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.petrocanadaproduct.PetroCanadaProduct;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.DateUtils;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.MerchantsUtil;

/*
public class RedeemReceiptFragment extends MainActivityFragment implements OnBackPressedListener {


    @Inject
    SessionManager sessionManager;
    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentRedeemReceiptBinding binding;
    private OrderResponse orderResponse;
    private boolean isMerchant;
    private boolean isLinkToAccount;
    private MainViewModel mainViewModel;
    private PetroCanadaProduct product;
    private boolean isDonate = false;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if(getActivity() != null)
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRedeemReceiptBinding.inflate(inflater, container, false);
        binding.buttonDone.setOnClickListener(v -> goBack());

        if (getArguments() != null) {
            isDonate = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsDonate();


            if(!isDonate){
                orderResponse = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getOrderResponse();
                isMerchant = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsMerchant();
                isLinkToAccount = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsLinkToAccount();
                product = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getPetroCanadaProduct();
                binding.setResponse(orderResponse);
            }else{

            }

        }
        return initView();
    }

    private View initView() {
        int imageId = 0;
        if (getContext() != null) {
        if (isMerchant) {
            sessionManager.getProfile().setPointsBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining());
            binding.newBalanceValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining())));
            binding.pointsRedeemedValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRedeemed())));
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse.getShoppingCart().geteGift().getMerchantId()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText(R.string.egift_card_value_title);
            binding.subtitle.setText(R.string.redeem_receipt_body);
            binding.cardValue.setText(getString(R.string.egift_card_value_in_dollar_generic, orderResponse.getShoppingCart().geteGift().getValue()));
            binding.dateValue.setText(DateUtils.getFormattedDate(orderResponse.getTransaction().getTransactionDate()));
            binding.emailSentToValue.setText(orderResponse.getShipping().getEmailSentTo());
        } else {
            int remainingPoints = sessionManager.getProfile().getPointsBalance() - product.getPointsPrice();
            sessionManager.getProfile().setPointsBalance(remainingPoints);
            binding.newBalanceValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(remainingPoints)));
            binding.pointsRedeemedValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(product.getPointsPrice())));
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getRewardSmallImage(product.getCategory()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText(getString(R.string.single_ticket_receipt_quantity_title));
            binding.subtitle.setText(getString(R.string.single_ticket_receipt_subtitle));
            binding.cardValue.setText(getContext().getResources().getQuantityString(R.plurals.single_ticket_receipt_quantity,
                    product.getUnits(), product.getUnits()));
            binding.emailSentToValue.setText(sessionManager.getProfile().getEmail());
            binding.dateValue.setText(DateUtils.getFormattedDate(Calendar.getInstance().getTime()));
        }
    }
        if(getActivity() != null)
        AnalyticsUtils.setCurrentScreenName(getActivity(), "my-petro-points-redeem-info-"+MerchantsUtil.getMerchantScreenName(orderResponse.getShoppingCart().geteGift().getMerchantId())+"-success");
        AnalyticsUtils.logEvent(this.getContext(),"form_complete",
                new Pair<>("formName", "Redeem for "+MerchantsUtil.getMerchantShortName(orderResponse.getShoppingCart().geteGift().getMerchantId())+" eGift card"),
                new Pair<>("formSelection","$"+orderResponse.getShoppingCart().eGift.getValue()+" gift card")
        );
        binding.setImage(getContext().getDrawable(imageId));
        binding.redeemReceiptCardviewTitle.setText(String.format(getString(R.string.thank_you), sessionManager.getProfile().getFirstName()));
        return binding.getRoot();
    }


    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        if (!isMerchant && isLinkToAccount) {
            mainViewModel.setLinkedToAccount(true);
            mainViewModel.setSingleTicketNumber(Sets.newHashSet(orderResponse.getProductsDelivered()));
        }
        if(getView() != null)
        Navigation.findNavController(getView()).popBackStack();
    }

}*/
