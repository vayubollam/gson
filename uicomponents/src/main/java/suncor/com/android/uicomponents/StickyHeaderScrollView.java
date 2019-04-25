package suncor.com.android.uicomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class StickyHeaderScrollView extends NestedScrollView {
    private ArrayList<View> stickyHeaders = new ArrayList<>();

    private View stickyHeader;

    private int stickyViewLeftOffset;
    private int maxStickScroll;

    private boolean isCollectionSorted;

    private Runnable sortingRunnable = () -> {
        Collections.sort(stickyHeaders, (v1, v2) -> getTopForViewRelativeOnlyChild(v1) - getTopForViewRelativeOnlyChild(v2));
    };

    public StickyHeaderScrollView(@NonNull Context context) {
        super(context);
    }

    public StickyHeaderScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StickyHeaderScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (stickyHeader != null && stickyHeader.isShown()) {
            canvas.save();

            int top = maxStickScroll == -1 || getScrollY() < maxStickScroll ? getScrollY() : maxStickScroll;
            canvas.translate(getPaddingLeft() + stickyViewLeftOffset, top + getPaddingTop());

            canvas.clipRect(0, 0, getWidth(), stickyHeader.getHeight() + 1);
            stickyHeader.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldl, int oldt) {
        super.onScrollChanged(scrollX, scrollY, oldl, oldt);

        //remove invisible headers and sort the list from bottom to top
        if (!isCollectionSorted) {
            for (int i = stickyHeaders.size() - 1; i >= 0; i--) {
                if (!stickyHeaders.get(i).isShown()) {
                    stickyHeaders.remove(i);
                }
            }
            Collections.sort(stickyHeaders, (v1, v2) -> getTopForViewRelativeOnlyChild(v2) - getTopForViewRelativeOnlyChild(v1));
            isCollectionSorted = true;
        }

        //find current sticky header
        for (View view : stickyHeaders) {
            int viewTop = getTopForViewRelativeOnlyChild(view);
            if (scrollY > viewTop) {
                stickyHeader = view;
                break;
            }
        }
        if (stickyHeader != null) {
            stickyViewLeftOffset = getLeftForViewRelativeOnlyChild(stickyHeader);

            //find next sticky header
            int index = stickyHeaders.indexOf(stickyHeader);
            if (index != 0) {
                maxStickScroll = getTopForViewRelativeOnlyChild(stickyHeaders.get(index - 1)) - stickyHeader.getHeight();
            } else {
                maxStickScroll = -1;
            }
        }
    }

    public void addStickyHeader(View view) {
        stickyHeaders.add(view);
        isCollectionSorted = false;
    }

    private int getLeftForViewRelativeOnlyChild(View v) {
        int left = v.getLeft();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            left += v.getLeft();
        }
        return left;
    }

    private int getTopForViewRelativeOnlyChild(View v) {
        int top = v.getTop();
        while (v.getParent() != getChildAt(0)) {
            v = (View) v.getParent();
            top += v.getTop();
        }
        return top;
    }
}
