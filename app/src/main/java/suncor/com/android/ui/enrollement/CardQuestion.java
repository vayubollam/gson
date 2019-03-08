package suncor.com.android.ui.enrollement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import suncor.com.android.R;
import suncor.com.android.ui.enrollement.form.EnrollmentFormFragment;
import suncor.com.android.uicomponents.SuncorAppBarLayout;

public class CardQuestion extends Fragment {

    private AppCompatImageView cardImg, cardShadow;
    private int cardAnimationDuration = 400;

    public CardQuestion() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        cardImg = getView().findViewById(R.id.cardImage);
        cardShadow = getView().findViewById(R.id.cardShadow);

        cardImg.post(() -> {
            float cardRatio = (float) cardImg.getDrawable().getIntrinsicHeight() / cardImg.getDrawable().getIntrinsicWidth();
            float shadowRatio = (float) cardShadow.getDrawable().getIntrinsicHeight() / cardShadow.getDrawable().getIntrinsicWidth();

            int width = (int) (cardImg.getMeasuredHeight() / cardRatio);
            cardImg.getLayoutParams().width = width;
            cardShadow.getLayoutParams().width = width;
            cardShadow.getLayoutParams().height = (int) (shadowRatio * width);
            cardImg.requestLayout();
            cardShadow.requestLayout();
        });

        SuncorAppBarLayout appBarLayout = getView().findViewById(R.id.app_bar);
        appBarLayout.setNavigationOnClickListener(v -> getActivity().onBackPressed());

        getView().findViewById(R.id.no_card_button).setOnClickListener((v) -> {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = new EnrollmentFormFragment();
            ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            ft.replace(R.id.enrollment_main_frame, fragment);
            ft.addToBackStack(null);
            ft.commit();
        });

        getView().findViewById(R.id.with_card_button).setOnClickListener((v) -> {
            Toast.makeText(getContext(), "This button will take to the \"Join with Card screen\"", Toast.LENGTH_LONG).show();
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        AnimationSet set = new AnimationSet(true);
        Animation trAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.5f, Animation.RELATIVE_TO_SELF, 0.02f);
        trAnimation.setFillAfter(true);
        trAnimation.setDuration(cardAnimationDuration);
        set.addAnimation(trAnimation);
        Animation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(cardAnimationDuration);
        set.addAnimation(alphaAnim);
        set.setInterpolator(new AccelerateInterpolator());
        set.setStartOffset(150);
        set.setFillAfter(true);
        cardImg.startAnimation(set);
        cardShadow.startAnimation(alphaAnim);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardImg.animate().translationY(-pxFromDp(getContext(), 8)).setDuration(150).setInterpolator(new DecelerateInterpolator()).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
}
