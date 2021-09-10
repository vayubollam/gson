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
import java.util.List;

import javax.inject.Inject;

import suncor.com.android.R;
import suncor.com.android.databinding.FragmentMoreEGiftCardCategoriesBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardCategory;
import suncor.com.android.model.thirdpartycard.ThirdPartyGiftCardSubCategory;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.rewards.redeem.GenericEGiftCard;

public class MoreEGiftCardCategoriesFragment extends MainActivityFragment implements OnBackPressedListener {

    @Inject
    ViewModelFactory factory;
    private MoreEGiftCardCategoriesViewModel viewModel;
    private FragmentMoreEGiftCardCategoriesBinding binding;
    private MoreEGiftCArdCategoriesAdapter adapter;
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

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        newCategoryList.clear();
        newCategoryList.addAll(viewModel.getRewards());
        adapter = new MoreEGiftCArdCategoriesAdapter(requireActivity(), newCategoryList, this::onCardClicked);
        binding.categoriesRecyclerView.setAdapter(adapter);

    }

    private void onCardClicked(ThirdPartyGiftCardSubCategory subCategory) {

        GenericEGiftCard genericEGiftCard = new GenericEGiftCard();
        genericEGiftCard.setTitle(subCategory.getSubcategoryName());
        genericEGiftCard.setSmallImage(subCategory.getSmallIcon());
        genericEGiftCard.setLargeImage(subCategory.getLargeIcon());
        genericEGiftCard.setSubtitle(getResources().getString(R.string.rewards_egift_card_subtitle));
        genericEGiftCard.setHowToRedeem(subCategory.getHowToRedeem());
        genericEGiftCard.setHowToUse(subCategory.getHowToUse());
        genericEGiftCard.setPoints(getResources().getString(R.string.rewards_e_gift_card_starting_points));

        MoreEGiftCardCategoriesFragmentDirections.ActionMoreEGiftCardCategoriesToMerchantDetailsFragment action = MoreEGiftCardCategoriesFragmentDirections.actionMoreEGiftCardCategoriesToMerchantDetailsFragment(genericEGiftCard);
        Navigation.findNavController(requireView()).navigate(action);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        Navigation.findNavController(requireView()).popBackStack();
    }
}
