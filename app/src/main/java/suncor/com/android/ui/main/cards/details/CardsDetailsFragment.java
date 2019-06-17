package suncor.com.android.ui.main.cards.details;

import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsDetailsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.main.common.BaseFragment;

public class CardsDetailsFragment extends BaseFragment {
    private FragmentCardsDetailsBinding binding;
    CardDetailsViewModel viewModel;
    private int clickedCardIndex;
    @Inject
    ViewModelFactory viewModelFactory;
    private float previousBrightness;
    private CardsDetailsAdapter cardsDetailsAdapter;
    private ObservableBoolean isRemoving = new ObservableBoolean(false);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clickedCardIndex = CardsDetailsFragmentArgs.fromBundle(getArguments()).getCardIndex();
        boolean loadCardFromProfile = CardsDetailsFragmentArgs.fromBundle(getArguments()).getIsCardFromProfile();
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardDetailsViewModel.class);
        viewModel.setCardFromProfile(loadCardFromProfile);
        viewModel.retrieveCards();
        viewModel.cards.observe(this, arrayListResource -> {
            ArrayList<ExpandedCardItem> expandedCardItems = new ArrayList<>();
            for (CardDetail cardDetail : arrayListResource) {
                expandedCardItems.add(new ExpandedCardItem(getContext(), cardDetail));
            }
            if (expandedCardItems.size() > 0) {
                cardsDetailsAdapter.setCardItems(expandedCardItems);
                cardsDetailsAdapter.notifyDataSetChanged();
                binding.cardDetailRecycler.scrollToPosition(clickedCardIndex);
                binding.setNumCards(expandedCardItems.size());
                binding.executePendingBindings();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsDetailsBinding.inflate(inflater, container, false);
        binding.cardDetailRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.cardDetailRecycler.setItemAnimator(new Animator());
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.cardDetailRecycler);
        cardsDetailsAdapter = new CardsDetailsAdapter(this::cardViewMoreHandler);
        binding.cardDetailRecycler.setAdapter(cardsDetailsAdapter);
        binding.pageIndicator.attachToRecyclerView(binding.cardDetailRecycler, pagerSnapHelper);
        cardsDetailsAdapter.registerAdapterDataObserver(binding.pageIndicator.getAdapterDataObserver());
        binding.buttonClose.setOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        binding.setIsRemoving(isRemoving);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        previousBrightness = attributes.screenBrightness;
        attributes.screenBrightness = 1f;
        getActivity().getWindow().setAttributes(attributes);
    }

    @Override
    public void onStop() {
        super.onStop();
        WindowManager.LayoutParams attributes = getActivity().getWindow().getAttributes();
        attributes.screenBrightness = previousBrightness;
        getActivity().getWindow().setAttributes(attributes);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    void cardViewMoreHandler(ExpandedCardItem expandedCardItem) {
        RemoveCardBottomSheet removeCardBottomSheet = new RemoveCardBottomSheet();
        removeCardBottomSheet.setClickListener(v -> {
            removeCardBottomSheet.dismiss();
            showConfirmationAlert(expandedCardItem);
        });
        removeCardBottomSheet.show(getFragmentManager(), RemoveCardBottomSheet.TAG);
    }

    private void showConfirmationAlert(ExpandedCardItem expandedCardItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.cards_remove_card_alert_title)).setMessage(getResources().getString(R.string.cards_remove_card_alert_message))
                .setPositiveButton(getResources().getString(R.string.cards_remove_card_alert_remove), (dialog, which) -> {
                    viewModel.deleteCard(expandedCardItem.getCardDetail()).observe(this, cardDetailResource -> {
                        if (cardDetailResource.status == Resource.Status.ERROR) {
                            isRemoving.set(false);
                            Alerts.prepareGeneralErrorDialog(getContext()).show();
                        } else if (cardDetailResource.status == Resource.Status.SUCCESS) {
                            isRemoving.set(false);
                            new Handler().postDelayed(() -> {
                                cardsDetailsAdapter.removeCard(new ExpandedCardItem(getContext(), cardDetailResource.data));
                            }, 200);
                        } else if (cardDetailResource.status == Resource.Status.LOADING) {
                            isRemoving.set(true);
                        }
                    });
                }).setNegativeButton(getResources().getString(R.string.cards_remove_card_alert_cancel), null);
        builder.show();
    }

    public class Animator extends DefaultItemAnimator {
        @Override
        public boolean animateRemove(RecyclerView.ViewHolder holder) {
            View view = holder.itemView;
            ViewPropertyAnimator propertyAnimator = view.animate();
            propertyAnimator.setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    propertyAnimator.setListener(null);
                    propertyAnimator.scaleX(0);
                    propertyAnimator.scaleY(0);
                    dispatchRemoveFinished(holder);
                }

                @Override
                public void onAnimationStart(android.animation.Animator animation) {
                    dispatchRemoveStarting(holder);
                }
            }).start();
            return true;
        }
    }


}
