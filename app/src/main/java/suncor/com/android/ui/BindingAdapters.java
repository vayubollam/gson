package suncor.com.android.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import java.util.HashMap;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;
import suncor.com.android.model.Station;
import suncor.com.android.uicomponents.SuncorTextInputLayout;

@InverseBindingMethods({
        @InverseBindingMethod(type = SuncorTextInputLayout.class, attribute = "text"),
})

public class BindingAdapters {
    @BindingAdapter({"station", "amenitieType"})
    public static void setAmenities(AppCompatTextView view, Station station, int amenitieType) {
        if (station == null) {
            return;
        }
        StringBuilder buffer = new StringBuilder();
        HashMap<String, String> amenitiesMap = null;
        switch (amenitieType) {
            case 0:
                amenitiesMap = Station.SERVICE_AMENITIES;
                break;
            case 1:
                amenitiesMap = Station.FUEL_AMENITIES;
                break;
            case 2:
                amenitiesMap = Station.WASH_AMENITIES;
                break;
        }
        for (String amenitie : station.getAmenities()) {
            if (amenitiesMap.containsKey(amenitie)) {
                buffer.append(amenitiesMap.get(amenitie));
                buffer.append("\n");
            }
        }
        view.setText(buffer.toString().trim());
    }

    @BindingAdapter({"visibleGone"})
    public static void showDelete(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter({"visibleInvisible"})
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @BindingAdapter({"android:drawableTint"})
    public static void setDrawableTint(AppCompatTextView view, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setCompoundDrawableTintList(ColorStateList.valueOf(color));
        } else {
            Drawable drawable = view.getCompoundDrawables()[0];
            drawable = drawable.mutate();
            drawable.setTint(color);
            view.setCompoundDrawables(drawable, view.getCompoundDrawables()[1], view.getCompoundDrawables()[2], view.getCompoundDrawables()[3]);
        }
    }

    @BindingAdapter({"afterTextChanged"})
    public static void afterTextChanged(SuncorTextInputLayout input, AfterTextChanged listener) {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listener != null) {
                    listener.afterTextChanged(input, s);
                }
            }
        };
        input.getEditText().addTextChangedListener(watcher);
    }

    @BindingAdapter({"focusChanged"})
    public static void showHide(SuncorTextInputLayout view, View.OnFocusChangeListener focusChangeListener) {
        view.getEditText().setOnFocusChangeListener((v, hasFocus) -> focusChangeListener.onFocusChange(view, hasFocus));
    }

    @BindingAdapter(value = "textAttrChanged")
    public static void setListener(SuncorTextInputLayout errorInputLayout, final InverseBindingListener textAttrChanged) {
        if (textAttrChanged != null) {
            errorInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    textAttrChanged.onChange();
                }
            });
        }
    }

    @BindingAdapter(value = "showError")
    public static void showError(SuncorTextInputLayout inputLayout, boolean showError) {
        inputLayout.setError(showError);
    }

//    @BindingAdapter(value = "animateVisibleGone")
//    public static void animateVisibleGone(View view, boolean visible) {
//        ConstraintLayout parent = (ConstraintLayout) view.getParent();
//        ConstraintSet set = new ConstraintSet();
//        set.clone(parent);
//        set.setVisibility(view.getId(), visible ? View.VISIBLE : View.GONE);
//        TransitionManager.beginDelayedTransition(parent);
//        set.applyTo(parent);
//    }

    public interface AfterTextChanged {
        void afterTextChanged(SuncorTextInputLayout input, Editable s);
    }
}
