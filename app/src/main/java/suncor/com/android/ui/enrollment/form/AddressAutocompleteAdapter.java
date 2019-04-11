package suncor.com.android.ui.enrollment.form;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.databinding.CanadaPostSuggestionItemBinding;
import suncor.com.android.model.canadapost.CanadaPostSuggestion;
import suncor.com.android.utilities.Consumer;

public class AddressAutocompleteAdapter extends RecyclerView.Adapter<AddressAutocompleteAdapter.AddressAutocompleteViewHolder> {

    private ArrayList<CanadaPostSuggestion> suggestions = new ArrayList<>();

    private Consumer<CanadaPostSuggestion> clickCallback;

    public AddressAutocompleteAdapter(Consumer<CanadaPostSuggestion> clickCallback) {
        this.clickCallback = clickCallback;
    }

    @NonNull
    @Override
    public AddressAutocompleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CanadaPostSuggestionItemBinding binding = CanadaPostSuggestionItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AddressAutocompleteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAutocompleteViewHolder holder, int position) {
        holder.binding.setPlace(suggestions.get(position));
        holder.binding.getRoot().setOnClickListener((v) -> clickCallback.accept(suggestions.get(position)));
    }

    public void setSuggestions(ArrayList<CanadaPostSuggestion> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        notifyDataSetChanged();
    }

    public void setSuggestions(CanadaPostSuggestion[] suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(Arrays.asList(suggestions));
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public static class AddressAutocompleteViewHolder extends RecyclerView.ViewHolder {

        private CanadaPostSuggestionItemBinding binding;

        public AddressAutocompleteViewHolder(CanadaPostSuggestionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
