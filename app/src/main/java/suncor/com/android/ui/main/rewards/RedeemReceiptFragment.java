package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.MerchantsUtil;

public class RedeemReceiptFragment extends MainActivityFragment {


    @Inject
    SessionManager sessionManager;
    private FragmentRedeemReceiptBinding binding;
    private OrderResponse orderResponse;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRedeemReceiptBinding.inflate(inflater, container, false);
        binding.buttonDone.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        if (getArguments() != null) {
            orderResponse = RedeemReceiptFragmentArgs.fromBundle(getArguments()).getOrderResponse();
            binding.setResponse(orderResponse);
            sessionManager.getProfile().setPointsBalance(orderResponse.getTransaction().getTransactionAmount().getPetroPoints().getPetroPointsRemaining());
        }return initView();
    }

    private View initView() {
        AnalyticsUtils.setCurrentScreenName(this.getActivity(), "my-petro-points-redeem-info-"+MerchantsUtil.getMerchantScreenName(orderResponse.getShoppingCart().geteGift().getMerchantId())+"-success");
        AnalyticsUtils.logEvent(this.getContext(),"form_complete",
                new Pair<>("formName", "Redeem for "+MerchantsUtil.getMerchantShortName(orderResponse.getShoppingCart().geteGift().getMerchantId())+" eGift card"),
                new Pair<>("formSelection","$"+orderResponse.getShoppingCart().eGift.getValue()+" gift card")
        );
        int imageId = getContext().getResources().getIdentifier(MerchantsUtil.getMerchantSmallImage(orderResponse.getShoppingCart().geteGift().getMerchantId()), "drawable", getContext().getPackageName());
        binding.setImage(getContext().getDrawable(imageId));
        binding.redeemReceiptCardviewTitle.setText(String.format(getString(R.string.thank_you), sessionManager.getProfile().getFirstName()));
        return binding.getRoot();
    }
}
