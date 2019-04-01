package suncor.com.android.ui.enrollment.cardform;


import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import suncor.com.android.R;
import suncor.com.android.databinding.FragmentCardFormBinding;
import suncor.com.android.ui.common.input.CardNumberFormattingTextWatcher;
import suncor.com.android.ui.common.input.PostalCodeFormattingTextWatcher;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFormFragment extends Fragment {


    private CardFormViewModel viewModel;
    private boolean isKeyboardShown;
    private float appBarElevation;

    public CardFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(CardFormViewModel.class);
        appBarElevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentCardFormBinding binding = FragmentCardFormBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener((v) -> {
            Navigation.findNavController(getView()).navigateUp();
        });
        binding.scrollView.setOnScrollChangeListener((View.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY > binding.appBar.getBottom()) {
                binding.appBar.setTitle(getString(R.string.enrollment_cardform_header));
                ViewCompat.setElevation(binding.appBar, appBarElevation);
            } else {
                binding.appBar.setTitle("");
                ViewCompat.setElevation(binding.appBar, 0);
            }
        });

        binding.postalcodeInput.getEditText().addTextChangedListener(new PostalCodeFormattingTextWatcher());
        binding.cardInput.getEditText().addTextChangedListener(new CardNumberFormattingTextWatcher(binding.cardInput.getEditText()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }
}
