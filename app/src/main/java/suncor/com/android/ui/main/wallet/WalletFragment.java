package suncor.com.android.ui.main.wallet;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;

import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentWalletBinding;
import suncor.com.android.ui.main.BottomNavigationFragment;
import suncor.com.android.uicomponents.swiperefreshlayout.SwipeRefreshLayout;

public class WalletFragment extends BottomNavigationFragment implements SwipeRefreshLayout.OnRefreshListener {

    private FragmentWalletBinding binding;
    private WalletPagerAdapter adapter;
    private float appBarElevation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);

        binding.refreshLayout.setColorSchemeResources(R.color.red);
        binding.refreshLayout.setOnRefreshListener(this);

        binding.appBar.setRightButtonOnClickListener((v) -> adapter.tabs[binding.pager.getCurrentItem()].navigateToAddCard());

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new WalletPagerAdapter(getChildFragmentManager(), getContext());
        binding.pager.setAdapter(adapter);

        binding.tabLayout.setupWithViewPager(binding.pager);

        // Default select petro-canada cards
        new Handler().postDelayed(() -> Objects.requireNonNull(binding.tabLayout.getTabAt(0)).select(),100);
    }

    @Override
    public void onRefresh() {
        adapter.tabs[binding.pager.getCurrentItem()].onRefresh();
    }

    public void stopRefresh() {
        binding.refreshLayout.setRefreshing(false);
    }
}