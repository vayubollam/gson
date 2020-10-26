package suncor.com.android.ui.main.profile.address;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.model.account.Province;
import suncor.com.android.ui.enrollment.form.ChoiceSelectorAdapter;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.common.MainActivityFragment;
import suncor.com.android.ui.main.profile.ProfileSharedViewModel;
import suncor.com.android.uicomponents.SuncorAppBarLayout;

public class ProvinceProfileFragment extends MainActivityFragment {
    private ProfileSharedViewModel sharedViewModel;
    private ArrayList<String> provinceNames = new ArrayList<>();


    public ProvinceProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_province, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SuncorAppBarLayout appBarLayout = getView().findViewById(R.id.app_bar);
        appBarLayout.setNavigationOnClickListener(v -> {
            Navigation.findNavController(getView()).navigateUp();
        });
        sharedViewModel = ViewModelProviders.of(getActivity()).get(ProfileSharedViewModel.class);
        ArrayList<Province> provinces = ((MainActivity) getActivity()).getProvinces();
        Province selectedProvince = sharedViewModel.getSelectedProvince().getValue();
        int index = -1;

        for (int i = 0; i < provinces.size(); i++) {
            provinceNames.add(provinces.get(i).getName());
            if (provinces.get(i).equals(selectedProvince)) {
                index = i;
            }
        }
        ChoiceSelectorAdapter provinceAdapter = new ChoiceSelectorAdapter(provinceNames, (this::provinceSelected), index);

        RecyclerView province_recycler = getView().findViewById(R.id.province_recycler);
        province_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        province_recycler.setAdapter(provinceAdapter);
    }

    public void provinceSelected(int selectedProvince) {
        sharedViewModel.setSelectedProvince(((MainActivity) getActivity()).getProvinces().get(selectedProvince));
    }

    @Override
    protected String getScreenName() {
        return "province-list";
    }
}
