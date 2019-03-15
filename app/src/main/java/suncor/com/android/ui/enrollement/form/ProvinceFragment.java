package suncor.com.android.ui.enrollement.form;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.uicomponents.SuncorAppBarLayout;

public class ProvinceFragment extends DialogFragment {
    private EnrollmentFormViewModel enrollmentFormViewModel;
    private String[] provinceNames;


    public ProvinceFragment() {
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
        enrollmentFormViewModel = ViewModelProviders.of(getActivity()).get(EnrollmentFormViewModel.class);
        ChoiceSelectorAdapter provinceAdapter;
        provinceNames = getResources().getStringArray(R.array.province_names);
        if (enrollmentFormViewModel.selectedProvince.getValue() != -1) {
            provinceAdapter = new ChoiceSelectorAdapter(provinceNames, (this::provinceSelected), enrollmentFormViewModel.selectedProvince.getValue());
        } else {
            provinceAdapter = new ChoiceSelectorAdapter(provinceNames, (this::provinceSelected), -1);
        }
        RecyclerView province_recycler = getView().findViewById(R.id.province_recycler);
        province_recycler.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        province_recycler.setAdapter(provinceAdapter);
    }

    public void provinceSelected(int selectedProvince) {
        enrollmentFormViewModel.selectedProvince.setValue(selectedProvince);
        enrollmentFormViewModel.getProvinceField().setText(provinceNames[selectedProvince]);
    }


}
