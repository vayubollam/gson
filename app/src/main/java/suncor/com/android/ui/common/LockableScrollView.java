package suncor.com.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;

public class LockableScrollView extends NestedScrollView {
    private boolean mScrollable = false;

    public LockableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockableScrollView(Context context) {
        super(context);
    }

    public void setScrollingEnabled(boolean enabled) {
        mScrollable = enabled;
    }

    public boolean isScrollable() {
        return mScrollable;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mScrollable) return super.onTouchEvent(ev);
                return mScrollable;
            default:
                return super.onTouchEvent(ev);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!mScrollable) return false;
        else return super.onInterceptTouchEvent(ev);
    }


}