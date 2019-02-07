package suncor.com.android.ui.home.profile;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.mfp.SessionManager;
import suncor.com.android.ui.home.HomeActivity;
import suncor.com.android.ui.home.common.BaseFragment;


public class ProfileFragment extends BaseFragment {

    public static String PROFILE_FRAGMENT_TAG = "profile";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.signout_button).setOnClickListener((v) -> {
            SessionManager sessionManager = SessionManager.getInstance();
            sessionManager.logout();
            ((HomeActivity) getActivity()).openFragment(R.id.menu_home);
        });
    }
}
