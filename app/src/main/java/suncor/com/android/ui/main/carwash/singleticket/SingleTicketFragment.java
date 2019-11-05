package suncor.com.android.ui.main.carwash.singleticket;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentSingleTicketRedeemBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.common.OnBackPressedListener;

public class SingleTicketFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private SingleTicketViewModel viewModel;
    private FragmentSingleTicketRedeemBinding binding;
    private SingleTicketListItemAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 600;
    private Animation animFromBottom;
    private boolean firstTime = true;
    private float totalFixY;

    public static SingleTicketFragment newInstance() {
        return new SingleTicketFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, factory).get(SingleTicketViewModel.class);
        animInterpolator = new DecelerateInterpolator(3f);
        animFromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        animFromBottom.setInterpolator(animInterpolator);
        animFromBottom.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int[] totalFix = new int[2];
                binding.termsAgreementDownDivider2.getLocationOnScreen(totalFix);
                totalFixY = totalFix[1];
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        MockSingleTicket singleTicket = GiftCardValueConfirmationFragmentArgs.fromBundle(getArguments()).getMerchanItem();
//        viewModel.setMerchantItem(merchantItem);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_single_ticket_redeem, container, false);
        //binding.setEventHandler(this);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.valuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new SingleTicketListItemAdapter(viewModel.getTicketItems(), viewModel.getSessionManager().getProfile().getPointsBalance(),
                this::cardValueChanged, this::moveUnderneathLayoutsUp);
        binding.valuesRecyclerView.setAdapter(adapter);
        binding.redeemTotalLayoutFix.setAlpha(0f);
        binding.changeValueBtn.setOnClickListener(v -> {
            binding.cardValueTxt.setText(getString(R.string.redeem_egift_card_select_value));
            binding.changeValueBtn.setEnabled(false);
            binding.changeValueBtn.animate().alpha(0.0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            shiftUnderneathLayousDown();
        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            int[] totalScroll = new int[2];
            binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
            float totalScrollY = totalScroll[1];
            //scrolling down
            if ((totalScrollY + 20) >= totalFixY) {
                binding.redeemTotalLayoutFix.setAlpha(1);
                binding.redeemTotalLayoutFix.setZ(4);
            }
            //scrolling up
            if (totalScrollY < totalFixY) {
                binding.redeemTotalLayoutFix.setAlpha(0);
                binding.redeemTotalLayoutFix.setZ(-4);
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(viewModel.getIsAnyTicketReedeemable().getValue()) adapter.initialLaunch();
    }

    private void cardValueChanged(Integer selectedItem) {
        int valueSelected = viewModel.getTicketItems().get(selectedItem).getPetroPointsRequired();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        //viewModel.setEGift(viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem));
        binding.redeemTotalPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(valueSelected)));
        binding.redeemTotalPointsTxt2.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(valueSelected)));
        binding.redeemNewPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(userPetroPoints - valueSelected)));
        binding.redeemNewPointsTxt2.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(userPetroPoints - valueSelected)));
        binding.cardValueTxt.setText(getString(R.string.redeem_egift_current_value));
        binding.changeValueBtn.animate().alpha(1.0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                binding.changeValueBtn.setEnabled(true);
            }
        });
        binding.nestedScrollView.scrollTo(0, 0);
        if (binding.paymentMethodLayout.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.paymentMethodLayout.setVisibility(View.VISIBLE);
                binding.paymentMethodLayout.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        if (binding.redeemAddressLayout.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.redeemAddressLayout.setVisibility(View.VISIBLE);
                binding.redeemAddressLayout.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        if (binding.addToAccountLayout.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.addToAccountLayout.setVisibility(View.VISIBLE);
                binding.addToAccountLayout.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        if (binding.redeemTotalLayoutScroll.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.redeemTotalLayoutScroll.setVisibility(View.VISIBLE);
                binding.redeemTotalLayoutScroll.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        if (binding.redeemBtn.getVisibility() == View.GONE) {
            new Handler().postDelayed(() ->
            {
                binding.redeemBtn.setVisibility(View.VISIBLE);
                binding.redeemBtn.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        if (!firstTime) {
            moveUnderneathLayoutsUp();
        }

        //binding.nestedScrollView.setScrollingEnabled(true);

        firstTime = false;
    }

    private void shiftUnderneathLayousDown() {
        adapter.showValues();
        binding.paymentMethodLayout.animate()
                .translationY(-(binding.paymentMethodLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1)))
                .setInterpolator(animInterpolator)
                .setStartDelay(0)
                .setDuration(ANIM_DURATION);
        binding.redeemAddressLayout.animate()
                .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1)))
                .setInterpolator(animInterpolator)
                .setStartDelay(0)
                .setDuration(ANIM_DURATION);
        binding.addToAccountLayout.animate()
                .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1)))
                .setInterpolator(animInterpolator)
                .setStartDelay(0)
                .setDuration(ANIM_DURATION);
        binding.redeemTotalLayoutScroll.animate()
                .translationY(-(binding.redeemTotalLayoutScroll.getTranslationY() + adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1)))
                .setInterpolator(animInterpolator)
                .setStartDelay(0)
                .setUpdateListener(animation -> {
                    int[] totalScroll = new int[2];
                    binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
                    float totalScrollY = totalScroll[1];
                    if (totalScrollY > totalFixY) {
                        binding.redeemTotalLayoutFix.setAlpha(1);
                        binding.redeemTotalLayoutFix.setZ(4);
                        // binding.nestedScrollView.setScrollingEnabled(true);
                        binding.valuesRecyclerView.setNestedScrollingEnabled(true);
                    }
                })
                .setDuration(ANIM_DURATION);
    }

    private void moveUnderneathLayoutsUp() {
        binding.paymentMethodLayout.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);
        binding.redeemAddressLayout.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);
        binding.addToAccountLayout.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);
        binding.redeemTotalLayoutScroll.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getTicketItems().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        int[] totalScroll = new int[2];
                        binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
                        float totalScrollY = totalScroll[1];
                        if (totalScrollY > totalFixY) {
                            binding.redeemTotalLayoutFix.setVisibility(View.VISIBLE);
                            binding.redeemTotalLayoutFix.setAlpha(1);
                            binding.redeemTotalLayoutFix.setZ(4);
                        }
                    }
                })
                .setUpdateListener(animation -> {
                    int[] totalScroll = new int[2];
                    binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
                    float totalScrollY = totalScroll[1];
                    if (totalScrollY < totalFixY) {
                        binding.redeemTotalLayoutFix.setAlpha(0);
                        binding.redeemTotalLayoutFix.setZ(-4);
                        //binding.nestedScrollView.setScrollingEnabled(false);
                        binding.valuesRecyclerView.setNestedScrollingEnabled(false);
                    }
                }).setDuration(ANIM_DURATION);
    }


    @Override
    public void onBackPressed() {
        Navigation.findNavController(getView()).popBackStack();
    }

//    public void redeemConfirmButtonClicked() {
//        viewModel.sendRedeemData();
//    }
}

