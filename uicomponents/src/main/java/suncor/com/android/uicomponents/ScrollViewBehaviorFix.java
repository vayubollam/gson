package suncor.com.android.uicomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class ScrollViewBehaviorFix extends AppBarLayout.ScrollingViewBehavior {

    public ScrollViewBehaviorFix() {
        super();
    }

    public ScrollViewBehaviorFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec,
                                  int heightUsed) {
        if (child.getLayoutParams().height == -1) {
            List<View> dependencies = parent.getDependencies(child);
            if (dependencies.isEmpty()) {
                return false;
            }

            final AppBarLayout appBar = findFirstAppBarLayout(dependencies);
            if (ViewCompat.isLaidOut(appBar)) {
                int availableHeight = View.MeasureSpec.getSize(parentHeightMeasureSpec);
                if (availableHeight == 0) {
                    availableHeight = parent.getHeight();
                }

                final int height = availableHeight - appBar.getMeasuredHeight();
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);

                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                int childContentHeight = getContentHeight(child);

                if (childContentHeight <= height) {
                    updateToolbar(parent, appBar, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed, false);

                    heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                    parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);

                    return true;
                } else {
                    updateToolbar(parent, appBar, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed, true);

                    return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
                }
            }
        }

        return false;
    }

    private static int getContentHeight(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;

            int contentHeight = 0;
            for (int index = 0; index < viewGroup.getChildCount(); ++index) {
                View child = viewGroup.getChildAt(index);
                contentHeight += child.getMeasuredHeight();
            }
            return contentHeight;
        } else {
            return view.getMeasuredHeight();
        }
    }

    private static AppBarLayout findFirstAppBarLayout(List<View> views) {
        int i = 0;

        for (int z = views.size(); i < z; ++i) {
            View view = views.get(i);
            if (view instanceof AppBarLayout) {
                return (AppBarLayout) view;
            }
        }

        throw new IllegalArgumentException("Missing AppBarLayout in CoordinatorLayout dependencies");
    }

    private void updateToolbar(CoordinatorLayout parent, AppBarLayout appBar, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec,
                               int heightUsed, boolean toggle) {
        toggleToolbarScroll(appBar, toggle);

        appBar.forceLayout();
        parent.onMeasureChild(appBar, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    private void toggleToolbarScroll(AppBarLayout appBar, boolean toggle) {
        for (int index = 0; index < appBar.getChildCount(); ++index) {
            View child = appBar.getChildAt(index);

            if (child instanceof CollapsingToolbarLayout) {
                CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) child;
                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
                int scrollFlags = params.getScrollFlags();

                if (toggle) {
                    scrollFlags |= AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
                } else {
                    scrollFlags &= ~AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL;
                    appBar.post(() -> {
                        ViewCompat.setElevation(collapsingToolbarLayout, 0);
                        ViewCompat.setElevation(collapsingToolbarLayout.findViewById(R.id.toolbar), 0);
                        ViewCompat.setElevation(appBar, 0);
                    });

                }

                params.setScrollFlags(scrollFlags);
            }
        }
    }
}