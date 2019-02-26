package suncor.com.android.ui.enrollement;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import suncor.com.android.R;

public class CardQuestion extends Fragment {

    private LinearLayout cardQuestionLayout;
    private AppCompatImageView cardImg, cardShadowImg;

    public CardQuestion() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_card_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cardQuestionLayout = getView().findViewById(R.id.xtCardQuestionLayout);
        cardImg = getView().findViewById(R.id.cardImage);
        cardShadowImg = getView().findViewById(R.id.cardShadowImg);
        cardShadowImg.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();

     /*   AnimationSet set = new AnimationSet(true);
        Animation trAnimation = new TranslateAnimation(0, 0, -50, 10);
        trAnimation.setDuration(200);

        trAnimation.setRepeatMode(Animation.REVERSE);
        set.addAnimation(trAnimation);
        Animation anim = new AlphaAnimation(0.3f, 1.0f);
        anim.setDuration(200);
        set.addAnimation(anim);
        cardImg.startAnimation(set);
*/
        Animation cardAnim = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_card);
        cardAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardQuestionLayout.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardQuestionLayout.animate().scaleY(1f).scaleX(1f).setDuration(100);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        cardShadowImg.setAlpha(0f);
        cardShadowImg.setVisibility(View.VISIBLE);
        cardImg.startAnimation(cardAnim);
        cardShadowImg.animate().alpha(1f).setDuration(200).setInterpolator(new AccelerateInterpolator()).setListener(null);
        cardImg.animate().translationY(1.5f).translationX(1.5f).setDuration(200);
    }

    public static int getDistanceBetweenViews(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int b = firstView.getMeasuredHeight() + firstPosition[1];
        int t = secondPosition[1];
        return Math.abs(b - t);
    }
}
