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

public class RewardsDetailsFragment extends MainActivityFragment {

    private FragmentRewardsDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsDetailsBinding.inflate(inflater, container, false);
        Reward reward = RewardsDetailsFragmentArgs.fromBundle(getArguments()).getReward();
        int imageId = getContext().getResources().getIdentifier(reward.getSmallImage(), "drawable", getContext().getPackageName());
        binding.setImage(getContext().getDrawable(imageId));
        binding.setReward(reward);

        binding.closeButton.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());

        return binding.getRoot();
    }

    @Override
    protected String getScreenName() {
        return "reward-detail-loggedin" + binding.getReward().getName();
    }
}
