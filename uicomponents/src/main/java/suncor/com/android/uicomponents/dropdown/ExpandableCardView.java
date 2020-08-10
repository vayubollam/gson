package suncor.com.android.uicomponents.dropdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import suncor.com.android.uicomponents.R;

public  class ExpandableCardView extends CardView implements View.OnClickListener  , ChildViewListener{

    /*
     *  This is our expandable card view title --> It is a textview
     * */
    private TextView textViewTitle;
    //Image button of collapse sign
    private ImageButton imageButtonCollapse;
    //Image button of Expand sign
    private ImageButton imageButtonExpand;
    private View titleBackgroundLayout;
    //Inner view
    private RecyclerView viewStub;
    // Card view default title size
    private static final float DEFAULT_TITLE_SIZE = 5.0f;
    // Card view default title color
    private static final int DEFAULT_TITLE_COLOR = Color.rgb(128, 128, 128);
    // Card view default title background color
    private static final int DEFAULT_TITLE_BACKGROUND_COLOR = Color.rgb(255, 255, 255);
    //Card view expand tracker
    private boolean isExpand = false;
    //Card view custom text
    private String mTitle;
    //card view title custom size
    private float mTitleSize;
    //card view title custom color
    private int mTitleColor;
    //card view title custom background color
    private int mTitleBackgroundColor;
    //card view inner item
    private int mInterViewID;
    //get inner view item
    private View mInnerView;
    //card collapse icon
    private Drawable mCollapseIcon;
    //card expand icon
    private Drawable mExpandIcon;
    private ExpandableViewListener mExpandCollapseListener;
    private ChildDropDownAdapter mAdapter;
    private Context mContext;


    public ExpandableCardView(Context context) {
        super(context);
        this.mContext = context;
    }

    public ExpandableCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;

        initAttrs(attrs);

