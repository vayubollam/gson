package suncor.com.android.ui.main.profile.transcations;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentTransactionDetailBinding;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.model.cards.TransactionDetail;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class TransactionDetailFragment extends MainActivityFragment {

    private FragmentTransactionDetailBinding binding;

    public TransactionDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction_detail, container, false);
        Transaction transaction = TransactionDetailFragmentArgs.fromBundle(getArguments()).getTransaction();
        binding.setTd(new TransactionDetail(transaction, getContext()));
        binding.setLifecycleOwner(this);
        binding.transactionToolbar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());
        return binding.getRoot();
    }

    @Override
    protected String getScreenName() {
        return "my-petro-points-account-transaction-detail";
    }
}
