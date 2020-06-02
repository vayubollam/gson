package suncor.com.android.ui.main.wallet;

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

    WalletPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        return i == 0 ? cardsFragment() : paymentsFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Petro-Canada Cards" : "Credit Cards";
    }
}
