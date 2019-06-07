package suncor.com.android.ui.main.profile.transcations;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.TransactionsFragmentBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.common.BaseFragment;
import suncor.com.android.ui.main.profile.transcations.TransactionsFragmentDirections.ActionTransactionsFragmentToTransactionDetailFragment;
import suncor.com.android.uicomponents.DividerItemDecoratorHideLastItem;

import static androidx.navigation.Navigation.findNavController;
import static suncor.com.android.ui.main.profile.transcations.TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDetailFragment;


public class TransactionsFragment extends BaseFragment {

    private TransactionsViewModel mViewModel;
    @Inject
    ViewModelFactory viewModelFactory;
    private TransactionsFragmentBinding binding;

    public static TransactionsFragment newInstance() {
        return new TransactionsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.transactions_fragment, container, false);
        Drawable divider = getResources().getDrawable(R.drawable.horizontal_divider);
        int inset = getResources().getDimensionPixelSize(R.dimen.faq_question_padding);
        InsetDrawable insetDivider = new InsetDrawable(divider, inset, 0, 0, 0);
        DividerItemDecoratorHideLastItem dividerItemDecoration = new DividerItemDecoratorHideLastItem(insetDivider);
        binding.transactionFirstMonth.transactionRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.transactionFirstMonth.transactionRecycler.addItemDecoration(dividerItemDecoration);
        binding.transactionSecondMonth.transactionRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.transactionSecondMonth.transactionRecycler.addItemDecoration(dividerItemDecoration);
        binding.transactionThirdMonth.transactionRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.transactionThirdMonth.transactionRecycler.addItemDecoration(dividerItemDecoration);
        binding.transactionFourthMonth.transactionRecycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        binding.transactionFourthMonth.transactionRecycler.addItemDecoration(dividerItemDecoration);
        binding.setLifecycleOwner(this);
        binding.setVm(mViewModel);
        binding.errorLayout.setModel(new GenericErrorView(getContext(), R.string.transactions_try_again, () -> mViewModel.loadTransactions()));
        return binding.getRoot();
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.black_4);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.transactions.observe(this, transactionsHashMap -> {
            HashMap<Integer, ArrayList<Transaction>> hashMap = transactionsHashMap;
            if (transactionsHashMap != null) {
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(0))) {
                    binding.transactionFirstMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(0)), (this::transactionDetailHandler)));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(1))) {
                    binding.transactionSecondMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(1)), (this::transactionDetailHandler)));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(2))) {
                    binding.transactionThirdMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(2)), (this::transactionDetailHandler)));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(3))) {
                    binding.transactionFourthMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(3)), (this::transactionDetailHandler)));
                }


            }
        });
        mViewModel.transactionsLiveData.observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.ERROR && mViewModel.transactions.getValue() != null) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            }
        });

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(TransactionsViewModel.class);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.transactionToolBar.setNavigationOnClickListener(v -> findNavController(getView()).popBackStack());

    }

    public void transactionDetailHandler(Transaction transaction) {
        ActionTransactionsFragmentToTransactionDetailFragment action = actionTransactionsFragmentToTransactionDetailFragment(transaction);
        findNavController(getView()).navigate(action);

    }

}
