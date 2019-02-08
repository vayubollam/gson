package suncor.com.android.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.internal.CheckableImageButton;
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
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        errorColor = getErrorCurrentTextColors();
        hintDefaultTextColor = getDefaultHintTextColor();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextInputLayout, defStyleAttr, 0);
        errorTextAppearance = a.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
        a.recycle();
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
        } else {
            setDefaultHintTextColor(hintDefaultTextColor);
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

    public void setError(CharSequence error, @DrawableRes int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        setError(error, drawable);
    }

    public void setError(CharSequence error, Drawable drawable) {
        if (!TextUtils.isEmpty(error)) {
            if (errorDrawable == null) {
                errorDrawable = new AppCompatImageView(getContext());
                errorDrawable.setPadding(
                        getEditText().getPaddingLeft(),
                        getEditText().getPaddingTop(),
                        getEditText().getPaddingRight(),
                        getEditText().getPaddingBottom()
                );
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
                inputFrame.addView(errorDrawable, params);
            } else {
                errorDrawable.setVisibility(VISIBLE);
            }
            errorDrawable.setImageDrawable(drawable);
            if (isPasswordVisibilityToggleEnabled()) {
                errorDrawable.post(() -> {
                    if (passwordToggle == null) {
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

}
