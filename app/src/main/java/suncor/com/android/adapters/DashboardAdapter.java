package suncor.com.android.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;


public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardHolder> {

    private ArrayList<Drawable> images;
    private Context context;
    private LayoutInflater layoutInflater;


    public DashboardAdapter(Context context) {
        this.context=context;
        layoutInflater=LayoutInflater.from(context);
        images=new ArrayList<>();
        images.add(context.getResources().getDrawable(R.drawable.car_trip));
        images.add(context.getResources().getDrawable(R.drawable.agriculture));
        images.add(context.getResources().getDrawable(R.drawable.petro_card));
    }

    @NonNull
    @Override
    public DashboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = layoutInflater.inflate(R.layout.dashboard_card, parent, false);
        return new DashboardHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardHolder holder, int position) {
        holder.img_card.setImageDrawable(images.get(position));

    }
    @Override
    public int getItemCount() {
        return images.size();
    }

    public class DashboardHolder extends RecyclerView.ViewHolder {

        private AppCompatImageView img_card;
        private AppCompatTextView card_welcome;
        private MaterialButton join,sign_in;
        public DashboardHolder(@NonNull View itemView) {
            super(itemView);
            img_card=itemView.findViewById(R.id.img_dashboard);
            card_welcome=itemView.findViewById(R.id.card_welcome);
            Typeface tfGibsonGold=ResourcesCompat.getFont(context,R.font.gibson_bold);
            Typeface tfGibsonSemiBold=ResourcesCompat.getFont(context,R.font.gibson_semibold);
            card_welcome.setTypeface(tfGibsonGold);
            join=itemView.findViewById(R.id.card_dashboard_join);
            sign_in=itemView.findViewById(R.id.card_dashboard_Sign_In);
            join.setTypeface(tfGibsonSemiBold);
            sign_in.setTypeface(tfGibsonSemiBold);
        }
    }

}
