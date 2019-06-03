package suncor.com.android.ui.main.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.DashboardHolder> {

    private ArrayList<Drawable> images;
    private HomeViewModel homeViewModel;


    public OffersAdapter(Activity activity, HomeViewModel viewModel) {
        images = new ArrayList<>();
        images.add(activity.getResources().getDrawable(R.drawable.car_trip));
        images.add(activity.getResources().getDrawable(R.drawable.agriculture));
        images.add(activity.getResources().getDrawable(R.drawable.petro_card));
        this.homeViewModel = viewModel;
    }

    @NonNull
    @Override
    public DashboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_card, parent, false);
        return new DashboardHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardHolder holder, int position) {
        holder.img_card.setImageDrawable(images.get(position));
        if (homeViewModel.isUserLoggedIn()) {
            holder.sign_in.setVisibility(View.GONE);
            holder.join.setVisibility(View.GONE);
        } else {
            holder.sign_in.setVisibility(View.VISIBLE);
            holder.join.setVisibility(View.VISIBLE);
            holder.sign_in.setOnClickListener(v -> {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), LoginActivity.class));
            });
            holder.join.setOnClickListener(v -> {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), EnrollmentActivity.class));
            });
        }

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class DashboardHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView img_card;
        private AppCompatTextView card_welcome;
        private MaterialButton join, sign_in;

        public DashboardHolder(@NonNull View itemView) {
            super(itemView);
            img_card = itemView.findViewById(R.id.img_dashboard);
            card_welcome = itemView.findViewById(R.id.card_welcome);
            join = itemView.findViewById(R.id.card_dashboard_join);
            sign_in = itemView.findViewById(R.id.card_dashboard_Sign_In);
        }
    }

}
