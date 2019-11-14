package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentRedeemReceiptBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.MainViewModel;
import suncor.com.android.ui.main.common.MainActivityFragment;
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

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mainViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MainViewModel.class);
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
            binding.setResponse(orderResponse);
        }
        return initView();
    }

    private View initView() {
        int imageId;
        if (isMerchant) {
            sessionManager.getProfile().setPointsBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining());
            binding.newBalanceValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining())));
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse.getShoppingCart().geteGift().getMerchantId()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText(R.string.egift_card_value_title);
            binding.subtitle.setText(R.string.redeem_receipt_body);
            binding.cardValue.setText(getString(R.string.egift_card_value_in_dollar_generic, orderResponse.getShoppingCart().geteGift().getValue()));
            binding.emailSentToValue.setText(orderResponse.getShipping().getEmailSentTo());
        } else {
            int remainingPoints = sessionManager.getProfile().getPointsBalance()
                    - orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRedeemed();
            sessionManager.getProfile().setPointsBalance(remainingPoints);
            binding.newBalanceValue.setText(getString(R.string.points_redeemed_value, CardFormatUtils.formatBalance(remainingPoints)));
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getRewardSmallImage(orderResponse.getShoppingCart().getPetroCanadaProduct().getCategory()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText(getString(R.string.single_ticket_receipt_quantity_title));
            binding.subtitle.setText(getString(R.string.single_ticket_receipt_subtitle));
            binding.cardValue.setText(getContext().getResources().getQuantityString(R.plurals.single_ticket_receipt_quantity,
                    orderResponse.getShoppingCart().getPetroCanadaProduct().getUnits(), orderResponse.getShoppingCart().getPetroCanadaProduct().getUnits()));
            binding.emailSentToValue.setText(sessionManager.getProfile().getEmail());
        }
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
        }
        Navigation.findNavController(getView()).popBackStack();
    }

}
