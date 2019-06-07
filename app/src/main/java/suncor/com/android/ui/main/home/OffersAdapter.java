package suncor.com.android.ui.main.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.OffersCardItemBinding;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    private ArrayList<OfferCard> offerCards;


    public OffersAdapter(Activity activity, boolean isSignedIn) {
        offerCards = new ArrayList<>();
        if (!isSignedIn) {
            OfferCard banner1 = new OfferCard(activity.getString(R.string.offers_banner_1_text),
                    activity.getDrawable(R.drawable.banner_1_signin),
                    new OfferCard.OfferButton(activity.getString(R.string.join), () -> {
                        activity.startActivity(new Intent(activity, EnrollmentActivity.class));
                    }),
                    new OfferCard.OfferButton(activity.getString(R.string.sign_in), () -> {
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }));
            offerCards.add(banner1);
        }

        OfferCard banner2 = new OfferCard(activity.getString(R.string.offers_banner_2_text),
                activity.getDrawable(R.drawable.banner_2_brand_vik),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_2_button),
                        activity.getDrawable(R.drawable.ic_play_video),
                        () -> {
                            //TODO
                        }
                ));
        offerCards.add(banner2);

        OfferCard banner3 = new OfferCard(activity.getString(R.string.offers_banner_3_text),
                activity.getDrawable(R.drawable.banner_3_ppts),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_3_button),
                        () -> {
                            //TODO
                        }
                ));
        offerCards.add(banner3);

        OfferCard banner4 = new OfferCard(activity.getString(R.string.offers_banner_4_text),
                activity.getDrawable(R.drawable.banner_4_ev),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_4_button),
                        activity.getDrawable(R.drawable.ic_play_video),
                        () -> {
                            //TODO
                        }
                ));
        offerCards.add(banner4);

        OfferCard banner5 = new OfferCard(activity.getString(R.string.offers_banner_5_text),
                activity.getDrawable(R.drawable.banner_5_rbc),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_5_button),
                        () -> {
                            //TODO
                        }
                ));
        offerCards.add(banner5);

    }

    @NonNull
    @Override
    public OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OffersCardItemBinding binding = OffersCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new OffersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OffersViewHolder holder, int position) {
        OffersCardItemBinding binding = holder.binding;
        OfferCard card = offerCards.get(position);
        binding.setItem(card);
        binding.executePendingBindings();
        binding.bannerImage.post(() -> {
            Matrix matrix = binding.bannerImage.getImageMatrix();
            float scaleFactor = binding.bannerImage.getWidth() / (float) card.getImage().getIntrinsicWidth();
            matrix.setScale(scaleFactor, scaleFactor, 0, 0);
            binding.bannerImage.setImageMatrix(matrix);
        });
    }

    @Override
    public int getItemCount() {
        return offerCards.size();
    }

    public class OffersViewHolder extends RecyclerView.ViewHolder {

        OffersCardItemBinding binding;

        public OffersViewHolder(OffersCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
