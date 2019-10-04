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
import suncor.com.android.model.merchants.EGift;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.MerchantItem;

import static suncor.com.android.ui.common.cards.CardFormatUtils.formatBalance;


public class GiftCardValueConfirmationFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private GiftCardValueConfirmationViewModel viewModel;
    private FragmentGiftCardValueConfirmationBinding binding;
    private MerchantItem merchantItem;
    private GiftCardValueAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 400;
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
                    if (ErrorCodes.ERR_CARD_LOCK.equals(orderResponseResource.message)) {
                        new AlertDialog.Builder(getContext())
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
            binding.changeValueBtn.animate().alpha(0.0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    binding.changeValueBtn.setEnabled(false);
                }
            });
            adapter.showValues();
            binding.redeemAddressLayout.animate()
                    .translationY(-(binding.redeemAddressLayout.getTranslationY() + adapter.getItemHeight() * (viewModel.getMerchantItem().getMerchant().geteGifts().size() - 1)))
                    .setInterpolator(animInterpolator)
                    .setStartDelay(0)
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
        final int ANIM_DURATION = 400;
        int valueSelected = viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem).getPetroPointsRequired();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        viewModel.setEGift(viewModel.getMerchantItem().getMerchant().geteGifts().get(selectedItem));
        binding.redeemTotalPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, formatBalance(valueSelected)));
        binding.redeemNewPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, formatBalance(userPetroPoints - valueSelected)));
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
        if (binding.redeemTotalLayout.getVisibility() == View.GONE) {
            new Handler().postDelayed(() -> {
                binding.redeemTotalLayout.setVisibility(View.VISIBLE);
                binding.redeemTotalLayout.startAnimation(animFromBottom);
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
