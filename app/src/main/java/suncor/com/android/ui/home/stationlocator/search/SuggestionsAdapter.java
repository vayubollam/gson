package suncor.com.android.ui.home.stationlocator.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.PlaceSuggestionItemBinding;
import suncor.com.android.utilities.Consumer;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionyHolder> {
    private ArrayList<PlaceSuggestion> suggestions;
    private Consumer<PlaceSuggestion> callback;

    public SuggestionsAdapter(Consumer<PlaceSuggestion> callback) {
        this.suggestions = new ArrayList<>();
        this.callback = callback;
    }

    public void setSuggestions(ArrayList<PlaceSuggestion> suggestions) {
        this.suggestions.clear();
        this.suggestions.addAll(suggestions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SuggestionyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlaceSuggestionItemBinding suggestionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.place_suggestion_item, parent, false);
        return new SuggestionyHolder(suggestionItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionyHolder holder, int position) {
        final PlaceSuggestion suggestion = suggestions.get(position);
        holder.binding.setPlace(suggestion);
        holder.binding.executePendingBindings();
        holder.binding.getRoot().setOnClickListener(v -> {
            callback.accept(suggestion);
        });
    }


    @Override
    public int getItemCount() {
        return suggestions.size();
    }


    public class SuggestionyHolder extends RecyclerView.ViewHolder {
        PlaceSuggestionItemBinding binding;

        public SuggestionyHolder(@NonNull PlaceSuggestionItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


}
