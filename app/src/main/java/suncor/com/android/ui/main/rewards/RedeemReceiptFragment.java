package suncor.com.android.ui.main.rewards;

import static suncor.com.android.analytics.AnalyticsConstants.PETRO_POINTS_REDEEM_INFO;
import android.os.Bundle;
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
import suncor.com.android.analytics.giftcard.RedeemReceiptAnalytics;
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
import suncor.com.android.utilities.MerchantsUtil;

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

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mainViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRedeemReceiptBinding.inflate(inflater, container, false);
        binding.buttonDone.setOnClickListener(v -> goBack());

        if (getArguments() != null) {
            orderResponse = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getOrderResponse();
            isMerchant = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsMerchant();
            isLinkToAccount = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsLinkToAccount();
            product = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getPetroCanadaProduct();
            binding.setResponse(orderResponse);
        }
        return initView();
    }

    private View initView() {
        int imageId;
        if (isMerchant) {
            sessionManager.getProfile().setPointsBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining());
            binding.newBalanceValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining())));
            binding.pointsRedeemedValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRedeemed())));
            imageId = requireContext().getResources().getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse.getShoppingCart().geteGift().getMerchantId()), "drawable", requireContext().getPackageName());
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
            imageId = requireContext().getResources().getIdentifier(MerchantsUtil.getRewardSmallImage(product.getCategory()), "drawable", requireContext().getPackageName());
            binding.valueTitle.setText(getString(R.string.single_ticket_receipt_quantity_title));
            binding.subtitle.setText(getString(R.string.single_ticket_receipt_subtitle));
            binding.cardValue.setText(requireContext().getResources().getQuantityString(R.plurals.single_ticket_receipt_quantity,
                    product.getUnits(), product.getUnits()));
            binding.emailSentToValue.setText(sessionManager.getProfile().getEmail());
            binding.dateValue.setText(DateUtils.getFormattedDate(Calendar.getInstance().getTime()));
        }

        RedeemReceiptAnalytics.logScreenNameClass(requireActivity(),
                PETRO_POINTS_REDEEM_INFO + orderResponse.getShoppingCart().geteGift().getMerchantId() + "success",
                this.getClass().getSimpleName());

        RedeemReceiptAnalytics.logRedeemReceiptFormComplete(requireContext(),
                MerchantsUtil.getMerchantShortName(orderResponse.getShoppingCart().geteGift().getMerchantId()),
                "$"+orderResponse.getShoppingCart().eGift.getValue()+" gift card" );
        binding.setImage(requireContext().getDrawable(imageId));
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
        Navigation.findNavController(requireView()).popBackStack();
    }

}
