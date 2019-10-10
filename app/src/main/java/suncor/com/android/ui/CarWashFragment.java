package suncor.com.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import suncor.com.android.R;
import suncor.com.android.ui.common.OnBackPressedListener;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.uicomponents.SuncorAppBarLayout;

//TODO: placeholder for now, will change to data binding later
public class CarWashFragment extends MainActivityFragment implements OnBackPressedListener {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_wash, container, false);
        SuncorAppBarLayout suncorAppBarLayout = view.findViewById(R.id.app_bar);
        suncorAppBarLayout.setNavigationOnClickListener(v -> goBack());
        return view;

    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        Navigation.findNavController(getView()).popBackStack();
    }
}
