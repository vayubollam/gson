package suncor.com.android.ui.main.carwash.singleticket;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Pair;
import android.util.TypedValue;
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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentSingleTicketRedeemBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.mfp.ErrorCodes;
import suncor.com.android.model.redeem.response.OrderResponse;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.common.cards.CardFormatUtils;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.utilities.AnalyticsUtils;

import static suncor.com.android.utilities.Constants.CARWASH_TICKET;

public class SingleTicketFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private SingleTicketViewModel viewModel;
    private FragmentSingleTicketRedeemBinding binding;
    private SingleTicketListItemAdapter adapter;
    private Interpolator animInterpolator;
    private final int ANIM_DURATION = 600;
    private Animation animFromBottom;
    private float totalFixY;
    private int marginTop;

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
                //do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int[] totalFix = new int[2];
                binding.termsAgreementDownDivider2.getLocationOnScreen(totalFix);
                totalFixY = totalFix[1];
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //do nothing
            }
        });

        viewModel.orderApiData.observe(this, (orderResponseResource) -> {

            switch (orderResponseResource.status) {
                case SUCCESS:
                    boolean isFromCardsTab = SingleTicketFragmentArgs.fromBundle(getArguments()).getIsFromCardsTab();
                    OrderResponse orderResponse = orderResponseResource.data;
                    if (isFromCardsTab) {
                        SingleTicketFragmentDirections.ActionCarWashPurchaseFragmentToRedeemReceiptFragmentPopToCards action
                                = SingleTicketFragmentDirections.actionCarWashPurchaseFragmentToRedeemReceiptFragmentPopToCards(Objects.requireNonNull(orderResponse), false);
                        action.setIsLinkToAccount(viewModel.isLinkedToAccount());
                        action.setPetroCanadaProduct(viewModel.getSelectedSingleTicketRedeem());
                        if (getView() != null) {
                            getView().postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 1000);
                        }
                    } else {
                        SingleTicketFragmentDirections.ActionCarWashPurchaseFragmentToRedeemReceiptFragmentPopToCarWash action
                                = SingleTicketFragmentDirections.actionCarWashPurchaseFragmentToRedeemReceiptFragmentPopToCarWash(Objects.requireNonNull(orderResponse), false);
                        action.setIsLinkToAccount(viewModel.isLinkedToAccount());
                        action.setPetroCanadaProduct(viewModel.getSelectedSingleTicketRedeem());
                        if (getView() != null) {
                            getView().postDelayed(() -> Navigation.findNavController(getView()).navigate(action), 1000);
                        }
                    }
                    break;
                case ERROR:
                    if (ErrorCodes.ERR_CARD_LOCK.equals(orderResponseResource.message) || ErrorCodes.ERR_SECONDARY_CARD_HOLDER_REDEMPTIONS_DISABLED.equals(orderResponseResource.message)) {
                        String analyticName = getString(R.string.msg_e030_title)+"("+getString(R.string.msg_e030_message)+")";
                        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event._ALERT,
                                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                                        new Pair<>(AnalyticsUtils.Param.FORMNAME, CARWASH_TICKET)
                        );
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.msg_e030_title)
                                .setMessage(R.string.msg_e030_message)
                                .setPositiveButton(R.string.ok, (dialog, which) -> {
                                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                                            new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.msg_e030_message)),
                                            new Pair<>(AnalyticsUtils.Param.FORMNAME, CARWASH_TICKET)
                                    );
                                    dialog.dismiss();
                                })
                                .create()
                                .show();
                    } else {
                        Alerts.prepareGeneralErrorDialog(getActivity(), CARWASH_TICKET).show();
                    }
                    break;
            }
        });

        marginTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_single_ticket_redeem, container, false);
        binding.setEventHandler(this);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.valuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        adapter = new SingleTicketListItemAdapter(viewModel.getTicketItems(), viewModel.getSessionManager().getProfile().getPointsBalance(),
                this::cardValueChanged, getContext());
        binding.valuesRecyclerView.setAdapter(adapter);
        binding.redeemTotalLayoutFix.setAlpha(0f);
        binding.changeValueBtn.setOnClickListener(v -> {
            binding.cardValueTxt.setText(getString(R.string.single_ticket_select_value));
            binding.changeValueBtn.setEnabled(false);
            binding.changeValueBtn.animate().alpha(0.0f).setDuration(ANIM_DURATION).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                }
            });
            shiftUnderneathLayoutDown();
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
        binding.accToAccountHelp.setOnClickListener(addToAccountHelpListener);
        binding.addToAccountCheckbox.setOnCheckedChangeListener((compoundButton, isChecked) -> viewModel.setLinkedToAccount(isChecked));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (viewModel.getIsAnyTicketReedeemable().getValue()) {
            adapter.initialLaunch();
        } else {
            binding.valuesRecyclerView.setPadding(0, 0, 0, marginTop);
        }
    }

    private View.OnClickListener addToAccountHelpListener = view -> {
        String analyticName = getString(R.string.single_ticket_add_to_account_help_title)+"("+getString(R.string.single_ticket_add_to_account_help_message)+")";
        AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event._ALERT,
                new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                new Pair<>(AnalyticsUtils.Param.FORMNAME, CARWASH_TICKET)
        );
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.single_ticket_add_to_account_help_title)
                .setMessage(R.string.single_ticket_add_to_account_help_message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, (dial, which)->{
                    AnalyticsUtils.logEvent(getContext(), AnalyticsUtils.Event.alertInteraction,
                            new Pair<>(AnalyticsUtils.Param.alertTitle, analyticName),
                            new Pair<>(AnalyticsUtils.Param.alertSelection, getString(R.string.ok)),
                            new Pair<>(AnalyticsUtils.Param.FORMNAME, CARWASH_TICKET)
                    );
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    };

    private void cardValueChanged(Integer selectedItem) {
        int valueSelected = viewModel.getTicketItems().get(selectedItem).getPointsPrice();
        int userPetroPoints = viewModel.getSessionManager().getProfile().getPointsBalance();
        viewModel.setSelectedSingleTicketRedeem(viewModel.getTicketItems().get(selectedItem));
        binding.redeemTotalPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(valueSelected)));
        binding.redeemTotalPointsTxt2.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(valueSelected)));
        binding.redeemNewPointsTxt.setText(getString(R.string.rewards_signedin_egift_value_in_pointr_generic, CardFormatUtils.formatBalance(userPetroPoints - valueSelected)));
        binding.cardValueTxt.setText(getString(R.string.single_ticket_quantity_title));
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
        if (binding.redeemTotalLayoutFix.getVisibility() == View.GONE) {
            new Handler().postDelayed(() ->
            {
                binding.redeemTotalLayoutFix.setVisibility(View.VISIBLE);
                binding.redeemTotalLayoutFix.startAnimation(animFromBottom);
            }, ANIM_DURATION);
        }
        moveUnderneathLayoutsUp();
        binding.nestedScrollView.setScrollingEnabled(true);
    }

    private void shiftUnderneathLayoutDown() {
        adapter.showValues();
        binding.valueRecyclerViewDownDivider.animate().alpha(0).start();
        binding.valuesRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.scrollViewGroup);
        constraintSet.clear(R.id.payment_method_layout, ConstraintSet.TOP);
        constraintSet.connect(R.id.payment_method_layout, ConstraintSet.TOP, R.id.values_recycler_view, ConstraintSet.BOTTOM, marginTop);
        constraintSet.connect(R.id.value_recycler_view_down_divider, ConstraintSet.TOP, R.id.values_recycler_view, ConstraintSet.BOTTOM, marginTop);
        constraintSet.applyTo(binding.scrollViewGroup);
        Transition transition = new ChangeBounds();
        transition.setDuration(ANIM_DURATION);
        transition.setInterpolator(animInterpolator);
        TransitionManager.beginDelayedTransition(binding.scrollViewGroup, transition);
    }

    private void moveUnderneathLayoutsUp() {
        binding.valuesRecyclerView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_4));
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.scrollViewGroup);
        constraintSet.clear(R.id.payment_method_layout, ConstraintSet.TOP);
        constraintSet.connect(R.id.payment_method_layout, ConstraintSet.TOP, R.id.values_recycler_view, ConstraintSet.TOP, (int) adapter.getItemHeight() + marginTop);
        constraintSet.connect(R.id.value_recycler_view_down_divider, ConstraintSet.TOP, R.id.values_recycler_view, ConstraintSet.TOP, (int) adapter.getItemHeight());
        constraintSet.applyTo(binding.scrollViewGroup);
        Transition transition = new ChangeBounds();
        transition.setDuration(ANIM_DURATION);
        transition.setInterpolator(animInterpolator);
        TransitionManager.beginDelayedTransition(binding.scrollViewGroup, transition);
        binding.valueRecyclerViewDownDivider.animate().setDuration(ANIM_DURATION).alpha(1).start();
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsUtils.setCurrentScreenName(getActivity(), "carwash-ticket");
    }

    @Override
    public void onBackPressed() {
        Navigation.findNavController(getView()).popBackStack();
    }

    public void redeemConfirmButtonClicked() {
        viewModel.sendRedeemData();
    }

}

