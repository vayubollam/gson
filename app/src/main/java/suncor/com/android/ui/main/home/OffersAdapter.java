package suncor.com.android.ui.main.home;

import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import suncor.com.android.R;
import suncor.com.android.databinding.OffersCardItemBinding;
import suncor.com.android.ui.YoutubePlayerActivity;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.utilities.AnalyticsUtils;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    private ArrayList<OfferCard> offerCards;

    YoutubePlayerActivity youtubePlayerActivity ;

    public OffersAdapter(MainActivity activity, boolean isSignedIn) {
        offerCards = new ArrayList<>();
        if (!isSignedIn) {
            OfferCard banner1 = new OfferCard(activity.getString(R.string.offers_banner_1_text),
                    activity.getDrawable(R.drawable.banner_1_signin),
                    new OfferCard.OfferButton(activity.getString(R.string.join), () -> {
                        AnalyticsUtils.logEvent(activity, "select_content",
                            new Pair<>("creative_slot", "1"),
                            new Pair<>("creative_name", activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("item_name", activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("item_id", "1|"+activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("content_type", "Internal Promotions")
                        );
                        activity.startActivity(new Intent(activity, EnrollmentActivity.class));
                    }),
                    new OfferCard.OfferButton(activity.getString(R.string.sign_in), () -> {
                        AnalyticsUtils.logEvent(activity, "select_content",
                            new Pair<>("creative_slot", "1"),
                            new Pair<>("creative_name", activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("item_name", activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("item_id", "1|"+activity.getString(R.string.offers_banner_1_text)),
                            new Pair<>("content_type", "Internal Promotions")
                        );
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }));
            offerCards.add(banner1);
        }

        OfferCard banner2 = new OfferCard(activity.getString(R.string.offers_banner_3_text),
                activity.getDrawable(R.drawable.banner_3_brand),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_3_button),
                        activity.getDrawable(R.drawable.ic_play_video),
                        () -> {
                            AnalyticsUtils.logEvent(activity, "select_content",
                                new Pair<>("creative_slot", isSignedIn? "2":"3"),
                                new Pair<>("creative_name", activity.getString(R.string.offers_banner_3_text)),
                                new Pair<>("item_name", activity.getString(R.string.offers_banner_3_text)),
                                new Pair<>("item_id", (isSignedIn? "2":"3")+"|"+activity.getString(R.string.offers_banner_3_text)),
                                new Pair<>("content_type", "Internal Promotions")
                            );
                            Intent intent = new Intent(activity, YoutubePlayerActivity.class);
                            intent.putExtra(YoutubePlayerActivity.VIDEO_ID_EXTRA, activity.getString(R.string.offers_banner_3_link));
                            intent.putExtra(YoutubePlayerActivity.VIDEO_TITLE, activity.getString(R.string.offers_banner_3_text));

                            activity.startActivity(intent);
                        }
                ));
        offerCards.add(banner2);

        OfferCard banner3 = new OfferCard(activity.getString(R.string.offers_banner_2_text),
                activity.getDrawable(R.drawable.banner_2_ppts),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_2_button),
                        () -> {
                            if (isSignedIn)
                            {
                                activity.getNavController().navigate(R.id.action_home_tab_to_rewardsDiscoveryFragment);
                            }
                            else
                            {
                                activity.getNavController().navigate(R.id.rewards_tab);
                            }
                            AnalyticsUtils.logEvent(activity, "select_content",
                                new Pair<>("creative_slot", isSignedIn? "1":"2"),
                                new Pair<>("creative_name", activity.getString(R.string.offers_banner_2_text)),
                                new Pair<>("item_name", activity.getString(R.string.offers_banner_2_text)),
                                new Pair<>("item_id", (isSignedIn? "1":"2")+"|"+activity.getString(R.string.offers_banner_2_text)),
                                new Pair<>("content_type", "Internal Promotions")
                            );
                        }
                ));
        offerCards.add(banner3);


        OfferCard banner4 = new OfferCard(activity.getString(R.string.offers_banner_4_text),
                activity.getDrawable(R.drawable.banner_4_ev),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_4_button),
                        activity.getDrawable(R.drawable.ic_play_video),
                        () -> {
                            AnalyticsUtils.logEvent(activity, "select_content",
                                    new Pair<>("creative_slot", isSignedIn? "3":"4"),
                                    new Pair<>("creative_name", activity.getString(R.string.offers_banner_4_text)),
                                    new Pair<>("item_name", activity.getString(R.string.offers_banner_4_text)),
                                    new Pair<>("item_id", (isSignedIn? "3":"4")+"|"+activity.getString(R.string.offers_banner_4_text)),
                                    new Pair<>("content_type", "Internal Promotions")
                            );
                            Intent intent = new Intent(activity, YoutubePlayerActivity.class);
                            AnalyticsUtils.logEvent(activity, "video_start", new Pair<>("videoTitle", activity.getString(R.string.offers_banner_4_text)));
                            intent.putExtra(YoutubePlayerActivity.VIDEO_ID_EXTRA, "xsa9QjRgy5w");
                            intent.putExtra(YoutubePlayerActivity.VIDEO_TITLE, activity.getString(R.string.offers_banner_4_text));

                            activity.startActivity(intent);
                        }
                ));
        offerCards.add(banner4);

        OfferCard banner5 = new OfferCard(activity.getString(R.string.offers_banner_5_text),
                activity.getDrawable(R.drawable.banner_5_rbc),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_5_button),
                        () -> {
                            AnalyticsUtils.logEvent(activity, "select_content",
                                    new Pair<>("creative_slot", isSignedIn? "4":"5"),
                                    new Pair<>("creative_name", activity.getString(R.string.offers_banner_5_text)),
                                    new Pair<>("item_name", activity.getString(R.string.offers_banner_5_text)),
                                    new Pair<>("item_id", (isSignedIn? "4":"5")+"|"+activity.getString(R.string.offers_banner_5_text)),
                                    new Pair<>("content_type", "Internal Promotions")
                            );
                            AnalyticsUtils.logEvent(activity.getApplicationContext(), "alert", new Pair<>("alertTitle", activity.getString(R.string.offers_leaving_app_alert_title)));
                            new AlertDialog.Builder(activity)
                                    .setTitle(activity.getString(R.string.offers_leaving_app_alert_title))
                                    .setMessage(activity.getString(R.string.offers_leaving_app_alert_message))
                                    .setPositiveButton(activity.getString(R.string.offers_leaving_app_alert_button), (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), "alert_interaction",
                                                new Pair<>("alertTitle", activity.getString(R.string.offers_leaving_app_alert_title)),
                                                new Pair<>("alertSelection",activity.getString(R.string.offers_leaving_app_alert_button))
                                        );
                                        String url = activity.getString(R.string.rbc_url);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        activity.startActivity(intent);

                                        AnalyticsUtils.logEvent(activity, "intersite", new Pair<>("intersiteURL", url));
                                    })
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), "alert_interaction",
                                                new Pair<>("alertTitle", activity.getString(R.string.offers_leaving_app_alert_title)),
                                                new Pair<>("alertSelection",activity.getString(R.string.cancel))
                                        );
                                    })
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
            //Apply a matrix to simulate center_top
            Matrix matrix = binding.bannerImage.getImageMatrix();
            float scaleXFactor = binding.bannerImage.getWidth() / (float) card.getImage().getIntrinsicWidth();
            float scaleYFactor = binding.bannerImage.getHeight() / (float) card.getImage().getIntrinsicHeight();
            float scaleFactor = Math.max(scaleXFactor, scaleYFactor);
            float pivotPoint = 2 * (card.getImage().getIntrinsicWidth() * scaleFactor - binding.bannerImage.getWidth());
            matrix.setScale(scaleFactor, scaleFactor, pivotPoint, 0);
            binding.bannerImage.setImageMatrix(matrix);
        });
    }

    @Override
    public int getItemCount() {
        return offerCards.size();
    }

    public OfferCard getOffer(int position) {
        if (position < offerCards.size() && position > 0) {
            return offerCards.get(position);
        } else {
            return offerCards.get(0);
        }
    }

    public class OffersViewHolder extends RecyclerView.ViewHolder {

        OffersCardItemBinding binding;

        public OffersViewHolder(OffersCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
