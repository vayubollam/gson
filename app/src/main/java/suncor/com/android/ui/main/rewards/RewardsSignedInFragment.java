package suncor.com.android.ui.main.rewards;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import suncor.com.android.R;
import suncor.com.android.analytics.giftcard.RewardsSignedInAnalytics;
import suncor.com.android.databinding.FragmentRewardsSignedinBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.model.redeem.response.MemberEligibilityResponse;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_25;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_5;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_50;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_75;
import static suncor.com.android.analytics.AnalyticsConstants.SCROLL_DEPTH_95;
import static suncor.com.android.analytics.giftcard.RewardsSignedInAnalytics.REWARDS_SIGNED_IN_SCREEN_NAME;
import suncor.com.android.utilities.AnalyticsUtils;
import suncor.com.android.utilities.Constants;
import suncor.com.android.utilities.Timber;

public class RewardsSignedInFragment extends BottomNavigationFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    Gson gson;

    private FragmentRewardsSignedinBinding binding;
    private RewardsSignedInViewModel viewModel;
    private boolean isHeaderVisible;
    private boolean scroll5 = false, scroll25 = false, scroll50 = false, scroll75 = false, scroll95 = false;
    private final ArrayList<GenericEGiftCard> eGiftCardsList = new ArrayList<>();
    private boolean systemMarginsAlreadyApplied;
    private boolean isRedeemable;
    public ObservableInt petroPointsBalance = new ObservableInt();
    private GenericGiftCardsAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardsSignedInViewModel.class);
        viewModel.navigateToDiscovery.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                NavDestination navDestination = Navigation.findNavController(requireView()).getCurrentDestination();
                if (navDestination != null && navDestination.getId() == R.id.rewards_signedin_tab) {
                    Navigation.findNavController(requireView()).navigate(R.id.action_rewards_signedin_tab_to_rewardsDiscoveryFragment);
                }
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RewardsSignedInAnalytics.logScreenNameClass(requireActivity(), REWARDS_SIGNED_IN_SCREEN_NAME,this.getClass().getSimpleName());

        viewModel.merchantsLiveData.observe(getViewLifecycleOwner(), merchants -> {
            if (merchants != null) {
                for (Merchant m : merchants) {
                    MerchantItem merchantItem = new MerchantItem(m, getContext());
                    if ((merchantItem.getLocalizedMerchantName().equals(requireContext().getResources().getString(R.string.merchant_petrocanada_card)))) {
                        GenericEGiftCard eGiftCard = new GenericEGiftCard();
                        eGiftCard.setSmallImage(merchantItem.getMerchantSmallImage());
                        eGiftCard.setLargeImage(merchantItem.getMerchantLargeImage());
                        eGiftCard.setTitle(merchantItem.getLocalizedMerchantName());
                        eGiftCard.setPoints(merchantItem.getPointsMerchantName());
                        eGiftCard.setSubtitle(merchantItem.getSubtitleMerchantName());
                        eGiftCard.setHowToRedeem(merchantItem.getRedeemingDescription());
                        eGiftCard.setHowToUse(requireContext().getString(R.string.how_to_use_petrocanada));
                        eGiftCard.setDataDynamic(true);
                        eGiftCard.setMoreGIftCard(false);
                        eGiftCard.seteGifts(m.geteGifts());
                        eGiftCard.setScreenName(merchantItem.getMerchantScreenName());
                        eGiftCard.setShortName(merchantItem.getMerchantShortName());

                        eGiftCardsList.add(2, eGiftCard);
                    }
                }

                eGiftCardsList.add(3, getGIftCardAt(3));
            }
        });

        adapter = new GenericGiftCardsAdapter(eGiftCardsList, this::eCardClicked, isRedeemable);
        binding.rewardsList.setAdapter(adapter);

        viewModel.getMemberEligibility().observe(getViewLifecycleOwner(), response -> {
            switch (response.status) {
                case SUCCESS:
                    MemberEligibilityResponse data = response.data;
                    if (data != null) {
                        isRedeemable = data.getEligible();
                        viewModel.updatePetroPoints(data.getPointsBalance());
                        petroPointsBalance.set(data.getPointsBalance());
                        adapter.isEligible = isRedeemable;
                        adapter.notifyDataSetChanged();
                    }
                    return;
                case ERROR:
                    adapter.isEligible = false;
                    petroPointsBalance.set(viewModel.getPetroPointsBalance());
                    adapter.notifyDataSetChanged();
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsSignedinBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.setPetroPoints(petroPointsBalance);
        mapRewardsListIntoGeneric(new ArrayList<>(Arrays.asList(viewModel.getRewards())));
        binding.rewardsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        systemMarginsAlreadyApplied = false;
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            if (!systemMarginsAlreadyApplied) {
                systemMarginsAlreadyApplied = true;
                int systemsTopMargin = insets.getSystemWindowInsetTop();
                ((ViewGroup.MarginLayoutParams) binding.balancePetropoints.getLayoutParams()).topMargin += systemsTopMargin;
                binding.balancePetropoints.getParent().requestLayout();
                binding.headerLayout.setPadding(
                        binding.headerLayout.getPaddingLeft(),
                        binding.headerLayout.getPaddingTop() + systemsTopMargin,
                        binding.headerLayout.getPaddingRight(),
                        binding.headerLayout.getPaddingBottom());
            }
            return insets;
        });

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //handle visibility of the header
            int threshold = binding.balancePetropoints.getBottom();
            if (scrollY > oldScrollY) {
                double scrollViewHeight = v.getChildAt(0).getBottom() - v.getHeight();
                double getScrollY = v.getScrollY();
                double scrollPosition = (getScrollY / scrollViewHeight) * 100d;
                int percentage = (int) scrollPosition;

                if (percentage > 5 && !scroll5) {
                    scroll5 = true;
                    RewardsSignedInAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_5);
                } else if (percentage > 25 && !scroll25) {
                    scroll25 = true;
                    RewardsSignedInAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_25);
                } else if (percentage > 50 && !scroll50) {
                    scroll50 = true;
                    RewardsSignedInAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_50);
                } else if (percentage > 75 && !scroll75) {
                    scroll75 = true;
                    RewardsSignedInAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_75);
                } else if (percentage > 95 && !scroll95) {
                    scroll95 = true;
                    RewardsSignedInAnalytics.logScrollDepth(requireContext(), SCROLL_DEPTH_95);
                }
            }
            if (scrollY >= threshold) {

                if (!isHeaderVisible) {
                    binding.headerLayout.setVisibility(View.VISIBLE);
                    isHeaderVisible = true;
                    enableLightStatusBar();
                }
                float alpha = Math.min(((float) (scrollY - threshold)) / (binding.balanceValue.getBottom() - threshold), 1f);
                binding.headerLayout.setAlpha(alpha);
            } else if (isHeaderVisible) {
                disableLightStatusBar();
                binding.headerLayout.setVisibility(View.INVISIBLE);
                binding.headerLayout.setAlpha(0);
                isHeaderVisible = false;
            }

            //Add parallax effect
            int totalTranslation = binding.balanceBottomToCards.getHeight();
            View barrierView = binding.rewardsList;
            float parallaxEffectValue = (float) totalTranslation / barrierView.getTop();
            float greetingsTranslation = Math.min(totalTranslation, scrollY * parallaxEffectValue);
            binding.balancePetropoints.setTranslationY(greetingsTranslation);
            binding.balanceValue.setTranslationY(greetingsTranslation);
            binding.headerImage.setTranslationY((float) (scrollY * 0.5));
        });

        //adapt header visibility after navigation
        if (isHeaderVisible) {
            binding.headerLayout.setVisibility(View.VISIBLE);
            enableLightStatusBar();
        }

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isHeaderVisible) {
            requireView().post(this::disableLightStatusBar);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        enableLightStatusBar();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-redeem-info-general";
    }

    private void enableLightStatusBar() {
        int flags = requireActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    private void disableLightStatusBar() {
        int flags = requireActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        requireActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void eCardClicked(GenericEGiftCard genericEGiftCard) {
        try {
            if (genericEGiftCard.isDataDynamic()) {
                if (genericEGiftCard.isMoreGIftCard()) {
                    String merchantList = gson.toJson(viewModel.getMerchantList());
                    RewardsSignedInFragmentDirections.ActionRewardsSignedinTabToMoreEGiftCardCategories action = RewardsSignedInFragmentDirections.actionRewardsSignedinTabToMoreEGiftCardCategories();
                    action.setMerchantList(merchantList);
                    NavDestination navDestination = Navigation.findNavController(requireView()).getCurrentDestination();
                    if (navDestination != null && navDestination.getId() == R.id.rewards_signedin_tab) {
                        Navigation.findNavController(requireView()).navigate(action);
                    }
                } else {
                    RewardsSignedInFragmentDirections.ActionRewardsSignedinTabToMerchantDetailsFragment action = RewardsSignedInFragmentDirections.actionRewardsSignedinTabToMerchantDetailsFragment(genericEGiftCard);
                    NavDestination navDestination = Navigation.findNavController(requireView()).getCurrentDestination();
                    if (navDestination != null && navDestination.getId() == R.id.rewards_signedin_tab) {
                        Navigation.findNavController(requireView()).navigate(action);
                    }
                }
            } else {
                RewardsSignedInFragmentDirections.ActionRewardsSignedinTabToRewardsDetailsFragment action = RewardsSignedInFragmentDirections.actionRewardsSignedinTabToRewardsDetailsFragment(genericEGiftCard);
                Navigation.findNavController(requireView()).navigate(action);

            }
        } catch (Exception e) {
            Timber.d(e);
        }

    }

    private void mapRewardsListIntoGeneric(ArrayList<Reward> rewardsList) {

        eGiftCardsList.clear();

        for (Reward reward : rewardsList) {
            if (!reward.getName().equals(Constants.E_GIFT_CARD)) {
                GenericEGiftCard giftCard = new GenericEGiftCard();
                giftCard.setName(reward.getName());
                giftCard.setPoints(reward.getPoints());
                giftCard.setTitle(reward.getTitle());
                giftCard.setSubtitle(reward.getSubtitle());
                giftCard.setHowToUse(reward.getDescription());
                giftCard.setLargeImage(reward.getLargeImage());
                giftCard.setSmallImage(reward.getSmallImage());
                giftCard.setDataDynamic(false);
                giftCard.setMoreGIftCard(false);

                eGiftCardsList.add(giftCard);
            }
        }
    }

    public GenericEGiftCard getGIftCardAt(int index) {
        GenericEGiftCard eGiftCard = new GenericEGiftCard();

        switch (index) {

            case 3:
                eGiftCard.setSmallImage(Constants.MORE_GIFT_CARD_IMAGE_SMALL);
                eGiftCard.setLargeImage(Constants.MORE_GIFT_CARD_IMAGE_LARGE);
                eGiftCard.setMoreGIftCard(true);
                eGiftCard.setTitle(getResources().getString(R.string.merchant_more_egift_card));
                eGiftCard.setPoints(getResources().getString(R.string.rewards_e_gift_card_starting_points));
                eGiftCard.setSubtitle(getResources().getString(R.string.rewards_egift_card_subtitle));
                eGiftCard.setHowToUse(getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_dining_card));
                eGiftCard.setDataDynamic(true);
                eGiftCard.seteGifts(null);
                eGiftCard.setScreenName(Constants.MORE_GIFT_CARD_SCREEN_NAME);
                eGiftCard.setShortName(Constants.MORE_GIFT_CARD_SHORT_NAME);
                break;
        }

        return eGiftCard;
    }
}