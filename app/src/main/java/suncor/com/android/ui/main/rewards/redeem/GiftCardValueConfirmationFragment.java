package suncor.com.android.ui.main.rewards.redeem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import javax.inject.Inject;



import suncor.com.android.R;
import suncor.com.android.databinding.FragmentGiftCardValueConfirmationBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;


public class GiftCardValueConfirmationFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private GiftCardValueConfirmationViewModel viewModel;
    private FragmentGiftCardValueConfirmationBinding binding;
    private GiftCardValueAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 600;
    private Animation animFromBottom;
    private boolean firstTime = true;
    private float totalFixY;

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

        viewModel.orderApiData.observe(this, (orderResponseResource) -> {

            switch (orderResponseResource.status) {
                case SUCCESS:
                    OrderResponse orderResponse = orderResponseResource.data;
                    GiftCardValueConfirmationFragmentDirections.ActionGiftCardValueConfirmationToRedeemReceiptFragment action =
                            GiftCardValueConfirmationFragmentDirections.actionGiftCardValueConfirmationToRedeemReceiptFragment(Objects.requireNonNull(orderResponse), true);

                    if (getView() != null) {
                        getView().postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 1000);
                    }
                    break;
                case ERROR:
                    AnalyticsUtils.logEvent(this.getContext(), "form_error",
                            new Pair<>("formName","Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card"),
                            new Pair<>("errorMessage",orderResponseResource.message));
                    if (ErrorCodes.ERR_CARD_LOCK.equals(orderResponseResource.message) || ErrorCodes.ERR_SECONDARY_CARD_HOLDER_REDEMPTIONS_DISABLED.equals(orderResponseResource.message)) {
                        AnalyticsUtils.logEvent(requireActivity().getApplicationContext(), "alert",
                                new Pair<>("alertTitle", getString(R.string.msg_e030_title)+"("+getString(R.string.msg_e030_message)+")"),
                                new Pair<>("formName","Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card")
                        );
                        new AlertDialog.Builder(requireContext())
                                .setTitle(R.string.msg_e030_title)
                                .setMessage(R.string.msg_e030_message)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {
                                    AnalyticsUtils.logEvent(requireActivity().getApplicationContext(), "alert_interaction",
                                            new Pair<>("alertTitle", getString(R.string.msg_e030_title)+"("+getString(R.string.msg_e030_message)+")"),
                                            new Pair<>("alertSelection",getString(R.string.ok)),
                                            new Pair<>("formName","Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card")
                                    );
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    } else {
                        AnalyticsUtils.logEvent(this.getContext(), AnalyticsUtils.Event.error,
                                new Pair<>(AnalyticsUtils.Param.errorMessage,getString(R.string.msg_e001_title)),
                                new Pair<>(AnalyticsUtils.Param.formName, "Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card"));
                        Alerts.prepareGeneralErrorDialog(getActivity(), "Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card").show();
                    }
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        GenericEGiftCard genericGiftCard = GiftCardValueConfirmationFragmentArgs.fromBundle(getArguments()).getGenericGiftCard();
        viewModel.setGenericCardItem(genericGiftCard);
        AnalyticsUtils.setCurrentScreenName(this.requireActivity(), "my-petro-points-redeem-info-"+viewModel.getGiftCardItem().getScreenName()+"-value");
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_gift_card_value_confirmation, container, false);
        binding.setEventHandler(this);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());
        binding.valuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new GiftCardValueAdapter(viewModel.getGiftCardItem().geteGifts(), viewModel.getSessionManager().getProfile().getPointsBalance(), this::cardValueChanged);
        binding.valuesRecyclerView.setAdapter(adapter);
        binding.redeemTotalLayoutFix.setAlpha(0f);
        binding.redeemTotalLayoutFixShadow.setAlpha(0f);
        binding.changeValueBtn.setOnClickListener(v -> {
            binding.cardValueTxt.setText(getString(R.string.redeem_egift_card_select_value));
            binding.changeValueBtn.setEnabled(false);
            binding.changeValueBtn.animate().alpha(0.0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            adapter.showValues();
            binding.redeemAddressLayout.animate()
                    .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getGiftCardItem().geteGifts().size() - 1)))
                    .setInterpolator(animInterpolator)
                    .setStartDelay(0)
                    .setDuration(ANIM_DURATION);
            binding.redeemTotalLayoutScroll.animate()
                    .translationY(-(binding.redeemTotalLayoutScroll.getTranslationY() + adapter.getItemHeight() * (viewModel.getGiftCardItem().geteGifts().size() - 1)))
                    .setInterpolator(animInterpolator)
                    .setStartDelay(0)
                    .setUpdateListener(animation -> {
                        int[] totalScroll = new int[2];
                        binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
                        float totalScrollY = totalScroll[1];
                        if (totalScrollY > totalFixY) {
                            binding.redeemTotalLayoutFix.setAlpha(1);
                            binding.redeemTotalLayoutFix.setZ(4);
                            binding.redeemTotalLayoutFixShadow.setAlpha(1);
                            binding.nestedScrollView.setScrollingEnabled(true);
                            binding.valuesRecyclerView.setNestedScrollingEnabled(true);
                        }
                    })
                    .setDuration(ANIM_DURATION);

        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            int[] totalScroll = new int[2];
            binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
            float totalScrollY = totalScroll[1];
            //scrolling down
            if ((totalScrollY + 20) >= totalFixY) {
                binding.redeemTotalLayoutFix.setAlpha(1);
                binding.redeemTotalLayoutFix.setZ(4);
                binding.redeemTotalLayoutFixShadow.setAlpha(1);

            }
            //scrolling up
            if (totalScrollY < totalFixY) {
                binding.redeemTotalLayoutFix.setAlpha(0);
                binding.redeemTotalLayoutFix.setZ(-4);
                binding.redeemTotalLayoutFixShadow.setAlpha(0);
            }

        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.getGiftCardItem().geteGifts().get(0).getPetroPointsRequired() > viewModel.getSessionManager().getProfile().getPointsBalance()) {
            binding.notEnoughPointLayout.setVisibility(View.VISIBLE);
        }

    }

    private void cardValueChanged(Integer selectedItem) {
        int valueSelected = viewModel.getGiftCardItem().geteGifts().get(selectedItem).getPetroPointsRequired();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        viewModel.setEGift(viewModel.getGiftCardItem().geteGifts().get(selectedItem));
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
        if (binding.redeemAddressLayout.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.redeemAddressLayout.setVisibility(View.VISIBLE);
                binding.redeemAddressLayout.startAnimation(animFromBottom);
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
        binding.redeemAddressLayout.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getGiftCardItem().geteGifts().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);
        binding.redeemTotalLayoutScroll.animate()
                .translationY(-adapter.getItemHeight() * (viewModel.getGiftCardItem().geteGifts().size() - 1))
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
                            binding.redeemTotalLayoutFixShadow.setAlpha(1);
                        }
                    }
                })
                .setUpdateListener(animation -> {
                    int[] totalScroll = new int[2];
                    binding.termsAgreementDownDivider.getLocationOnScreen(totalScroll);
                    float totalScrollY = totalScroll[1];
                    if (totalScrollY < totalFixY) {
                        binding.redeemTotalLayoutFix.setAlpha(0);
                        binding.redeemTotalLayoutFixShadow.setAlpha(0);
                        binding.redeemTotalLayoutFix.setZ(-4);
                        binding.nestedScrollView.setScrollingEnabled(false);
                        binding.valuesRecyclerView.setNestedScrollingEnabled(false);
                    }
                }).setDuration(ANIM_DURATION);


        binding.nestedScrollView.setScrollingEnabled(true);

        firstTime = false;
    }


    @Override
    public void onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack();
    }

    public void redeemConfirmButtonClicked() {
        AnalyticsUtils.logEvent(this.getContext(), "form_step",
                new Pair<>("formName", "Redeem for "+viewModel.getGiftCardItem().getShortName()+" eGift card"),
                new Pair<>("stepName", "Click to redeem"));
        AnalyticsUtils.setCurrentScreenName(this.requireActivity(),"my-petro-points-redeem-info-"+viewModel.getGiftCardItem().getScreenName()+"-redeeming");
        viewModel.sendRedeemData();
    }
}
