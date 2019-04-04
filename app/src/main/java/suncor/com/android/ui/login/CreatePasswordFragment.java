package suncor.com.android.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        FragmentCreatePasswordBinding binding = FragmentCreatePasswordBinding.inflate(inflater, container, false);
        binding.setVm(viewModel);
        return binding.getRoot();
    }
}
