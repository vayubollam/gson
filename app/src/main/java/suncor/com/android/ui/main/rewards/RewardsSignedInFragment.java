package suncor.com.android.ui.main.rewards;

import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
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
import suncor.com.android.databinding.FragmentRewardsSignedinBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;
import suncor.com.android.utilities.AnalyticsUtils;

public class RewardsSignedInFragment extends BottomNavigationFragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    Gson gson;

    private FragmentRewardsSignedinBinding binding;
    private RewardsSignedInViewModel viewModel;
    private boolean isHeaderVisible;
    private boolean scroll20 = false, scroll40 = false, scroll60 = false, scroll80 = false, scroll100 = false;
    private final ArrayList<GenericEGiftCard> eGiftCardsList = new ArrayList<>();
    private boolean systemMarginsAlreadyApplied;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardsSignedInViewModel.class);
        viewModel.navigateToDiscovery.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_rewards_signedin_tab_to_rewardsDiscoveryFragment);
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                        eGiftCard.setHowToUse(getContext().getString(R.string.how_to_use_petrocanada));
                        eGiftCard.setDataDynamic(true);
                        eGiftCard.setMoreGIftCard(false);
                        eGiftCard.seteGifts(m.geteGifts());
                        eGiftCard.setScreenName(merchantItem.getMerchantScreenName());
                        eGiftCard.setShortName(merchantItem.getMerchantShortName());

                        eGiftCardsList.add(2, eGiftCard);
                    }
                }

                GenericEGiftCard eGiftCard = new GenericEGiftCard();
                eGiftCard.setSmallImage("more_e_gift_card_small");
                eGiftCard.setLargeImage("more_e_gift_card_large");
                eGiftCard.setMoreGIftCard(true);
                eGiftCard.setTitle(getResources().getString(R.string.merchant_more_egift_card));
                eGiftCard.setPoints(getResources().getString(R.string.rewards_e_gift_card_starting_points));
                eGiftCard.setSubtitle(getResources().getString(R.string.rewards_egift_card_subtitle));
                eGiftCard.setHowToUse(getResources().getString(R.string.rewards_signedin_redeeming_your_rewards_desc_dining_card));
                eGiftCard.setDataDynamic(true);
                eGiftCard.setSubtitle(getResources().getString(R.string.rewards_egift_card_subtitle));
                eGiftCard.seteGifts(null);
                eGiftCard.setScreenName("more_gift_card");
                eGiftCard.setShortName("More_gift_card");

                eGiftCardsList.add(3, eGiftCard);

                binding.rewardsList.setAdapter(new GenericGiftCardsAdapter(eGiftCardsList, this::eCardClicked));

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsSignedinBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
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
                if (percentage > 20 && !scroll20) {
                    scroll20 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "20"));
                } else if (percentage > 40 && !scroll40) {
                    scroll40 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "40"));
                } else if (percentage > 60 && !scroll60) {
                    scroll60 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "60"));
                } else if (percentage > 80 && !scroll80) {
                    scroll80 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "80"));
                } else if (percentage > 100 && !scroll100) {
                    scroll100 = true;
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", "100"));
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
            getView().post(this::disableLightStatusBar);
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
        int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    private void disableLightStatusBar() {
        int flags = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    private void eCardClicked(GenericEGiftCard genericEGiftCard) {
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
    }

    private void mapRewardsListIntoGeneric(ArrayList<Reward> rewardsList) {

        eGiftCardsList.clear();

        for (Reward reward : rewardsList) {
            if (!reward.getName().equals("egift-cards")) {
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
}