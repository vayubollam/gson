package suncor.com.android.ui.main.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.databinding.RewardsListItemBinding;
import suncor.com.android.utilities.Consumer;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder> {

    private final Consumer<Reward> clickListener;
    private ArrayList<Reward> rewards;

    public RewardsAdapter(ArrayList<Reward> rewards, Consumer<Reward> clickListener) {
        this.rewards = rewards;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RewardsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RewardsListItemBinding binding = RewardsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RewardsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RewardsViewHolder holder, int position) {
        Context context = holder.binding.getRoot().getContext();
        int imageId = context.getResources().getIdentifier(rewards.get(position).getLargeImage(), "drawable", context.getPackageName());
//        holder.binding.setGiftCard(rewards.get(position));
        holder.binding.setImage(context.getDrawable(imageId));
        holder.binding.executePendingBindings();

        holder.itemView.setOnClickListener(v -> clickListener.accept(rewards.get(position)));
    }

    @Override
    public int getItemCount() {
        return rewards.size();
    }

    public class RewardsViewHolder extends RecyclerView.ViewHolder {
        private RewardsListItemBinding binding;

        public RewardsViewHolder(@NonNull RewardsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }
}
