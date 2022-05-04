package suncor.com.android.ui.enrollment.form;


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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import suncor.com.android.R;
import suncor.com.android.analytics.enrollment.EnrollmentAnalytics;
import suncor.com.android.model.account.Province;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.uicomponents.SuncorAppBarLayout;

public class ProvinceFragment extends DialogFragment {
    private EnrollmentFormViewModel enrollmentFormViewModel;
    private ArrayList<String> provinceNames = new ArrayList<>();
    private Timer timer;


    public ProvinceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                EnrollmentAnalytics.logTimer30Event(getContext());
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 30000);
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
        ArrayList<Province> provinces = ((EnrollmentActivity) getActivity()).getProvinces();
        Province selectedProvince = enrollmentFormViewModel.getSelectedProvince();
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

    @Override
    public void onResume() {
        super.onResume();
        EnrollmentAnalytics.logScreenNameClass(requireActivity(),EnrollmentAnalytics.SCREEN_NAME_PROVINCE_SECURITY_HELP);
    }

    public void provinceSelected(int selectedProvince) {
        enrollmentFormViewModel.setSelectedProvince(((EnrollmentActivity) getActivity()).getProvinces().get(selectedProvince));
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

}
