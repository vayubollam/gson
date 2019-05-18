package suncor.com.android.uicomponents.pagerindicator;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;

import suncor.com.android.uicomponents.R;

class BaseCircleIndicator extends LinearLayout {

    private final static int DEFAULT_INDICATOR_WIDTH = 5;

    protected int mIndicatorMargin = -1;
    protected int mIndicatorWidth = -1;
    protected int mIndicatorHeight = -1;

    protected int mIndicatorBackgroundResId;
    protected int mIndicatorUnselectedBackgroundResId;


    protected int mLastPosition = -1;

    public BaseCircleIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public BaseCircleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BaseCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCircleIndicator(Context context, AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Config config = handleTypedArray(context, attrs);
        initialize(config);
    }

    private Config handleTypedArray(Context context, AttributeSet attrs) {
        Config config = new Config();
        if (attrs == null) {
            return config;
        }
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.BaseCircleIndicator);
        config.width =
                typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_width, -1);
        config.height =
                typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_height, -1);
        config.margin =
                typedArray.getDimensionPixelSize(R.styleable.BaseCircleIndicator_ci_margin, -1);
        config.animatorReverseResId =
                typedArray.getResourceId(R.styleable.BaseCircleIndicator_ci_animator_reverse, 0);
        config.backgroundResId =
                typedArray.getResourceId(R.styleable.BaseCircleIndicator_ci_drawable,
                        R.drawable.white_radius);
        config.unselectedBackgroundId =
                typedArray.getResourceId(R.styleable.BaseCircleIndicator_ci_drawable_unselected,
                        config.backgroundResId);
        config.orientation = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_orientation, -1);
        config.gravity = typedArray.getInt(R.styleable.BaseCircleIndicator_ci_gravity, -1);
        typedArray.recycle();

        return config;
    }

    public void initialize(Config config) {
        int miniSize = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_INDICATOR_WIDTH, getResources().getDisplayMetrics()) + 0.5f);
        mIndicatorWidth = (config.width < 0) ? miniSize : config.width;
        mIndicatorHeight = (config.height < 0) ? miniSize : config.height;
        mIndicatorMargin = (config.margin < 0) ? miniSize : config.margin;

        mIndicatorBackgroundResId =
                (config.backgroundResId == 0) ? R.drawable.white_radius : config.backgroundResId;
        mIndicatorUnselectedBackgroundResId =
                (config.unselectedBackgroundId == 0) ? config.backgroundResId
                        : config.unselectedBackgroundId;

        setOrientation(config.orientation == VERTICAL ? VERTICAL : HORIZONTAL);
        setGravity(config.gravity >= 0 ? config.gravity : Gravity.CENTER);
    }

    protected void createIndicators(int count, int currentPosition) {
        int orientation = getOrientation();
        for (int i = 0; i < count; i++) {
            if (currentPosition == i) {
                addIndicator(orientation, mIndicatorBackgroundResId);
            } else {
                addIndicator(orientation, mIndicatorUnselectedBackgroundResId);
            }
        }
    }

    protected void addIndicator(int orientation, @DrawableRes int backgroundDrawableId) {

        View indicator = new View(getContext());
        indicator.setBackgroundResource(backgroundDrawableId);
        addView(indicator, mIndicatorWidth, mIndicatorHeight);
        LayoutParams lp = (LayoutParams) indicator.getLayoutParams();

        if (orientation == HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin;
            lp.rightMargin = mIndicatorMargin;
        } else {
            lp.topMargin = mIndicatorMargin;
            lp.bottomMargin = mIndicatorMargin;
        }

        indicator.setLayoutParams(lp);
    }

    protected void internalPageSelected(int position) {



        View currentIndicator;
        if (mLastPosition >= 0 && (currentIndicator = getChildAt(mLastPosition)) != null) {
            currentIndicator.setBackgroundResource(mIndicatorUnselectedBackgroundResId);
        }

        View selectedIndicator = getChildAt(position);
        if (selectedIndicator != null) {
            selectedIndicator.setBackgroundResource(mIndicatorBackgroundResId);
        }
    }

    protected class ReverseInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float value) {
            return Math.abs(1.0f - value);
        }
    }
}

