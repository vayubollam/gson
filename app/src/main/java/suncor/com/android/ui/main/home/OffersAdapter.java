package suncor.com.android.ui.main.home;

import static suncor.com.android.utilities.Constants.ALERT;
import static suncor.com.android.utilities.Constants.ALERT_INTERACTION;
import static suncor.com.android.utilities.Constants.ALERT_SELECTION;
import static suncor.com.android.utilities.Constants.ALERT_TITLE;
import static suncor.com.android.utilities.Constants.CARWASH_ACTIVATION_BANNER;
import static suncor.com.android.utilities.Constants.CONTACTLESS_PAYMENT_BANNER;
import static suncor.com.android.utilities.Constants.POINTS_CONTEST_BANNER;
import static suncor.com.android.utilities.Constants.REDEEM_FOR_FREE_GAS_BANNER;
import static suncor.com.android.utilities.Constants.SAVE_AMOUNT_WITH_RBC_BANNER;
import static suncor.com.android.utilities.Constants.SIGN_IN_BANNER;

import android.content.Intent;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

import suncor.com.android.HomeNavigationDirections;
import suncor.com.android.R;
import suncor.com.android.databinding.OffersCardItemBinding;
import suncor.com.android.ui.YoutubePlayerActivity;
import suncor.com.android.ui.enrollment.EnrollmentActivity;
import suncor.com.android.ui.login.LoginActivity;
import suncor.com.android.ui.main.MainActivity;
import suncor.com.android.ui.main.stationlocator.FiltersFragment;
import suncor.com.android.ui.main.wallet.cards.CardsLoadType;
import suncor.com.android.utilities.AnalyticsUtils;


