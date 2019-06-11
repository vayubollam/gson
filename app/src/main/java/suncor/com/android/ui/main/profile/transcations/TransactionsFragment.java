package suncor.com.android.ui.main.profile.transcations;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.TransactionsFragmentBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.model.cards.Transaction;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.common.GenericErrorView;
import suncor.com.android.ui.main.common.BaseFragment;
import suncor.com.android.uicomponents.DividerItemDecoratorHideLastItem;
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
    private TransactionAdapter transactionAdapter1, transactionAdapter2, transactionAdapter3, transactionAdapter4;
    private boolean isExpanded = true;
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.transactions.observe(this, transactionsHashMap -> {
            if (transactionsHashMap != null) {
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(0)) && transactionAdapter1 == null) {
                    transactionAdapter1 = new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(0)), (this::transactionDetailHandler));
                    binding.transactionFirstMonth.transactionRecycler.setAdapter(transactionAdapter1);
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(1)) && transactionAdapter2 == null) {
                    transactionAdapter2 = new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(1)), (this::transactionDetailHandler));
                    binding.transactionSecondMonth.transactionRecycler.setAdapter(transactionAdapter2);
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(2)) && transactionAdapter3 == null) {
                    transactionAdapter3 = new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(2)), (this::transactionDetailHandler));
                    binding.transactionThirdMonth.transactionRecycler.setAdapter(transactionAdapter3);
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(3)) && transactionAdapter4 == null) {
                    transactionAdapter4 = new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(3)), (this::transactionDetailHandler));
                    binding.transactionFourthMonth.transactionRecycler.setAdapter(transactionAdapter4);
                }


            }
        });
        mViewModel.transactionsLiveData.observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.ERROR && mViewModel.transactions.getValue() != null) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            }
        });
        binding.transactionToolBar.post(() -> {
            binding.transactionToolBar.setExpanded(isExpanded, false);
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
        transactionAdapter1 = null;
        transactionAdapter2 = null;
        transactionAdapter3 = null;
        transactionAdapter4 = null;
        isExpanded = binding.transactionToolBar.isExpanded();
        ActionTransactionsFragmentToTransactionDetailFragment action = actionTransactionsFragmentToTransactionDetailFragment(transaction);
        findNavController(getView()).navigate(action);

    }

}
