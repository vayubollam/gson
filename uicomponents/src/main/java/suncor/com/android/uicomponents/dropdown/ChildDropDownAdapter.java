package suncor.com.android.uicomponents.dropdown;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Objects;

import suncor.com.android.uicomponents.databinding.ChildDropDownItemBinding;


public class ChildDropDownAdapter extends RecyclerView.Adapter<ChildDropDownAdapter.ChildDropDownViewHolder> {

    private static final String TAG = ChildDropDownAdapter.class.getSimpleName();

        private  HashMap<String,Integer> childList = new HashMap<>();
        private int selectedPos = 0;
        private final TextView value;
        private ExpandableViewListener listener;

        ChildDropDownAdapter( final HashMap<String,Integer> data, TextView view, ExpandableViewListener listener) {
            this.childList = data;
            this.value = view;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ChildDropDownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChildDropDownItemBinding binding = ChildDropDownItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ChildDropDownViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChildDropDownViewHolder holder, int position) {
            try {
                if(position != childList.size() -1) {
                    holder.binding.title.setText(String.format("$%s", childList.get(String.valueOf(position + 1))));
                } else {
                    holder.binding.inputField.setHint("Other Amounts");
                }
            }catch (NullPointerException ex){
                Log.e(TAG,  "Error on inflating data , " + ex.getMessage());
            }
            holder.binding.title.setVisibility((position == childList.size() -1) ? View.GONE : View.VISIBLE);
            holder.binding.inputField.setVisibility((position == childList.size() -1) ?  View.VISIBLE : View.GONE);
            holder.binding.container.setSelected(selectedPos== position);

            holder.binding.container.setOnClickListener(v -> {
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);
                value.setText((position != childList.size() -1) ? String.format("$%s", childList.get(String.valueOf(position + 1))) : String.format("$%s", "0"));
                if(Objects.nonNull(listener)) {
                    listener.onSelectFuelUpLimit((position != childList.size() - 1) ? String.format("%s", childList.get(String.valueOf(position + 1))) : String.format("%s", "0"));
                }
            });

            holder.binding.inputField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    value.setText(String.format("$%s", editable.toString()));
                    if(Objects.nonNull(listener)) {
                        listener.onSelectFuelUpLimit(String.format("%s", editable.toString()));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return childList.size();
        }


        static class ChildDropDownViewHolder extends RecyclerView.ViewHolder {
            ChildDropDownItemBinding binding;

            ChildDropDownViewHolder(@NonNull ChildDropDownItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
