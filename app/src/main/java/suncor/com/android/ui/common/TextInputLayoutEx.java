package suncor.com.android.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.material.internal.CheckableImageButton;
import com.google.android.material.internal.CollapsingTextHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import suncor.com.android.R;

public class TextInputLayoutEx extends TextInputLayout {

    private FrameLayout inputFrame;
    @ColorInt
    private int errorColor;

    @ColorInt
    private int errorLabelColor;
    private ColorStateList hintDefaultTextColor;
    private AppCompatImageView errorDrawable;
    private CheckableImageButton passwordToggle;
    private CollapsingTextHelper collapsingTextHelper;
    private int initialHeight;

    private int errorTextAppearance;

    private int originalPasswordToggleMinWidth;

    public TextInputLayoutEx(Context context) {
        this(context, null);
    }

    public TextInputLayoutEx(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.textInputStyle);
    }

    public TextInputLayoutEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        try {
            Field field = getClass().getSuperclass().getDeclaredField("inputFrame");
            field.setAccessible(true);
            inputFrame = (FrameLayout) field.get(this);
            field = getClass().getSuperclass().getDeclaredField("collapsingTextHelper");
            field.setAccessible(true);
            collapsingTextHelper = (CollapsingTextHelper) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        errorColor = getResources().getColor(R.color.red);//getErrorCurrentTextColors();
        hintDefaultTextColor = getDefaultHintTextColor();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextInputLayout, defStyleAttr, 0);
        errorTextAppearance = a.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        a.recycle();
        post(() -> {
            initialHeight = getMeasuredHeight();
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int l = this.getEditText().getLeft() + this.getEditText().getCompoundPaddingLeft();
        int r = this.getEditText().getRight() - this.getEditText().getCompoundPaddingRight();
        int t = this.getEditText().getTop() + this.getEditText().getCompoundPaddingTop() - getResources().getDimensionPixelOffset(R.dimen.space_between_hint_and_label);
        this.collapsingTextHelper.setCollapsedBounds(l, t, r, bottom - top - this.getPaddingBottom());
        this.collapsingTextHelper.recalculate();
    }

    @Override
    public void setError(@Nullable CharSequence errorText) {
        if (errorLabelColor == getErrorCurrentTextColors()) {
            setErrorTextAppearance(errorTextAppearance);
        }
        super.setError(errorText);
        if (!TextUtils.isEmpty(errorText)) {
            setDefaultHintTextColor(ColorStateList.valueOf(errorColor));
            setErrorTextColor(ColorStateList.valueOf(errorLabelColor));
            if (getEditText().getText().toString().isEmpty()) {
                expandHint();
            }
            applyBottomPadding(true);
        } else {
            setDefaultHintTextColor(hintDefaultTextColor);
            setErrorEnabled(false);
            applyBottomPadding(false);
            if (errorDrawable != null) {
                errorDrawable.setVisibility(GONE);
                if (passwordToggle != null) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) passwordToggle.getLayoutParams();
                    params.rightMargin = 0;
                    passwordToggle.setLayoutParams(params);
                    passwordToggle.setMinimumWidth(originalPasswordToggleMinWidth);
                }
            }
        }
    }

    private void applyBottomPadding(boolean b) {
        setPadding(
                getPaddingLeft(),
                getPaddingTop(),
                getPaddingRight(),
                b ? getResources().getDimensionPixelOffset(R.dimen.error_hint_spacing) : 0
        );
        getLayoutParams().height = b ? ViewGroup.LayoutParams.WRAP_CONTENT : initialHeight;
    }

    public void setError(CharSequence error, @DrawableRes int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        setError(error, drawable);
    }

    public void setError(CharSequence error, Drawable drawable) {
        if (!TextUtils.isEmpty(error)) {
            if (errorDrawable == null) {
                errorDrawable = new AppCompatImageView(getContext());
                errorDrawable.setPadding(
                        0,
                        getResources().getDimensionPixelOffset(R.dimen.edit_text_top_padding_hint_expanded),
                        getEditText().getPaddingRight(),
                        0
                );

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.TOP | Gravity.END;
                inputFrame.addView(errorDrawable, params);
            } else {
                errorDrawable.setVisibility(VISIBLE);
            }
            errorDrawable.setImageDrawable(drawable);
            if (isPasswordVisibilityToggleEnabled()) {
                errorDrawable.post(() -> {
                    if (passwordToggle != null) {
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) passwordToggle.getLayoutParams();
                        params.rightMargin = errorDrawable.getMeasuredWidth();
                        passwordToggle.setLayoutParams(params);
                        if (originalPasswordToggleMinWidth == 0) {
                            originalPasswordToggleMinWidth = passwordToggle.getMinimumWidth();
                        }
                        passwordToggle.setMinimumWidth(0);
                    }
                });
            }
        } else if (errorDrawable != null) {
            errorDrawable.setVisibility(GONE);
            if (passwordToggle != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) passwordToggle.getLayoutParams();
                params.rightMargin = 0;
                passwordToggle.setMinimumWidth(originalPasswordToggleMinWidth);
            }
        }
        setError(error);
    }

    public void setErrorLabelColor(@ColorInt int errorLabelColor) {
        this.errorLabelColor = errorLabelColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isPasswordVisibilityToggleEnabled() && passwordToggle == null) {
            try {
                Field field = getClass().getSuperclass().getDeclaredField("passwordToggleView");
                field.setAccessible(true);
                passwordToggle = (CheckableImageButton) field.get(this);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (passwordToggle != null) {
            passwordToggle.setPadding(
                    passwordToggle.getPaddingLeft(),
                    getResources().getDimensionPixelOffset(R.dimen.edit_text_top_padding_hint_expanded),
                    passwordToggle.getPaddingRight(),
                    getResources().getDimensionPixelOffset(R.dimen.edit_text_bottom_padding_hint_expanded)
            );
            passwordToggle.setBackground(null);

            passwordToggle.requestLayout();
        }

        if (getEditText() != null) {
            getEditText().setPadding(
                    getEditText().getPaddingLeft(),
                    getResources().getDimensionPixelOffset(isHintExpanded() ? R.dimen.edit_text_top_padding_hint_expanded : R.dimen.edit_text_top_padding_hint_collapsed),
                    getEditText().getPaddingRight(),
                    getResources().getDimensionPixelOffset(isHintExpanded() ? R.dimen.edit_text_bottom_padding_hint_expanded : R.dimen.edit_text_bottom_padding_hint_collapsed));
        }
    }

    @Override
    public void refreshDrawableState() {
        if (!TextUtils.isEmpty(getError())) {
            setErrorTextAppearance(errorTextAppearance);
        }
        super.refreshDrawableState();
        if (!TextUtils.isEmpty(getError())) {
            setDefaultHintTextColor(ColorStateList.valueOf(errorColor));
            setErrorTextColor(ColorStateList.valueOf(errorLabelColor));
            if (getEditText().getText().toString().isEmpty()) {
                expandHint();
            }
        }

        if (getEditText() != null) {
            getEditText().setPadding(
                    getEditText().getPaddingLeft(),
                    getResources().getDimensionPixelOffset(isHintExpanded() ? R.dimen.edit_text_top_padding_hint_expanded : R.dimen.edit_text_top_padding_hint_collapsed),
                    getEditText().getPaddingRight(),
                    getResources().getDimensionPixelOffset(isHintExpanded() ? R.dimen.edit_text_bottom_padding_hint_expanded : R.dimen.edit_text_bottom_padding_hint_collapsed));
        }
    }

    private void expandHint() {
        try {
            Method m = getClass().getSuperclass().getDeclaredMethod("expandHint", new Class<?>[]{boolean.class});
            m.setAccessible(true);
            m.invoke(this, false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private boolean isHintExpanded() {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("hintExpanded");
            field.setAccessible(true);
            return (boolean) field.get(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}
