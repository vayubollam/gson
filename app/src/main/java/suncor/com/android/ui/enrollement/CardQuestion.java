package suncor.com.android.ui.enrollement;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import suncor.com.android.R;

public class CardQuestion extends Fragment {

    private AppCompatImageView cardImg, cardShadow;
    private int cardAnimationDuration = 300;

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

        AppCompatImageButton backButton = getView().findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getActivity().finish());
    }


    @Override
    public void onResume() {
        super.onResume();
        AnimationSet set = new AnimationSet(true);
        Animation trAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.5f, Animation.RELATIVE_TO_SELF, 0.05f);
        trAnimation.setDuration(cardAnimationDuration);
        set.addAnimation(trAnimation);
        Animation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
        alphaAnim.setDuration(cardAnimationDuration);
        set.addAnimation(alphaAnim);
        set.setInterpolator(new AccelerateInterpolator());
        set.setStartOffset(cardAnimationDuration / 2);
        cardImg.startAnimation(set);
        cardShadow.startAnimation(alphaAnim);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardImg.animate().translationY(-pxFromDp(getContext(), 8)).setDuration(cardAnimationDuration / 2).start();
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
