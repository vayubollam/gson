package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentRedeemReceiptBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.MerchantsUtil;

public class RedeemReceiptFragment extends MainActivityFragment implements OnBackPressedListener {


    @Inject
    SessionManager sessionManager;
    private FragmentRedeemReceiptBinding binding;
    private OrderResponse orderResponse;
    private boolean isMerchant;
    private boolean isLinkToAccount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRedeemReceiptBinding.inflate(inflater, container, false);
        binding.buttonDone.setOnClickListener(v -> goBack());

        if (getArguments() != null) {
            orderResponse = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getOrderResponse();
            isMerchant = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsMerchant();
            isLinkToAccount = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getIsLinkToAccount();
            Log.i("TTT", "isMerchant=" + isMerchant + " isLinkedToAccount=" + isLinkToAccount);
            binding.setResponse(orderResponse);
            sessionManager.getProfile().setPointsBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining());
        }
        return initView();
    }

    private View initView() {
        int imageId;
        if (isMerchant) {
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse.getShoppingCart().geteGift().getMerchantId()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText(R.string.egift_card_value_title);
            binding.subtitle.setText(R.string.redeem_receipt_body);
            binding.cardValue.setText(getString(R.string.egift_card_value_in_dollar_generic, orderResponse.getShoppingCart().geteGift().getValue()));
            binding.emailSentToValue.setText(orderResponse.getShipping().getEmailSentTo());
        } else {
            imageId = getContext().getResources().getIdentifier(MerchantsUtil.getRewardSmallImage(orderResponse.getShoppingCart().getPetroCanadaProduct().getCategory()), "drawable", getContext().getPackageName());
            binding.valueTitle.setText("Number of tickets");
            binding.subtitle.setText("Scan your ticket at any participating GlideWash or SuperWash location.");
            binding.cardValue.setText(String.valueOf(orderResponse.getShoppingCart().getPetroCanadaProduct().getUnits()));
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
//        if (!isMerchant && isLinkToAccount) {
//            RedeemReceiptFragmentDirections.ActionRedeemReceiptFragmentToCardsDetailsFragment action
//                    = RedeemReceiptFragmentDirections.actionRedeemReceiptFragmentToCardsDetailsFragment();
//            action.setIsCardFromCarWash(true);
//            action.setCardIndex(0);
//            action.setIsCardFromProfile(false);
//            Navigation.findNavController(getView()).navigate(action);
//        } else {
            Navigation.findNavController(getView()).popBackStack();
//        }
    }

    public boolean isMerchant() {
        return isMerchant;
    }
}
