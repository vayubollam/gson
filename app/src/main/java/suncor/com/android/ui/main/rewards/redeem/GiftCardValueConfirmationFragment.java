package suncor.com.android.ui.main.rewards.redeem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentGiftCardValueConfirmationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.merchants.EGift;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.MerchantItem;

import static suncor.com.android.ui.common.cards.CardFormatUtils.formatBalance;


public class GiftCardValueConfirmationFragment extends MainActivityFragment {

    private GiftCardValueConfirmationViewModel viewModel;
    private FragmentGiftCardValueConfirmationBinding binding;
    private MerchantItem merchantItem;
    private GiftCardValueAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 300;
    private Animation animFromBottom;
    @Inject
    ViewModelFactory factory;

    public static GiftCardValueConfirmationFragment newInstance() {
        return new GiftCardValueConfirmationFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, factory).get(GiftCardValueConfirmationViewModel.class);
        animInterpolator = new DecelerateInterpolator(3f);
        animFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        animFromBottom.setInterpolator(animInterpolator);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        merchantItem = GiftCardValueConfirmationFragmentArgs.fromBundle(getArguments()).getMerchanItem();
        viewModel.setMerchantItem(merchantItem);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_gift_card_value_confirmation, container, false);
        binding.setVm(viewModel);
        binding.valuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new GiftCardValueAdapter(viewModel.getMerchantItem().getMerchant().geteGifts(), viewModel.getSessionManager().getProfile().getPointsBalance(), this::cardValueChanged);
        binding.valuesRecyclerView.setAdapter(adapter);
        binding.changeValueBtn.setOnClickListener(v -> {
            binding.cardValueTxt.setText("Select value");
            binding.changeValueBtn.setVisibility(View.GONE);
            adapter.showValues();
            binding.redeemAddressLayout.animate()
                    .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getMerchantItem().getMerchant().geteGifts().size() - 1)))
                    .setInterpolator(animInterpolator)
                    .setDuration(ANIM_DURATION);
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        for (EGift e : viewModel.getMerchantItem().getMerchant().geteGifts()) {
            if (e.getPetroPointsRequired() < viewModel.getSessionManager().getProfile().getPointsBalance()) {
                binding.notEnoughPointLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void cardValueChanged(Integer selectedItem) {
        int valueSelected = viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem).getPetroPointsRequired();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        binding.redeemTotalPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, formatBalance(valueSelected)));
        binding.redeemNewPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, formatBalance(userPetroPoints - valueSelected)));
        binding.cardValueTxt.setText("Card Value");
        binding.changeValueBtn.setVisibility(View.VISIBLE);
        binding.nestedScrollView.scrollTo(0, 0);
        if (binding.redeemAddressLayout.getVisibility() == View.GONE) {
            binding.redeemAddressLayout.setVisibility(View.VISIBLE);
        }
        if (binding.redeemTotalLayout.getVisibility() == View.GONE) {
            binding.redeemTotalLayout.setVisibility(View.VISIBLE);
            binding.redeemTotalLayout.startAnimation(animFromBottom);
        }
        if (binding.redeemBtn.getVisibility() == View.GONE) {
            binding.redeemBtn.setVisibility(View.VISIBLE);
            binding.redeemBtn.startAnimation(animFromBottom);
        }
        binding.redeemAddressLayout.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getMerchantItem().getMerchant().geteGifts().size() - 1))
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);

    }


}
