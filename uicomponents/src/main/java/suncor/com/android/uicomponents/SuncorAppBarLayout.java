package suncor.com.android.uicomponents;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

public class SuncorAppBarLayout extends AppBarLayout {

    boolean isToolbarTitleShown = true;

    private final BaseOnOffsetChangedListener offsetChangeListener = new OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            int positiveOffset = -verticalOffset;
            int threshold = expandedTitleTextView.getMeasuredHeight() + expandedTitleTopMargin;
            Log.d("offset", positiveOffset + " " + threshold);
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
    private int expandedTitleTopMargin;

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

        if (a.hasValue(R.styleable.SuncorAppBarLayout_navigationIcon)) {
            toolbar.setNavigationIcon(a.getResourceId(R.styleable.SuncorAppBarLayout_navigationIcon, -1));
        }

        if (a.hasValue(R.styleable.SuncorAppBarLayout_statusBarScrim)) {
            collapsingToolbarLayout.setStatusBarScrimColor(a.getColor(R.styleable.SuncorAppBarLayout_statusBarScrim, -1));
        }

        boolean isExpandable = a.getBoolean(R.styleable.SuncorAppBarLayout_expandable, true);
        if (!isExpandable) {
            setElevation(0);
            toolbar.setElevation(0);
            collapsingToolbarLayout.setElevation(0);
            expandedTitleTextView.setVisibility(GONE);
            AppBarLayout.LayoutParams params = (LayoutParams) collapsingToolbarLayout.getLayoutParams();
            params.setScrollFlags(0);
        }

        addOnOffsetChangedListener(offsetChangeListener);

        toolbar.post(() -> {
            CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) expandedTitleTextView.getLayoutParams();
            params.topMargin = toolbar.getMeasuredHeight() + expandedTitleTopMargin;
            expandedTitleTextView.setLayoutParams(params);
        });

        a.recycle();
    }

    public void setTitle(CharSequence title) {
        collapsedTitleTextView.setText(title);
    }

    public void setExpandedTitle(CharSequence expandedTitle) {
        expandedTitleTextView.setText(expandedTitle);
    }

    public void setNavigationOnClickListener(OnClickListener listener) {
        navigationButton.setOnClickListener(listener);
    }
}
