package suncor.com.android.ui.main.rewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import suncor.com.android.databinding.RewardsListItemBinding;

public class RewardsAdapter extends RecyclerView.Adapter<RewardsAdapter.RewardsViewHolder> {

    private Reward[] rewards;

    public RewardsAdapter(Reward[] rewards) {
        this.rewards = rewards;
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
        int imageId = context.getResources().getIdentifier(rewards[position].getLargeImage(), "drawable", context.getPackageName());
        holder.binding.setReward(rewards[position]);
        holder.binding.setImage(context.getDrawable(imageId));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return rewards.length;
    }

    public class RewardsViewHolder extends RecyclerView.ViewHolder {
        private RewardsListItemBinding binding;

        public RewardsViewHolder(@NonNull RewardsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
