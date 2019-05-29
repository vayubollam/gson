package suncor.com.android.ui.home.profile.transcations;

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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.TransactionsFragmentBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.Resource;
import suncor.com.android.ui.common.Alerts;
import suncor.com.android.ui.home.common.BaseFragment;


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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(insetDivider);
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
        return binding.getRoot();
    }

    @Override
    protected int getStatusBarColor() {
        return getResources().getColor(R.color.black_4);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(TransactionsViewModel.class);
        mViewModel.transactions.observe(this, transactionsHashMap -> {
            if (transactionsHashMap != null) {
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(0)) && transactionsHashMap.keySet().size() == 2) {
                    binding.transactionFirstMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(0))));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(1)) && transactionsHashMap.keySet().size() == 2) {
                    binding.transactionSecondMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(1))));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(2)) && transactionsHashMap.keySet().size() == 3) {
                    binding.transactionThirdMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(2))));
                }
                if (transactionsHashMap.containsKey(mViewModel.getCurrentMonth(3)) && transactionsHashMap.keySet().size() == 4) {
                    binding.transactionFourthMonth.transactionRecycler.setAdapter(new TransactionAdapter(transactionsHashMap.get(mViewModel.getCurrentMonth(3))));
                }


            }
        });
        mViewModel.transactionsLiveData.observe(this, arrayListResource -> {
            if (arrayListResource.status == Resource.Status.ERROR && mViewModel.transactions != null) {
                Alerts.prepareGeneralErrorDialog(getContext()).show();
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.viewPreviousMonthButton.setOnClickListener(v -> mViewModel.loadTransactions());
        binding.transactionToolBar.setNavigationOnClickListener(v -> Navigation.findNavController(getView()).popBackStack());

    }

}
