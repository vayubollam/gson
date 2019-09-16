package suncor.com.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import suncor.com.android.utilities.AnalyticsUtils;


public class SuncorButton extends MaterialButton {
    public SuncorButton(Context context) {
        super(context);
    }

    public SuncorButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SuncorButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        AnalyticsUtils.logEvent(getContext(), "button_tap", new Pair<>("buttonText", getText().toString()));
        return super.performClick();
    }
}
