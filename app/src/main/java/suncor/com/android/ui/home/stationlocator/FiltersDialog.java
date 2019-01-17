package suncor.com.android.ui.home.stationlocator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import suncor.com.android.R;
import suncor.com.android.ui.common.FullScreenDialog;

public class FiltersDialog extends FullScreenDialog {


    ListView servicesListView;
    ListView fuelListView;
    ListView carwashListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filters_fragment, container, false);
        servicesListView = rootView.findViewById(R.id.service_amenities_list);
        servicesListView.setAdapter(new ArrayAdapter<>(getContext(), R.layout.filter_item, getResources().getStringArray(R.array.station_services_values)));
        fuelListView = rootView.findViewById(R.id.fuel_amenities_list);
        carwashListView = rootView.findViewById(R.id.carwash_amenities_listview);
        return rootView;
    }
}
