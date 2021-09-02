package suncor.com.android.ui.main.rewards.thirdpartygiftcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.databinding.FragmentMoreEGiftCardCategoriesBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardSubCategory;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;

public class MoreEGiftCardCategoriesFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private MoreEGiftCardCategoriesViewModel viewModel;
    private FragmentMoreEGiftCardCategoriesBinding binding;
    private MoreEGiftCArdCategoriesAdapter adapter;
    private List<ThirdPartyGiftCardSubCategory> categoriesList = new ArrayList<>();
    private List<ThirdPartyGiftCardCategory> newCategoryList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, factory).get(MoreEGiftCardCategoriesViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding = FragmentMoreEGiftCardCategoriesBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);

        binding.appBar.setNavigationOnClickListener(v -> Navigation.findNavController(requireView()).popBackStack());

        newCategoryList = new ArrayList<>(Arrays.asList(viewModel.getRewards()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        adapter = new MoreEGiftCArdCategoriesAdapter(requireActivity(), newCategoryList);

        binding.categoriesRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack();
    }
}
