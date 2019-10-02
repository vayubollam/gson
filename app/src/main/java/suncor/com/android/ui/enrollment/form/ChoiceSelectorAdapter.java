package suncor.com.android.ui.enrollment.form;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.databinding.ProvinceItemBinding;
import suncor.com.android.utilities.Consumer;

public class ChoiceSelectorAdapter extends RecyclerView.Adapter<ChoiceSelectorAdapter.ProvinceHolder> {
    private ArrayList<String> provinces;
    private int selectedItem;
    private Handler handler;
    private Consumer<Integer> callback;


    public ChoiceSelectorAdapter(ArrayList<String> provinces, Consumer<Integer> callback, int selectedItem) {
        this.provinces = provinces;
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
        holder.binding.checkProvince.setText(provinces.get(position));
        holder.binding.checkProvince.setChecked(position == selectedItem);
        holder.binding.checkProvince.setOnClickListener(v -> {
            if (selectedItem == position) {
                holder.binding.checkProvince.setChecked(true);
            } else {
                int previousItem = selectedItem;
                selectedItem = position;
                if (previousItem != -1) {
                    notifyItemChanged(previousItem);
                }
            }
            callback.accept(position);
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Navigation.findNavController(holder.itemView).navigateUp();
                }
            }, 300);
        });
        if (position == (provinces.size() - 1)) {
            holder.binding.bottomDivider.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return provinces.size();
    }

    public class ProvinceHolder extends RecyclerView.ViewHolder {
        ProvinceItemBinding binding;

        public ProvinceHolder(ProvinceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}
