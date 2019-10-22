package suncor.com.android.ui.main.rewards.redeem;

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
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import suncor.com.android.ui.main.rewards.MerchantItem;


public class GiftCardValueConfirmationFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private GiftCardValueConfirmationViewModel viewModel;
    private FragmentGiftCardValueConfirmationBinding binding;
    private MerchantItem merchantItem;
    private GiftCardValueAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 600;
    private Animation animFromBottom;
    private boolean firstTime = true;

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

        viewModel.orderApiData.observe(this, (orderResponseResource) -> {

            switch (orderResponseResource.status) {
                case SUCCESS:
                    OrderResponse orderResponse = orderResponseResource.data;

                    GiftCardValueConfirmationFragmentDirections.ActionGiftCardValueConfirmationToRedeemReceiptFragment action =
                            GiftCardValueConfirmationFragmentDirections.actionGiftCardValueConfirmationToRedeemReceiptFragment(orderResponse);

                    if (getView() != null) {
                        getView().postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 1000);
                    }
                    break;
                case ERROR:
                    if (ErrorCodes.ERR_CARD_LOCK.equals(orderResponseResource.message) || ErrorCodes.ERR_SECONDARY_CARD_HOLDER_REDEMPTIONS_DISABLED.equals(orderResponseResource.message)) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.msg_e030_title)
                                .setMessage(R.string.msg_e030_message)
                                .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                                .create()
                                .show();
                    } else {
                        Alerts.prepareGeneralErrorDialog(getActivity()).show();
                    }
                    break;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        merchantItem = GiftCardValueConfirmationFragmentArgs.fromBundle(getArguments()).getMerchanItem();
        viewModel.setMerchantItem(merchantItem);

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_gift_card_value_confirmation, container, false);
        binding.setEventHandler(this);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.valuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new GiftCardValueAdapter(viewModel.getMerchantItem().getMerchant().geteGifts(), viewModel.getSessionManager().getProfile().getPointsBalance(), this::cardValueChanged);
        binding.valuesRecyclerView.setAdapter(adapter);
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
                    .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getMerchantItem().getMerchant().geteGifts().size() - 1)))
                    .setInterpolator(animInterpolator)
                    .setStartDelay(0)
                    .setDuration(ANIM_DURATION);

        });

        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (binding.redeemTotalLayoutDown.getVisibility() == View.GONE) {
                return;
            }


            int[] addressLocation = new int[2];
            int[] totalUpLocation = new int[2];
            int[] totalDownLocation = new int[2];

            binding.addressLayoutDownDivider.getLocationOnScreen(addressLocation);
            binding.redeemTotalLayoutUp.getLocationOnScreen(totalUpLocation);
            binding.redeemTotalLayoutDown.getLocationOnScreen(totalDownLocation);
            float address = addressLocation[1];
            float totaldown = totalDownLocation[1];
            float totalup = totalUpLocation[1];
            if (scrollY < oldScrollY) {
                //scrolling down
                if (address > totaldown) {
                    //    binding.redeemTotalLayoutDown.setTranslationY(-binding.nestedScrollView.computeVerticalScrollOffset());
                    binding.redeemTotalLayoutDown.setVisibility(View.VISIBLE);
                    binding.redeemTotalLayoutUp.setVisibility(View.INVISIBLE);
                    binding.redeemTotalLayoutUp.setElevation(-1);
                }
            } else {
                if (address < totaldown && totaldown >= totalup) {

                    //  binding.redeemTotalLayoutDown.setTranslationY(-(scrollY-oldScrollY));
                    binding.redeemTotalLayoutDown.setVisibility(View.INVISIBLE);
                    binding.redeemTotalLayoutUp.setVisibility(View.VISIBLE);
                    binding.redeemTotalLayoutUp.setElevation(1);
                }
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.getMerchantItem().getMerchant().geteGifts().get(0).getPetroPointsRequired() > viewModel.getSessionManager().getProfile().getPointsBalance()) {
            binding.notEnoughPointLayout.setVisibility(View.VISIBLE);
        }

    }

    void cardValueChanged(Integer selectedItem) {
        int valueSelected = viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem).getPetroPointsRequired();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        viewModel.setEGift(viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem));
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
        if (binding.redeemTotalLayoutDown.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.redeemTotalLayoutDown.setVisibility(View.VISIBLE);
                binding.redeemTotalLayoutDown.startAnimation(animFromBottom);
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
                .translationY(-adapter.getItemHeight() * (viewModel.getMerchantItem().getMerchant().geteGifts().size() - 1))
                .setStartDelay(firstTime ? ANIM_DURATION : 0)
                .setInterpolator(animInterpolator)
                .setDuration(ANIM_DURATION);
        firstTime = false;
    }


    @Override
    public void onBackPressed() {
        Navigation.findNavController(getView()).popBackStack();
    }

    public void redeemConfirmButtonClicked() {
        viewModel.sendRedeemData();
    }
}
