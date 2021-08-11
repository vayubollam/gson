package suncor.com.android.uicomponents.dropdown;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import suncor.com.android.uicomponents.R;

public class ExpandableCardView extends CardView implements View.OnClickListener, ChildViewListener{

    //expandable card view title
    private TextView textViewTitle;
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
    private String mExpandedTitle;
    //card view title custom size
    private float mTitleSize;
    //card view title custom color
    private int mTitleColor;
    //card view title custom background color
    private int mTitleBackgroundColor;
    //card collapse icon
    private Drawable mCollapseIcon;
    //card expand icon
    private Drawable mExpandIcon;

    private String selectedPosition;
    private boolean isFromRedeemPoints;
    private ExpandableViewListener mExpandCollapseListener;
    private DropDownAdapter mAdapter;
    private Context mContext;
    private boolean isRedeemSelectionChanged;
    private static String currentPosition;
    private static boolean isCollapsed = false;

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
        imageButtonExpand = findViewById(R.id.image_button_expand);
        viewStub = findViewById(R.id.recycler_view);
        titleBackgroundLayout = findViewById(R.id.header_layout);
        setTitle(mTitle);
        setTitleSize(mTitleSize);
        setTitleColor(mTitleColor);
        setIcon(mCollapseIcon, mExpandIcon);
        setTitleBackgroundColor(mTitleBackgroundColor);
        findViewById(R.id.header_layout).setOnClickListener(this);
        findViewById(R.id.image_button_expand).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_layout || view.getId() == R.id.image_button_expand) {
            if(isRedeemSelectionChanged && selectedPosition.equalsIgnoreCase("1")){
                mAdapter.showUpdatePreAuthPopup();
            }else{
                expandCollapse();
            }
        }
    }

    @Override
    public void expandCollapse() {
        if(isCollapsed && isExpand) {
            if(selectedPosition == currentPosition) {
                isExpand = isExpand;
            } else {
                isExpand = !isExpand;
            }
        }
        imageButtonExpand.setVisibility(isExpand ? VISIBLE : GONE);
        textViewTitle.setText(isExpand ? mTitle.toUpperCase() : mExpandedTitle == null || mExpandedTitle.isEmpty() ? mTitle : mExpandedTitle);
        textViewTitle.setTypeface(isExpand ? null : textViewTitle.getTypeface(), isExpand ? Typeface.NORMAL  : Typeface.BOLD);

        if (mExpandCollapseListener != null) {
            mExpandCollapseListener.onExpandCollapseListener(!isExpand, textViewTitle.getText().toString());
            mExpandCollapseListener.collapseManage(!isExpand, textViewTitle.getText().toString());
        }

        findViewById(R.id.recycler_view).setVisibility(isExpand ? GONE : VISIBLE);
        findViewById(R.id.selected_layout).setVisibility(isExpand ? VISIBLE : GONE);

        if(isExpand){
            collapse(findViewById(R.id.recycler_view));
        } else {
            expand(findViewById(R.id.recycler_view));
        }

        if(mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        isExpand = !isExpand;
        currentPosition = selectedPosition;
    }

    public void collapseExpanded() {
        imageButtonExpand.setVisibility(isExpand ? VISIBLE : VISIBLE);
        textViewTitle.setText(isExpand ? mTitle.toUpperCase() : mTitle.toUpperCase());
        textViewTitle.setTypeface(isExpand ? null : null, isExpand ? Typeface.NORMAL  : Typeface.NORMAL);

        if (mExpandCollapseListener != null)
            mExpandCollapseListener.onExpandCollapseListener(!isExpand, textViewTitle.getText().toString());

        findViewById(R.id.recycler_view).setVisibility(isExpand ? VISIBLE : VISIBLE);
        findViewById(R.id.selected_layout).setVisibility(isExpand ? VISIBLE : VISIBLE);

        collapse(findViewById(R.id.recycler_view));
        isCollapsed = true;
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
        selectedPosition = typedArray.getString(R.styleable.ExpandableCardView_selected_position);
        mTitleBackgroundColor = typedArray.getColor(R.styleable.ExpandableCardView_title_background_color, DEFAULT_TITLE_BACKGROUND_COLOR);
        mCollapseIcon = typedArray.getDrawable(R.styleable.ExpandableCardView_collapse_icon);
        mExpandIcon = typedArray.getDrawable(R.styleable.ExpandableCardView_expand_icon);
        mExpandedTitle = typedArray.getString(R.styleable.ExpandableCardView_expanded_title);
        typedArray.recycle();

    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.expandable_drop_down_layout, this);
        }
    }


    @Override
    public void onRedeemSectionChanged(boolean isRedeemChanged) {
        isRedeemSelectionChanged = isRedeemChanged;
    }

    public void setDropDownData(DropDownAdapter adapter, boolean isFromRedeemPoints){
        mAdapter = adapter;
        mAdapter.setListener(this);

        ((RecyclerView)findViewById(R.id.recycler_view)).setLayoutManager(new LinearLayoutManager(mContext));
        ((RecyclerView)findViewById(R.id.recycler_view)).setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        if(isFromRedeemPoints){

            onSelectValue("$0 off", "0 points" , true, false);
        }else{

            onSelectValue(null, null, false , false);

        }

    }

    @Override
    public void onSelectValue(String header, String subheader, boolean isFromRedeemSection, boolean isRedeemSelectionChanged) {
        String selectedValue = header;
        String selectedSubValue = subheader;

        if(mAdapter != null) {
            selectedValue = mAdapter.getSelectedValue();
            selectedSubValue = mAdapter.getSelectedSubValue();
        }

        boolean selectedRedemptionValue = false;
        if(!TextUtils.isEmpty(selectedValue)) {
            if (selectedValue.equals("$ 0 off")) {
                selectedRedemptionValue = true;
            } else if (selectedValue.equals("0 $ de rabais")) {
                selectedRedemptionValue = true;
            }
        }

        ((TextView)findViewById(R.id.selected_value)).setText(selectedValue);
        ((TextView)findViewById(R.id.selected_subheader_value)).setText(selectedSubValue);

        if(isFromRedeemSection || selectedRedemptionValue){
            ((TextView)findViewById(R.id.selected_value)).setTextColor(ContextCompat.getColor(mContext, R.color.black_60));
        }else{
            ((TextView)findViewById(R.id.selected_value)).setTextColor(ContextCompat.getColor(mContext, R.color.black_80));
        }
        findViewById(R.id.selected_subheader_value).setVisibility(selectedSubValue == null || selectedSubValue.isEmpty() ? GONE : VISIBLE);
        findViewById(R.id.google_pay).setVisibility(GONE);
    }

    public void setRedemptionChange(boolean isRedeemSelectionChanged){
        this.isRedeemSelectionChanged = isRedeemSelectionChanged;
    }

    @Override
    public void onSelectGooglePay(String header) {
        String selectedValue = header;
        if(mAdapter != null) {
            selectedValue = mAdapter.getSelectedValue();
        }
        ((TextView)findViewById(R.id.selected_value)).setText(selectedValue);
        findViewById(R.id.google_pay).setVisibility(VISIBLE);
        findViewById(R.id.selected_subheader_value).setVisibility(GONE);
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


    private void setIcon(Drawable collapseIcon, Drawable expandIcon) {

        if (collapseIcon != null) {
          //  imageButtonCollapse.setImageDrawable(collapseIcon);
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

        v.setVisibility(View.GONE);
       /* final int initialHeight = v.getMeasuredHeight();

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
        v.startAnimation(a);*/
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
       // if (mInnerView != null) return mInnerView;
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