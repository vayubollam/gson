package suncor.com.android.ui.home.cards;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardsBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.home.common.BaseFragment;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;

public class CardsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentCardsBinding binding;
    private CardsViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;
    private CardsListAdapter petroCanadaCardsAdapter;
    private CardsListAdapter partnerCardsAdapter;
    private float appBarElevation;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CardsViewModel.class);
        petroCanadaCardsAdapter = new CardsListAdapter();
        partnerCardsAdapter = new CardsListAdapter();

        viewModel.viewState.observe(this, (result) -> {
            if (result == CardsViewModel.ViewState.SUCCESS || result == CardsViewModel.ViewState.BALANCE_FAILED) {
                petroCanadaCardsAdapter.setCards(viewModel.getPetroCanadaCards().getValue());
                partnerCardsAdapter.setCards(viewModel.getPartnerCards().getValue());
                binding.refreshLayout.setRefreshing(false);

                if (result == CardsViewModel.ViewState.BALANCE_FAILED) {
                    SuncorToast.makeText(getContext(), R.string.msg_cm003, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCardsBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.setLifecycleOwner(this);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.cards_fragment_try_again, () -> viewModel.retryAgain()));

        ItemDecorator listDecorator = new ItemDecorator(-getResources().getDimensionPixelSize(R.dimen.petro_canada_cards_padding));

        binding.petroCanadaCardsList.setAdapter(petroCanadaCardsAdapter);
        binding.petroCanadaCardsList.addItemDecoration(listDecorator);

        binding.partnerCardsList.setAdapter(partnerCardsAdapter);
        binding.partnerCardsList.addItemDecoration(listDecorator);

        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        binding.scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int[] headerLocation = new int[2];
            int[] appBarLocation = new int[2];

            binding.header.getLocationInWindow(headerLocation);
            binding.appBar.getLocationInWindow(appBarLocation);
            int appBarBottom = appBarLocation[1] + binding.appBar.getMeasuredHeight();
            int headerBottom = headerLocation[1] + binding.header.getMeasuredHeight() - binding.header.getPaddingBottom();

            if (headerBottom <= appBarBottom) {
                binding.appBar.setTitle(binding.header.getText());
                ViewCompat.setElevation(binding.appBar, appBarElevation);
                binding.appBar.findViewById(R.id.collapsed_title).setAlpha(Math.min(1, (float) (appBarBottom - headerBottom) / 100));
            } else {
                binding.appBar.setTitle("");
                ViewCompat.setElevation(binding.appBar, 0);
            }
        });

        return binding.getRoot();
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.black_4);
    }

    @Override
    public void onRefresh() {
        viewModel.refreshBalance();
    }

    private class ItemDecorator extends RecyclerView.ItemDecoration {
        private final int mSpace;

        public ItemDecorator(int space) {
            this.mSpace = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position != 0)
                outRect.top = mSpace;
        }
    }
}
