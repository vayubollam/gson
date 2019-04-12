package suncor.com.android.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerFragment;
import suncor.com.android.databinding.FragmentCreatePasswordBinding;
import suncor.com.android.di.viewmodel.ViewModelFactory;

public class CreatePasswordFragment extends DaggerFragment {

    @Inject
    ViewModelFactory viewModelFactory;
    private CreatePasswordViewModel viewModel;
    private FragmentCreatePasswordBinding binding;

    public CreatePasswordFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CreatePasswordViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreatePasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        binding.appBar.setNavigationOnClickListener((v) -> {
            getFragmentManager().popBackStack();
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.createButton.setOnClickListener((v) -> {
            Toast.makeText(getContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        });
    }
}
