package suncor.com.android.ui.main.carwash;

import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCarWashBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.cards.CardDetail;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.cards.list.CardItemDecorator;
import suncor.com.android.ui.main.cards.list.CardListItem;
import suncor.com.android.ui.main.cards.list.CardsListAdapter;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;

public class CarWashCardFragment extends MainActivityFragment implements OnBackPressedListener,
        SwipeRefreshLayout.OnRefreshListener {

    @Inject
    ViewModelFactory viewModelFactory;
    private FragmentCarWashBinding binding;
    private CarWashCardViewModel viewModel;
    private CardsListAdapter petroCanadaCardsAdapter;
    private float appBarElevation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarWashCardViewModel.class);
        petroCanadaCardsAdapter = new CardsListAdapter(this::cardClick);

        viewModel.getViewState().observe(this, (result) -> {
            if (result != CarWashCardViewModel.ViewState.REFRESHING) {
                binding.refreshLayout.setRefreshing(false);
            }

            if (result != CarWashCardViewModel.ViewState.REFRESHING && result != CarWashCardViewModel.ViewState.LOADING
                    && result != CarWashCardViewModel.ViewState.FAILED && viewModel.getIsCardAvailable().getValue()) {

                ArrayList<CardListItem> petroCanadaCards = new ArrayList<>();
                for (CardDetail cardDetail : viewModel.getPetroCanadaCards().getValue()) {
                    petroCanadaCards.add(new CardListItem(getContext(), cardDetail));
                }
                petroCanadaCardsAdapter.setCards(petroCanadaCards);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCarWashBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.msg_sl005_button,
                () -> viewModel.loadData(CarWashCardViewModel.ViewState.LOADING)));

        binding.scrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    int[] headerLocation = new int[2];
                    int[] appBarLocation = new int[2];

                    binding.carWashWelcomeMessage.getLocationInWindow(headerLocation);
                    binding.appBar.getLocationInWindow(appBarLocation);
                    int appBarBottom = appBarLocation[1] + binding.appBar.getMeasuredHeight();
                    int headerBottom = headerLocation[1] +
                            binding.carWashWelcomeMessage.getMeasuredHeight()
                            - binding.carWashWelcomeMessage.getPaddingBottom();

                    if (headerBottom <= appBarBottom) {
                        binding.appBar.setTitle(binding.carWashWelcomeMessage.getText());
                        ViewCompat.setElevation(binding.appBar, appBarElevation);
                        binding.appBar.findViewById(R.id.collapsed_title).setAlpha(
                                Math.min(1, (float) (appBarBottom - headerBottom) / 100));
                    } else {
                        binding.appBar.setTitle("");
                        ViewCompat.setElevation(binding.appBar, 0);
                    }
                });

        binding.appBar.setNavigationOnClickListener(v -> goBack());
        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        CardItemDecorator listDecorator = new CardItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.carwashWashCards.carWashCardsList.setAdapter(petroCanadaCardsAdapter);
        binding.carwashWashCards.carWashCardsList.addItemDecoration(listDecorator);
        binding.carwashWashCards.carWashCardsList.setNestedScrollingEnabled(false);
        binding.carwashWashCards.carWashCardsList.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Drawable drawable = getActivity().getDrawable(R.drawable.petro_canada_card_background);
                drawable.setBounds(new Rect(0, 0, view.getWidth(), view.getHeight()));
                drawable.getOutline(outline);
            }
        });

        return binding.getRoot();

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onAttached();
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        viewModel.loadData(CarWashCardViewModel.ViewState.REFRESHING);
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }


    private void cardClick(CardDetail cardDetail) {
        //TODO: goto card detail page
    }


}
