package suncor.com.android.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class ExtendedNestedScrollView extends NestedScrollView {

    private boolean isScrollEnabled = true;

    public ExtendedNestedScrollView(@NonNull Context context) {
        super(context);
    }

    public ExtendedNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isScrollEnabled() {
        return isScrollEnabled;
    }

    public void setScrollEnabled(boolean scrollEnabled) {
        isScrollEnabled = scrollEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isScrollEnabled) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return super.onStartNestedScroll(child, target, axes, type);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.d(ExtendedNestedScrollView.class.getSimpleName(), "" + dyUnconsumed + " " + dyConsumed);
        if (isScrollEnabled || dyUnconsumed == 0) {
            super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (isScrollEnabled) {
            super.scrollTo(x, y);
        }
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (isScrollEnabled) {
            super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        }
    }
}
