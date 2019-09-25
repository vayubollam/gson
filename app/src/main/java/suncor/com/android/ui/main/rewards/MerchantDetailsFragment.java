package suncor.com.android.ui.main.rewards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentMerchantDetailsBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;


public class MerchantDetailsFragment extends MainActivityFragment {


    @Inject
    SessionManager sessionManager;
    private FragmentMerchantDetailsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMerchantDetailsBinding.inflate(inflater, container, false);
        MerchantItem merchantItem = MerchantDetailsFragmentArgs.fromBundle(getArguments()).getMerchantItem();
        int imageId = getContext().getResources().getIdentifier(merchantItem.getMerchantSmallImage(), "drawable", getContext().getPackageName());
        binding.setImage(getContext().getDrawable(imageId));
        binding.setMerchantItem(merchantItem);
        binding.executePendingBindings();
        binding.closeButton.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.points.setText(getString(R.string.rewards_signedin_header_balance, CardFormatUtils.formatBalance(sessionManager.getProfile().getPointsBalance())));
        return binding.getRoot();
    }


}
