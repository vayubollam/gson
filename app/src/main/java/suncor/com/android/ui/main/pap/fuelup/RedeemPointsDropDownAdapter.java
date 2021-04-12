package suncor.com.android.ui.main.pap.fuelup;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import suncor.com.android.R;
import suncor.com.android.databinding.FuelUpLimitDropDownItemBinding;
import suncor.com.android.databinding.ManualLimitDropDownItemBinding;
import suncor.com.android.uicomponents.dropdown.ChildViewListener;
import suncor.com.android.uicomponents.dropdown.DropDownAdapter;
import suncor.com.android.utilities.Timber;

public class RedeemPointsDropDownAdapter extends DropDownAdapter {

    private ChildViewListener listener;
    private Context mContext;

    private static final int DROP_DOWN_LAYOUT = 1;
    private static final int MANUAL_DROP_DOWN_LAYOUT = 2;
    public static final String TAG = "RedeemPointsAdapter";
    public HashMap<String, String> redeemPoints = new HashMap<>();
    private int selectedPos;
    private NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

    RedeemPointsDropDownAdapter(final Context context, HashMap<String, String> redeemPoints) {

        this.mContext = context;
        this.redeemPoints = redeemPoints;
    }

    @Override
    public String getSelectedValue() {
        return mContext.getString(R.string.zero_dollar_off);
    }

    @Override
    public String getSelectedSubValue() {
        return mContext.getString(R.string.zero_points);
    }

    @Override
    public void setListener(ChildViewListener listener) {
        this.listener = listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MANUAL_DROP_DOWN_LAYOUT) {
            return new ManualLimitViewHolder(ManualLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        } else {
            return new ChildDropDownViewHolder(FuelUpLimitDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position != redeemPoints.size() - 1) {
            ((ChildDropDownViewHolder) holder).setDataOnView(redeemPoints.get(String.valueOf(position + 1)));
        } else {
            ((ManualLimitViewHolder) holder).setDataOnView(redeemPoints.get(String.valueOf(position + 1)));
        }

    }

    @Override
    public int getItemCount() {
        return redeemPoints.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 2 ? MANUAL_DROP_DOWN_LAYOUT : DROP_DOWN_LAYOUT;
    }

    class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
        FuelUpLimitDropDownItemBinding binding;

        ChildDropDownViewHolder(@NonNull FuelUpLimitDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setDataOnView(String price) {

            try {
                binding.title.setText(price);
            } catch (NullPointerException ex) {
                Timber.e(TAG, "Error on inflating data , " + ex.getMessage());
            }
            binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.container.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                if (Objects.nonNull(listener)) {
//                   listener.onSelectValue(formatter.format(value), null, false);
                    listener.expandCollapse();
                }
            });

        }
    }

    class ManualLimitViewHolder extends RecyclerView.ViewHolder {
        ManualLimitDropDownItemBinding binding;

        ManualLimitViewHolder(@NonNull ManualLimitDropDownItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        public void setDataOnView(String value) {
//           binding.container.setSelected(selectedPos == getAdapterPosition());

            binding.separator.setVisibility(View.GONE);

            binding.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setVisibility(View.GONE);
                    binding.manualLimit.setVisibility(View.VISIBLE);
                    binding.inputField.setEnabled(true);
                    binding.inputField.requestFocus();
                    binding.inputField.setSelection(binding.inputField.getText().length());
                }
            });

            binding.container.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);
                binding.inputField.setVisibility(View.VISIBLE);
                binding.manualLimit.setVisibility(View.VISIBLE);
                binding.prefixCurrency.setText(mContext.getString(suncor.com.android.uicomponents.R.string.currency_dollar));
            });

            binding.inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                    } catch (Exception e) {

                    }
                }
            });

            binding.inputField.setOnKeyListener((view, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {

                }
                return false;
            });
        }
    }
}