        initView(context);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        textViewTitle = findViewById(R.id.header_title);
        imageButtonCollapse = findViewById(R.id.image_button_collapse);
        imageButtonExpand = findViewById(R.id.image_button_expand);
        viewStub = (RecyclerView) findViewById(R.id.recycler_view);
        titleBackgroundLayout = findViewById(R.id.header_layout);
        setTitle(mTitle);
        setTitleSize(mTitleSize);
        setTitleColor(mTitleColor);
      //  setInnerItem(mInterViewID);
        setIcon(mCollapseIcon, mExpandIcon);
        setTitleBackgroundColor(mTitleBackgroundColor);
        imageButtonCollapse.setOnClickListener(this);
        imageButtonExpand.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.image_button_collapse) {
            isExpand = true;
            imageButtonCollapse.setVisibility(GONE);
            imageButtonExpand.setVisibility(VISIBLE);
            textViewTitle.setTypeface(textViewTitle.getTypeface(), Typeface.BOLD);
            textViewTitle.setText(mTitle);

            mExpandCollapseListener.onExpandCollapseListener(isExpand);

            if (mInnerView != null) {
                expand(mInnerView);
            }
            findViewById(R.id.recycler_view).setVisibility(VISIBLE);
            findViewById(R.id.selected_value).setVisibility(INVISIBLE);
            if(mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }

        } else if (i == R.id.image_button_expand) {
            isExpand = false;
            imageButtonCollapse.setVisibility(VISIBLE);
            imageButtonExpand.setVisibility(GONE);
            textViewTitle.setText(textViewTitle.getText().toString().toUpperCase());
            textViewTitle.setTypeface(textViewTitle.getTypeface(), Typeface.NORMAL);

            mExpandCollapseListener.onExpandCollapseListener(isExpand);

            ((RecyclerView)findViewById(R.id.recycler_view)).setVisibility(GONE);
            findViewById(R.id.selected_value).setVisibility(VISIBLE);
            if (mInnerView != null) {
                collapse(mInnerView);
            }
            int selectedValue = mAdapter.getSelectedValue();
            if(selectedValue == 0){
                Snackbar.make(getRootView(), mContext.getString(R.string.invalid_manual_fuel_up_limit), Snackbar.LENGTH_SHORT).show();
                ((TextView)findViewById(R.id.selected_value)).setText(String.format("$%s", ""));
            } else {
                ((TextView)findViewById(R.id.selected_value)).setText(String.format("$%s", selectedValue));
            }
            mExpandCollapseListener.onSelectFuelUpLimit(selectedValue);
        }
    }

    /**
     *
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandableCardView);
        mTitle = typedArray.getString(R.styleable.ExpandableCardView_header_title);
        mTitleSize = typedArray.getDimension(R.styleable.ExpandableCardView_title_size, DEFAULT_TITLE_SIZE);
        mTitleColor = typedArray.getColor(R.styleable.ExpandableCardView_title_color, DEFAULT_TITLE_COLOR);
        mTitleBackgroundColor = typedArray.getColor(R.styleable.ExpandableCardView_title_background_color, DEFAULT_TITLE_BACKGROUND_COLOR);
        mInterViewID = typedArray.getResourceId(R.styleable.ExpandableCardView_item_inner_view, View.NO_ID);
        mCollapseIcon = typedArray.getDrawable(R.styleable.ExpandableCardView_collapse_icon);
        mExpandIcon = typedArray.getDrawable(R.styleable.ExpandableCardView_expand_icon);
        typedArray.recycle();

    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.expandable_drop_down_layout, this);
        }
    }

    public void setDropDownData(HashMap<String,String>  data, int otherLimitMaxValue, int otherLimitMinvalue){
        mAdapter = new ChildDropDownAdapter(mContext, data, this, otherLimitMaxValue, otherLimitMinvalue);
        ((RecyclerView)findViewById(R.id.recycler_view)).setLayoutManager(new LinearLayoutManager(mContext));
        ((RecyclerView)findViewById(R.id.recycler_view)).setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelectFuelUpLimit(int value) {

    }

    private void setTitle(String title) {
        if (title != null) {
            textViewTitle.setText(title.toUpperCase());
        }
    }


    private void setTitleSize(float titleSize) {
        mTitleSize = px2sp(titleSize);
        textViewTitle.setTextSize(mTitleSize);
    }

    private void setTitleColor(int color) {
        textViewTitle.setTextColor(color);
    }

    private void setInnerItem(int id) {
      //  viewStub.setLayoutResource(R.id.recycler_view);
        viewStub.setVisibility(View.GONE);
        if (id != View.NO_ID) {
          //  mInnerView = viewStub.inflate();
            mInnerView.setVisibility(GONE);
        }
      //  viewStub.getInflatedId();

    }

    private void setIcon(Drawable collapseIcon, Drawable expandIcon) {

        if (collapseIcon != null) {
            imageButtonCollapse.setImageDrawable(collapseIcon);
        }
        if (expandIcon != null) {
            imageButtonExpand.setImageDrawable(expandIcon);
        }
    }

    private void setTitleBackgroundColor(int color) {
        titleBackgroundLayout.setBackgroundColor(color);

    }

    /*
     * Animation section
     * */

    public void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(100);
        v.startAnimation(a);
    }

    public void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(500);
        v.startAnimation(a);
    }

    public void slideUp(final View view) {
        //view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // slide the view from its current position to below itself
    public void slideDown(final View view) {
        view.setVisibility(VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);

        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /*
     * User settable property
     * */

    public void setTitleTextColor(int color) {
        textViewTitle.setTextColor(color);
    }

    public void setTitleText(String text) {
        textViewTitle.setText(text);
    }

    /*
     * User gettable property
     * */

    public View getChildView() {
        if (mInnerView != null) return mInnerView;
        return null;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void initListener(ExpandableViewListener listener) {
        this.mExpandCollapseListener = listener;
    }


    private float px2sp(float px) {
        return px / getResources().getDisplayMetrics().scaledDensity;
    }

    private float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }
}