package suncor.com.android.uicomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class SuncorAppBarLayout extends AppBarLayout {

    boolean isToolbarTitleShown = true;

    private final BaseOnOffsetChangedListener offsetChangeListener = new OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            int positiveOffset = -verticalOffset;
            int threshold = expandedTitleTextView.getMeasuredHeight() + expandedTitleTopMargin;
            if (positiveOffset >= threshold) {
                if (!isToolbarTitleShown) {
                    expandedTitleTextView.setVisibility(INVISIBLE);
                    isToolbarTitleShown = true;
                }
                float alpha = ((float) (positiveOffset - threshold)) / (getTotalScrollRange() - threshold);
                collapsedTitleTextView.setAlpha(alpha);
            } else if (isToolbarTitleShown) {
                expandedTitleTextView.setVisibility(VISIBLE);
                collapsedTitleTextView.setAlpha(0);
                isToolbarTitleShown = false;
            }
        }
    };

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private AppCompatTextView expandedTitleTextView;
    private AppCompatTextView collapsedTitleTextView;
    private AppCompatImageButton navigationButton;
    private AppCompatImageButton rightButton;
    private int expandedTitleTopMargin;

    private boolean isDragable = true;

    public SuncorAppBarLayout(Context context) {
        super(context);
    }

    public SuncorAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setClipToPadding(false);
        LayoutInflater.from(context).inflate(R.layout.toolbar_layout, this, true);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        expandedTitleTextView = findViewById(R.id.expanded_title);
        collapsedTitleTextView = findViewById(R.id.collapsed_title);
        navigationButton = findViewById(R.id.navigation_button);
        rightButton = findViewById(R.id.right_button);
        expandedTitleTopMargin = getResources().getDimensionPixelSize(R.dimen.app_bar_layout_expanded_title_top_margin);

        if (getBackground() != null) {
            toolbar.setBackground(getBackground());
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SuncorAppBarLayout, R.attr.suncorAppBarLayoutStyle, 0);
        String title = a.getString(R.styleable.SuncorAppBarLayout_title);
        String expandedTitle;
        if (a.hasValue(R.styleable.SuncorAppBarLayout_expandedTitle)) {
            expandedTitle = a.getString(R.styleable.SuncorAppBarLayout_expandedTitle);
        } else {
            expandedTitle = title;
        }

        collapsedTitleTextView.setText(title);
        expandedTitleTextView.setText(expandedTitle);

        int titleTextAppearance = a.getResourceId(R.styleable.SuncorAppBarLayout_titleAppearance, -1);
        if (titleTextAppearance != -1) {
            collapsedTitleTextView.setTextAppearance(context, titleTextAppearance);
        }

        int expandedTitleTextAppearance = a.getResourceId(R.styleable.SuncorAppBarLayout_expandedTitleAppearance, -1);
        if (expandedTitleTextAppearance != -1) {
            expandedTitleTextView.setTextAppearance(context, expandedTitleTextAppearance);
        } else if (titleTextAppearance != -1) {
            expandedTitleTextView.setTextAppearance(context, titleTextAppearance);
        }

        if (a.hasValue(R.styleable.SuncorAppBarLayout_rightButtonIcon)) {
            rightButton.setImageDrawable(a.getDrawable(R.styleable.SuncorAppBarLayout_rightButtonIcon));
            rightButton.setVisibility(VISIBLE);
        }

        navigationButton.setVisibility(a.getBoolean(R.styleable.SuncorAppBarLayout_navigationVisibility, true) ? VISIBLE : GONE);

        if (a.hasValue(R.styleable.SuncorAppBarLayout_statusBarScrim)) {
            collapsingToolbarLayout.setStatusBarScrimColor(a.getColor(R.styleable.SuncorAppBarLayout_statusBarScrim, -1));
        }

        boolean isExpandable = a.getBoolean(R.styleable.SuncorAppBarLayout_expandable, true);
        if (!isExpandable) {
            expandedTitleTextView.setVisibility(GONE);
            disableScroll();
        }

        isDragable = a.getBoolean(R.styleable.SuncorAppBarLayout_dragable, true);

        if (!isDragable) {
            disableScroll();
        }

        addOnOffsetChangedListener(offsetChangeListener);

        toolbar.post(() -> {
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) expandedTitleTextView.getLayoutParams();
            params.topMargin = toolbar.getMeasuredHeight() + expandedTitleTopMargin;
            expandedTitleTextView.setLayoutParams(params);
        });
        a.recycle();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
        toolbar.setOnClickListener(l);
        collapsingToolbarLayout.setOnClickListener(l);
    }

    @Override
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
        collapsingToolbarLayout.setBackgroundColor(color);
    }

    private void disableScroll() {
        LayoutParams params = (LayoutParams) collapsingToolbarLayout.getLayoutParams();
        params.setScrollFlags(0);
        //Post elevation change to give it time to be done after drawing
        post(() -> {
            ViewCompat.setElevation(this, 0);
            ViewCompat.setElevation(collapsingToolbarLayout, 0);
            ViewCompat.setElevation(toolbar, 0);
        });
    }

    public boolean isExpanded() {
        return !isToolbarTitleShown;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params instanceof CoordinatorLayout.LayoutParams) {
            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            ((CoordinatorLayout.LayoutParams) params).setBehavior(behavior);
        }
        super.setLayoutParams(params);
    }

    /**
     * Sets the main title of this @{@link SuncorAppBarLayout},
     * if the expanded title is empty, the same title is used for both collapsing and expanding states
     * if the expanded title is already set, it will be only used as collapsed title.
     *
     * @param title the main title
     */
    public void setTitle(CharSequence title) {
        collapsedTitleTextView.setText(title);
        if (TextUtils.isEmpty(expandedTitleTextView.getText())) {
            expandedTitleTextView.setText(title);
        }
    }

    /**
     * Sets the expanded title of this @{@link SuncorAppBarLayout},
     *
     * @param expandedTitle the expanded title
     */
    public void setExpandedTitle(CharSequence expandedTitle) {
        expandedTitleTextView.setText(expandedTitle);
    }

    /**
     * Sets click listener on navigation icon
     *
     * @param listener click listener to be applied on navigation icon
     */
    public void setNavigationOnClickListener(OnClickListener listener) {
        navigationButton.setOnClickListener(listener);
    }

    public void setRightButtonIcon(Drawable drawable) {
        rightButton.setImageDrawable(drawable);
        rightButton.setVisibility(VISIBLE);
    }

    public void setRightButtonOnClickListener(OnClickListener listener) {
        rightButton.setOnClickListener(listener);
    }
}
