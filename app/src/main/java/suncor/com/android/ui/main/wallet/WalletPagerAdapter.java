package suncor.com.android.ui.main.wallet;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import suncor.com.android.ui.main.wallet.cards.list.CardsFragment;
import suncor.com.android.ui.main.wallet.payments.list.PaymentsFragment;

public class WalletPagerAdapter extends FragmentPagerAdapter {
    private static class LazyCardsFragment {
        static final CardsFragment INSTANCE = new CardsFragment();
    }

    private static CardsFragment cardsFragment() {
        return LazyCardsFragment.INSTANCE;
    }

    private static class LazyPaymentsFragment {
        static final PaymentsFragment INSTANCE = new PaymentsFragment();
    }

    private static PaymentsFragment paymentsFragment() {
        return LazyPaymentsFragment.INSTANCE;
    }

    WalletTabInterface[] tabs = new WalletTabInterface[]{ cardsFragment(), paymentsFragment() };

    private Context context;

    WalletPagerAdapter(FragmentManager fm, Context context) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        return (Fragment) tabs[i];
    }

    @Override
    public int getCount() {
        return tabs.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs[position].getTabName(context);
    }
}
