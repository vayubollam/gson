package suncor.com.android.ui.home.profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import javax.inject.Inject;
import afu.org.checkerframework.checker.nullness.qual.Nullable;
import androidx.annotation.NonNull;
import suncor.com.android.databinding.FragmentPersonalInfoBinding;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.home.common.BaseFragment;


public class PersonalInfoFragment extends BaseFragment {

    FragmentPersonalInfoBinding binding;

    @Inject
    SessionManager sessionManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.firstnameInput.setText(sessionManager.getProfile().getFirstName());
        binding.lastnameInput.setText(sessionManager.getProfile().getLastName());
        binding.emailInput.setText(sessionManager.getProfile().getEmail());
        binding.phoneInput.setText(sessionManager.getProfile().getPhone());
        binding.appBar.setNavigationOnClickListener(v -> getActivity().onBackPressed());





    }
}
