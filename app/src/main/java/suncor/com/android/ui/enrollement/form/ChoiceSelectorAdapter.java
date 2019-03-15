package suncor.com.android.ui.enrollement.form;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.databinding.ProvinceItemBinding;
import suncor.com.android.utilities.Consumer;

public class ChoiceSelectorAdapter extends RecyclerView.Adapter<ChoiceSelectorAdapter.ProvinceHolder> {
    private String[] provinces;
    private int selectedItem;
    private ArrayList<CheckBox> provincesCheckBoxs;
    private Handler handler;
    private Consumer<Integer> callback;


    public ChoiceSelectorAdapter(String[] provinces, Consumer<Integer> callback, int selectedItem) {
        this.provinces = provinces;
        provincesCheckBoxs = new ArrayList<>();
        handler = new Handler();
        this.callback = callback;
        this.selectedItem = selectedItem;
    }


    @NonNull
    @Override
    public ProvinceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProvinceItemBinding binding = ProvinceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProvinceHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProvinceHolder holder, int position) {
        holder.binding.checkProvince.setText(provinces[position]);
        provincesCheckBoxs.add(holder.binding.checkProvince);
        holder.binding.checkProvince.setOnClickListener(v -> {
            if (selectedItem == position) {
                holder.binding.checkProvince.setChecked(true);
            } else if (selectedItem != -1) {
                provincesCheckBoxs.get(selectedItem).setChecked(false);
            }

            callback.accept(position);


            selectedItem = position;
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(holder.itemView).navigateUp();
                }
            }, 300);
        });
        if (position == (provinces.length - 1)) {
            holder.binding.bottomDivider.setVisibility(View.VISIBLE);
            if (selectedItem != -1) {
                provincesCheckBoxs.get(getSelectedItem()).setChecked(true);
            }
        }

    }

    @Override
    public int getItemCount() {
        return provinces.length;
    }

    public class ProvinceHolder extends RecyclerView.ViewHolder {
        ProvinceItemBinding binding;

        public ProvinceHolder(ProvinceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}
