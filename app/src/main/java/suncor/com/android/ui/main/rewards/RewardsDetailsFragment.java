package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import suncor.com.android.databinding.FragmentRewardsDetailsBinding;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;

public class RewardsDetailsFragment extends MainActivityFragment {

    private FragmentRewardsDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsDetailsBinding.inflate(inflater, container, false);
        GenericEGiftCard genericGiftCard = RewardsDetailsFragmentArgs.fromBundle(getArguments()).getGenericGiftCard();
        int imageId = requireContext().getResources().getIdentifier(genericGiftCard.getSmallImage(), "drawable", requireContext().getPackageName());
        binding.setImage(requireContext().getDrawable(imageId));
        binding.setGenericEGiftCard(genericGiftCard);

        binding.closeButton.setOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());

        return binding.getRoot();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-redeem-info-" + binding.getGenericEGiftCard().getName();
    }
}
