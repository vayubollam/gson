package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentMerchantDetailsBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;
import suncor.com.android.utilities.AnalyticsUtils;


public class MerchantDetailsFragment extends MainActivityFragment {


    @Inject
    SessionManager sessionManager;
    private FragmentMerchantDetailsBinding binding;
    private GenericEGiftCard genericEGiftCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMerchantDetailsBinding.inflate(inflater, container, false);
        genericEGiftCard = MerchantDetailsFragmentArgs.fromBundle(getArguments()).getGenericGiftCard();
        int imageId = getContext().getResources().getIdentifier(genericEGiftCard.getSmallImage(), "drawable", getContext().getPackageName());
        binding.setImage(getContext().getDrawable(imageId));
        binding.setGenericGiftCard(genericEGiftCard);
        binding.executePendingBindings();
        binding.closeButton.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.points.setText(getString(R.string.rewards_signedin_header_balance, CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance())));
        AnalyticsUtils.setCurrentScreenName(this.getActivity(), "my-petro-points-redeem-info-"+genericEGiftCard.getScreenName());
        binding.buyButton.setOnClickListener(v -> {
            MerchantDetailsFragmentDirections.ActionMerchantDetailsFragmentToGiftCardValueConfirmation action = MerchantDetailsFragmentDirections.actionMerchantDetailsFragmentToGiftCardValueConfirmation(genericEGiftCard);
            AnalyticsUtils.logEvent(this.getContext(),"form_start",new Pair<>("formName", "Redeem for "+genericEGiftCard.getShortName()+" eGift card"));
            Navigation.findNavController(getView()).navigate(action);
        });
        return binding.getRoot();
    }


}