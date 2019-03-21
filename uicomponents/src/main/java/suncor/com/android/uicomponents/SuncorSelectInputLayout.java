package suncor.com.android.uicomponents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;

@SuppressLint("RestrictedApi")
public class SuncorSelectInputLayout extends SuncorTextInputLayout {

    private AppCompatImageView chevronImage;

    public SuncorSelectInputLayout(Context context) {
        this(context, null);
    }

    public SuncorSelectInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuncorSelectInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // getEditText().setInputType(InputType.TYPE_NULL);
        getEditText().setFocusable(false);

        chevronImage = new AppCompatImageView(getContext());
        chevronImage.setId(R.id.chevron);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

        getInputLayout().addView(chevronImage, params);
        ConstraintLayout.LayoutParams inputParams = (ConstraintLayout.LayoutParams) getEditText().getLayoutParams();
        inputParams.endToStart = chevronImage.getId();
        getEditText().setMaxLines(2);
        getEditText().setLayoutParams(inputParams);
        getEditText().setHorizontallyScrolling(false);
        Drawable drawable = getResources().getDrawable(R.drawable.chevron_tinted);
        chevronImage.setImageDrawable(drawable);


    }


    @Override
    public void setText(CharSequence text) {
        super.setText(text);
        getEditText().post(() -> {
            if (getEditText().getLineCount() == 2) {
                getInputLayout().getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.select_input_two_lines_height);
            }
        });

    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        getInputLayout().setOnClickListener(l);
        getEditText().setOnClickListener(l);
    }

    @Override
    public void refreshDrawableState() {
        super.refreshDrawableState();
        if (chevronImage != null) {
            Drawable drawable = chevronImage.getDrawable().mutate();
            drawable.setState(getEditText().getDrawableState());
            chevronImage.setImageDrawable(drawable);
        }
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);
        if (!TextUtils.isEmpty(error)) {
            chevronImage.setVisibility(GONE);
        } else {
            chevronImage.setVisibility(VISIBLE);
        }
    }

}
