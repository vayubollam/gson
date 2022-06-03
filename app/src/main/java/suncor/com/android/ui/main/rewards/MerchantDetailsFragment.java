package suncor.com.android.ui.main.rewards;

import static suncor.com.android.analytics.AnalyticsConstants.PETRO_POINTS_REDEEM_INFO;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import java.util.Objects;
import javax.inject.Inject;
import suncor.com.android.R;
import suncor.com.android.analytics.giftcard.MerchantDetailsAnalytics;
import suncor.com.android.databinding.FragmentMerchantDetailsBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;


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
        int imageId = requireContext().getResources().getIdentifier(genericEGiftCard.getSmallImage(), "drawable", requireContext().getPackageName());
        binding.setImage(requireContext().getDrawable(imageId));
        binding.setGenericGiftCard(genericEGiftCard);
        binding.executePendingBindings();
        binding.closeButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.points.setText(getString(R.string.rewards_signedin_header_balance, Objects.nonNull(sessionManager.getProfile()) ? CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance()) : 0));
        MerchantDetailsAnalytics.logScreenNameClass(requireActivity(),PETRO_POINTS_REDEEM_INFO + genericEGiftCard.getScreenName(),this.getClass().getSimpleName());
        binding.buyButton.setOnClickListener(v -> {
            MerchantDetailsFragmentDirections.ActionMerchantDetailsFragmentToGiftCardValueConfirmation action = MerchantDetailsFragmentDirections.actionMerchantDetailsFragmentToGiftCardValueConfirmation(genericEGiftCard);
            MerchantDetailsAnalytics.logFormStart(requireContext(), "Redeem for "+genericEGiftCard.getShortName()+" eGift card");
            Navigation.findNavController(requireView()).navigate(action);
        });
        return binding.getRoot();
    }


}