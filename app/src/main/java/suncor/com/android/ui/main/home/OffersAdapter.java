package suncor.com.android.ui.main.home;

import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.R;
import suncor.com.android.databinding.OffersCardItemBinding;
import suncor.com.android.ui.common.SuncorToast;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.MainActivity;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    private ArrayList<OfferCard> offerCards;


    public OffersAdapter(MainActivity activity, boolean isSignedIn) {
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
                        }
                ));
        offerCards.add(banner2);

        OfferCard banner3 = new OfferCard(activity.getString(R.string.offers_banner_3_text),
                activity.getDrawable(R.drawable.banner_3_ppts),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_3_button),
                        () -> {
                            SuncorToast.makeText(activity, "This will redirect to rewards screen", Toast.LENGTH_SHORT).show();
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
                            new AlertDialog.Builder(activity)
                                    .setTitle("Leaving Petro-Canada?")
                                    .setMessage("You're about to open another app.")
                                    .setPositiveButton("Open", (dialog, which) -> {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("https://www.rbcroyalbank.com/petro-canada/cards-25.html"));//TODO
                                        activity.startActivity(intent);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
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
