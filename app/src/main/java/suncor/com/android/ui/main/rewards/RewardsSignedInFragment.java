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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentRewardsSignedinBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.merchants.Merchant;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.utilities.AnalyticsUtils;

public class RewardsSignedInFragment extends BottomNavigationFragment {

    private FragmentRewardsSignedinBinding binding;
    private RewardsSignedInViewModel viewModel;
    private boolean isHeaderVisible;

    @Inject
    ViewModelFactory viewModelFactory;
    private boolean systemMarginsAlreadyApplied;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RewardsSignedInViewModel.class);
        viewModel.navigateToPetroPoints.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                RewardsSignedInFragmentDirections.ActionRewardsSignedinTabToCardsDetailsFragment action = RewardsSignedInFragmentDirections.actionRewardsSignedinTabToCardsDetailsFragment();
                Navigation.findNavController(getView()).navigate(action);
            }
        });
        viewModel.navigateToDiscovery.observe(this, event -> {
            if (event.getContentIfNotHandled() != null) {
                Navigation.findNavController(getView()).navigate(R.id.action_rewards_signedin_tab_to_rewardsDiscoveryFragment);
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.merchantsLiveData.observe(this, merchants -> {
            if (merchants != null) {
                ArrayList<MerchantItem> merchantItems = new ArrayList<>();
                for (Merchant m : merchants) {
                    MerchantItem merchantItem = new MerchantItem(m, getContext());
                    merchantItems.add(merchantItem);
                }
                binding.eGiftList.setAdapter(new EGiftsCardAdapter(merchantItems, this::eCardClicked));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRewardsSignedinBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        RewardsAdapter rewardsAdapter = new RewardsAdapter(viewModel.getRewards(), this::rewardClicked);
        binding.rewardsList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.eGiftList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.rewardsList.setAdapter(rewardsAdapter);

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
                int pourcentage = (int) scrollPosition;
                if (pourcentage == 5 || pourcentage == 25 || pourcentage == 50 || pourcentage == 75 || pourcentage == 95) {
                    AnalyticsUtils.logEvent(getContext(), "scroll", new Pair<>("scrollDepthThreshold", Integer.toString(pourcentage)));
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

    private void rewardClicked(Reward reward) {
        RewardsSignedInFragmentDirections.ActionRewardsSignedinTabToRewardsDetailsFragment action = RewardsSignedInFragmentDirections.actionRewardsSignedinTabToRewardsDetailsFragment(reward);
        Navigation.findNavController(getView()).navigate(action);
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

    private void eCardClicked(MerchantItem merchantItem) {
        //TODO: handle merchant item clicked
    }
}
