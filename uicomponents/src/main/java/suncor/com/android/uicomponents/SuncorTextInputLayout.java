package suncor.com.android.uicomponents;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.TintTypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

@SuppressLint("RestrictedApi")
public class SuncorTextInputLayout extends LinearLayout {

    private static final int LABEL_SCALE_ANIMATION_DURATION = 167;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLabelState(true);
        }
    };
    private final AppCompatCheckBox.OnCheckedChangeListener passwordToggled = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            final int selection = editText.getSelectionEnd();
            if (isChecked && hasPasswordTransformation()) {
                getEditText().setTransformationMethod(null);
            } else {
                getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            editText.setSelection(selection);
        }
    };


    private final ConstraintLayout inputFrame;
    private final AppCompatEditText editText;
    private final AppCompatTextView errorTextView;
    private final AppCompatTextView hintTextView;
    private final AppCompatCheckBox passwordToggle;
    private final AppCompatImageView errorImage;

    private float collapsedHintTextSize;

    private int errorColor;
    private ColorStateList hintTextColor;

    private boolean hintExpanded;

    private ValueAnimator labelSizeAnimator;
    private ColorStateList originalEditTextTint;

    public SuncorTextInputLayout(Context context) {
        this(context, null);
    }

    public SuncorTextInputLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.suncorTextInputStyle);
    }

    public SuncorTextInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        context = getContext();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.suncor_textinput_layout, this, true);

        inputFrame = findViewById(R.id.input_layout);
        editText = findViewById(R.id.edit_text);
        hintTextView = findViewById(R.id.hint_text);
        errorTextView = findViewById(R.id.error_text);
        passwordToggle = findViewById(R.id.password_toggle);
        errorImage = findViewById(R.id.error_image);
        setOrientation(VERTICAL);
        setWillNotDraw(false);
        setAddStatesFromChildren(true);
        setFocusable(true);

        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), attrs, R.styleable.SuncorTextInputLayout, defStyleAttr, 0);
        final int hintAppearance = a.getResourceId(R.styleable.SuncorTextInputLayout_hintTextAppearance, -1);
        if (hintAppearance != -1) {
            setHintTextAppearance(hintAppearance);
        }
        hintTextColor = a.getColorStateList(R.styleable.SuncorTextInputLayout_hintTextColor);
        if (hintTextColor != null) {
            hintTextView.setTextColor(hintTextColor);
        } else {
            hintTextColor = hintTextView.getTextColors();
        }


        final int textAppearance = a.getResourceId(R.styleable.SuncorTextInputLayout_android_textAppearance, -1);
        if (textAppearance != -1) {
            editText.setTextAppearance(context, textAppearance);
        }

        final int errorTextAppearance = a.getResourceId(R.styleable.SuncorTextInputLayout_errorTextAppearance, -1);
        if (errorTextAppearance != -1) {
            errorTextView.setTextAppearance(context, errorTextAppearance);
        }

        hintTextView.setText(a.getText(R.styleable.SuncorTextInputLayout_android_hint));

        boolean passwordToggleEnabled = a.getBoolean(R.styleable.SuncorTextInputLayout_passwordToggleEnabled, false);
        passwordToggle.setVisibility(passwordToggleEnabled ? VISIBLE : GONE);
        passwordToggle.setOnCheckedChangeListener(passwordToggled);

        if (a.hasValue(R.styleable.SuncorTextInputLayout_passwordToggleTint)) {
            passwordToggle.setButtonTintList(
                    ColorStateList.valueOf(a.getColor(R.styleable.SuncorTextInputLayout_passwordToggleTint, -1)));
        }

        Drawable errorDrawable = a.getDrawable(R.styleable.SuncorTextInputLayout_errorDrawable);
        if (errorDrawable != null) {
            errorImage.setImageDrawable(errorDrawable);
        }

        if (a.hasValue(R.styleable.SuncorTextInputLayout_android_maxLength)) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(a.getInt(R.styleable.SuncorTextInputLayout_android_maxLength, -1));
            editText.setFilters(FilterArray);
        }

        editText.setInputType(a.getInt(R.styleable.SuncorTextInputLayout_android_inputType, InputType.TYPE_CLASS_TEXT));

        errorColor = a.getColor(R.styleable.SuncorTextInputLayout_errorColor, Color.RED);

        a.recycle();

        editText.addTextChangedListener(textWatcher);

        inputFrame.setOnClickListener((v) -> {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        });
    }

    public void setError(@StringRes int error) {
        setError(getContext().getString(error));
    }

    public void setError(CharSequence error) {
        if (!TextUtils.isEmpty(error)) {
            errorTextView.setText(error);
            errorTextView.setVisibility(VISIBLE);
            errorImage.setVisibility(VISIBLE);
        } else {
            errorTextView.setText("");
            errorTextView.setVisibility(GONE);
            errorImage.setVisibility(GONE);
        }
        updateBackground();
        updateLabelState(false);
    }

    public AppCompatEditText getEditText() {
        return editText;
    }

    public CharSequence getText() {
        return editText.getText();
    }

    protected ConstraintLayout getInputLayout() {
        return inputFrame;
    }

    @Override
    public void refreshDrawableState() {
        super.refreshDrawableState();
        updateLabelState(ViewCompat.isLaidOut(this) && this.isEnabled());
        updateBackground();
    }

    private void updateBackground() {
        final boolean errorShouldBeShown = !TextUtils.isEmpty(errorTextView.getText());
        if (errorShouldBeShown) {
            inputFrame.setBackgroundResource(R.drawable.textfield_activated);
            inputFrame.setBackgroundTintList(ColorStateList.valueOf(errorColor));
        } else {
            inputFrame.setBackgroundResource(R.drawable.input_field_background);
            inputFrame.setBackgroundTintList(null);
        }
    }

    private void setHintTextAppearance(int resId) {
        hintTextView.setTextAppearance(getContext(), resId);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(getContext(), resId, androidx.appcompat.R.styleable.TextAppearance);
        collapsedHintTextSize = (float) a.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int) hintTextView.getTextSize());
        a.recycle();
        if (editText != null) {
            updateLabelState(false, true);
        }
    }

    private void updateLabelState(boolean animate) {
        updateLabelState(animate, false);
    }

    private void updateLabelState(boolean animate, boolean force) {
        final boolean isEnabled = isEnabled();
        final boolean hasText = !TextUtils.isEmpty(editText.getText());
        final boolean hasFocus = editText.hasFocus();
        final boolean errorShouldBeShown = !TextUtils.isEmpty(errorTextView.getText());

        // Set the collapsed and expanded label text colors based on the current state.
        if (!isEnabled) {
            //TODO handle disabled state
        } else if (errorShouldBeShown) {
            hintTextView.setTextColor(errorColor);
        } else if (hintTextColor != null) {
            hintTextView.setTextColor(hintTextColor);
        }

        if (hasText || (isEnabled() && (hasFocus) && !errorShouldBeShown)) {
            // We should be showing the label so do so if it isn't already
            if (force || hintExpanded) {
                collapseHint(animate);
            }
        } else {
            // We should not be showing the label so hide it
            if (force || !hintExpanded) {
                expandHint(animate);
            }
        }
    }

    private void collapseHint(boolean animate) {
        if (labelSizeAnimator != null) {
            labelSizeAnimator.cancel();
        }
        TransitionManager.endTransitions(inputFrame);

        ConstraintSet set = new ConstraintSet();
        set.clone(inputFrame);
        set.addToVerticalChain(editText.getId(), hintTextView.getId(), ConstraintSet.PARENT_ID);
        set.setMargin(hintTextView.getId(), ConstraintSet.BOTTOM, 4);
        set.setVerticalChainStyle(editText.getId(), ConstraintSet.CHAIN_PACKED);
        set.setVerticalChainStyle(hintTextView.getId(), ConstraintSet.CHAIN_PACKED);

        if (animate) {
            Transition transition = new AutoTransition();
            transition.setDuration(LABEL_SCALE_ANIMATION_DURATION);
            TransitionManager.beginDelayedTransition(inputFrame, transition);
            animateLabelText(collapsedHintTextSize);
        } else {
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, collapsedHintTextSize);
        }
        set.applyTo(inputFrame);

        hintExpanded = false;
    }

    private void expandHint(boolean animate) {
        if (labelSizeAnimator != null) {
            labelSizeAnimator.cancel();
        }
        TransitionManager.endTransitions(inputFrame);

        ConstraintSet set = new ConstraintSet();
        set.clone(inputFrame);
        set.connect(editText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);

        set.connect(hintTextView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.setMargin(hintTextView.getId(), ConstraintSet.BOTTOM, 0);

        if (animate) {
            Transition transition = new AutoTransition();
            transition.setDuration(LABEL_SCALE_ANIMATION_DURATION);
            TransitionManager.beginDelayedTransition(inputFrame, transition);
            animateLabelText(editText.getTextSize());
        } else {
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, editText.getTextSize());
        }
        set.applyTo(inputFrame);

        hintExpanded = true;
    }

    private void animateLabelText(float textSize) {
        labelSizeAnimator = ObjectAnimator.ofFloat(hintTextView.getTextSize(), textSize).setDuration(LABEL_SCALE_ANIMATION_DURATION);
        labelSizeAnimator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            hintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animatedValue);
        });
        labelSizeAnimator.start();
    }

    private boolean hasPasswordTransformation() {
        return editText != null
                && editText.getTransformationMethod() instanceof PasswordTransformationMethod;
    }
}