public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OffersViewHolder> {

    YoutubePlayerActivity youtubePlayerActivity;
    private ArrayList<OfferCard> offerCards;
    private int optionalImagePosition = 1;
    private ObservableBoolean isEnglishLocale = new ObservableBoolean();
    private ObservableBoolean isExpired = new ObservableBoolean();

    public OffersAdapter(MainActivity activity, boolean isSignedIn, ObservableBoolean isExpired) {
        offerCards = new ArrayList<>();
        this.isExpired = isExpired;
        checkForLocale();
        if (!isSignedIn) {
            optionalImagePosition = 2;
            OfferCard banner1 = new OfferCard(activity.getString(R.string.offers_banner_1_text),
                    activity.getDrawable(R.drawable.banner_8_signin_summer),
                    new OfferCard.OfferButton(activity.getString(R.string.join), () -> {
                        AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                "1|" + activity.getString(R.string.offers_banner_1_text),
                                activity.getString(R.string.offers_banner_1_text),
                                activity.getString(R.string.offers_banner_1_text),
                                "1"
                        );
                        activity.startActivity(new Intent(activity, EnrollmentActivity.class));
                    }),
                    new OfferCard.OfferButton(activity.getString(R.string.sign_in), () -> {
                        AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                "1|" + activity.getString(R.string.offers_banner_1_text),
                                activity.getString(R.string.offers_banner_1_text),
                                activity.getString(R.string.offers_banner_1_text),
                                "1"
                        );
                        activity.startActivity(new Intent(activity, LoginActivity.class));
                    }), false, SIGN_IN_BANNER);
            offerCards.add(banner1);
        }

        OfferCard banner2 = new OfferCard(activity.getString(R.string.offers_banner_2_text),
                activity.getDrawable(R.drawable.banner_6_fuelup),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_2_button),
                        () -> {

                            activity.getNavController().navigate(R.id.action_to_TutorialFragment);

                            AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                    (isSignedIn ? "1" : "2") + "|" + activity.getString(R.string.offers_banner_2_text),
                                    activity.getString(R.string.offers_banner_2_text),
                                    activity.getString(R.string.offers_banner_2_text),
                                    (isSignedIn ? "1" : "2")
                            );
                        }
                ), false, CONTACTLESS_PAYMENT_BANNER);
        offerCards.add(banner2);

        OfferCard banner3 = new OfferCard(activity.getString(!isSignedIn ? R.string.offers_banner_3a_text : R.string.offers_banner_3b_text),
                activity.getDrawable(R.drawable.banner_9_ppts_summer),
                new OfferCard.OfferButton(
                        activity.getString(!isSignedIn ? R.string.offers_banner_3a_button : R.string.offers_banner_3b_button),
                        () -> {
                            if (isSignedIn) {
                                HomeNavigationDirections.ActionToCardsDetailsFragment action = HomeNavigationDirections.actionToCardsDetailsFragment();
                                action.setLoadType(CardsLoadType.PETRO_POINT_ONLY);
                                activity.getNavController().navigate(action);
                            } else {
                                activity.getNavController().navigate(R.id.rewards_tab);
                            }
                            AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                    (isSignedIn ? "1" : "2") + "|" + activity.getString(R.string.offers_banner_2_text),
                                    activity.getString(R.string.offers_banner_2_text),
                                    activity.getString(R.string.offers_banner_2_text),
                                    (isSignedIn ? "1" : "2")
                            );
                        }
                ), false, REDEEM_FOR_FREE_GAS_BANNER);
        offerCards.add(banner3);


        OfferCard banner4 = new OfferCard(activity.getString(R.string.offers_banner_4_text),
                activity.getDrawable(R.drawable.banner_7_carwash),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_4_button),
                        () -> {
                            AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                    (isSignedIn ? "3" : "4") + "|" + activity.getString(R.string.offers_banner_4_text),
                                    activity.getString(R.string.offers_banner_4_text),
                                    activity.getString(R.string.offers_banner_4_text),
                                    (isSignedIn ? "3" : "4")
                            );

                            HomeNavigationDirections.ActionToStationsFragment action = HomeNavigationDirections.actionToStationsFragment();
                            action.setFilters(FiltersFragment.CARWASH_ALL_WASHES_KEY);
                            action.setRoot(false);
                            activity.getNavController().navigate(action);
                        }
                ), false, CARWASH_ACTIVATION_BANNER);
        offerCards.add(banner4);

        OfferCard banner5 = new OfferCard(activity.getString(R.string.offers_banner_5_text),
                activity.getDrawable(R.drawable.banner_5_rbc),
                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_5_button),
                        () -> {
                            AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                    (isSignedIn ? "4" : "5") + "|" + activity.getString(R.string.offers_banner_5_text),
                                    activity.getString(R.string.offers_banner_5_text),
                                    activity.getString(R.string.offers_banner_5_text),
                                    (isSignedIn ? "4" : "5")
                            );
                            AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT,
                                    new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")")
                            );
                            new AlertDialog.Builder(activity)
                                    .setTitle(activity.getString(R.string.offers_leaving_app_alert_title))
                                    .setMessage(activity.getString(R.string.offers_leaving_app_alert_message))
                                    .setPositiveButton(activity.getString(R.string.offers_leaving_app_alert_button), (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT_INTERACTION,
                                                new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")"),
                                                new Pair<>(ALERT_SELECTION, activity.getString(R.string.offers_leaving_app_alert_button))
                                        );
                                        String url = activity.getString(R.string.rbc_url);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        activity.startActivity(intent);

                                        AnalyticsUtils.logEvent(activity, "intersite", new Pair<>("intersiteURL", url));
                                    })
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT_INTERACTION,
                                                new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")"),
                                                new Pair<>(ALERT_SELECTION, activity.getString(R.string.cancel))
                                        );
                                    })
                                    .show();
                        }
                ), false, SAVE_AMOUNT_WITH_RBC_BANNER);
        offerCards.add(banner5);

        OfferCard optionalBanner = new OfferCard(activity.getString(R.string.offers_banner_optional_text),

                activity.getDrawable( isEnglishLocale.get() ? R.drawable.banner_black_man_en : R.drawable.banner_black_man_fr),

                new OfferCard.OfferButton(
                        activity.getString(R.string.offers_banner_optional_button),
                        () -> {
                            AnalyticsUtils.logPromotionEvent(activity, AnalyticsUtils.Event.SELECTCONTENT,
                                    (isSignedIn ? "4" : "5") + "|" + activity.getString(R.string.offers_banner_optional_text),
                                    activity.getString(R.string.offers_banner_optional_text),
                                    activity.getString(R.string.offers_banner_optional_text),
                                    (isSignedIn ? "4" : "5")
                            );
                            AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT,
                                    new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")")
                            );
                            new AlertDialog.Builder(activity)
                                    .setTitle(activity.getString(R.string.offers_leaving_app_alert_title))
                                    .setMessage(activity.getString(R.string.offers_leaving_app_alert_message))
                                    .setPositiveButton(activity.getString(R.string.offers_leaving_app_alert_button), (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT_INTERACTION,
                                                new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")"),
                                                new Pair<>(ALERT_SELECTION, activity.getString(R.string.offers_leaving_app_alert_button))
                                        );
                                        String url = activity.getString(R.string.petro_points_contest_url);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        activity.startActivity(intent);

                                        AnalyticsUtils.logEvent(activity, "intersite", new Pair<>("intersiteURL", url));
                                    })
                                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                                        AnalyticsUtils.logEvent(activity.getApplicationContext(), ALERT_INTERACTION,
                                                new Pair<>(ALERT_TITLE, activity.getString(R.string.offers_leaving_app_alert_title) + "(" + activity.getString(R.string.offers_leaving_app_alert_message) + ")"),
                                                new Pair<>(ALERT_SELECTION, activity.getString(R.string.cancel))
                                        );
                                    })
                                    .show();

                        }
                ), true, POINTS_CONTEST_BANNER);

        if (!isExpired.get()) {

            offerCards.add(optionalImagePosition, optionalBanner);
        }

    }

    @NonNull
    @Override
    public OffersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OffersCardItemBinding binding = OffersCardItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        checkForLocale();
        return new OffersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OffersViewHolder holder, int position) {
        OffersCardItemBinding binding = holder.binding;
        binding.setIsEnglish(isEnglishLocale);
        OfferCard card = offerCards.get(position);
        if (isExpired.get()) {
            if (!card.getBannerTag().equalsIgnoreCase(POINTS_CONTEST_BANNER)) {
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
        } else {
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

    private void checkForLocale() {
        isEnglishLocale.set(!Locale.getDefault().getLanguage().equalsIgnoreCase("fr"));
    }

    public class OffersViewHolder extends RecyclerView.ViewHolder {

        OffersCardItemBinding binding;

        public OffersViewHolder(OffersCardItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
