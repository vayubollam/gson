package suncor.com.android.ui;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseBindingMethod;
import androidx.databinding.InverseBindingMethods;

import java.util.HashMap;

import suncor.com.android.model.station.Station;
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
        if (amenitieType == 0 || amenitieType == 1) {
            for (String amenitie : station.getAmenities()) {
                if (amenitiesMap.containsKey(amenitie)) {
                    buffer.append(amenitiesMap.get(amenitie));
                    buffer.append("\n");
                }
            }
        } else {
            for (String amenitie : station.getAmenities()) {
                //For car wash amenities, there is some duplication, to avoid showing multiple entries, we check if we already inserted it
                if (amenitiesMap.containsKey(amenitie) && buffer.indexOf(amenitiesMap.get(amenitie)) == -1) {
                    buffer.append(amenitiesMap.get(amenitie));
                    buffer.append("\n");
                }
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

    @BindingAdapter({"focus"})
    public static void setFocus(View view, boolean hasFocus) {
        if (hasFocus) {
            view.requestFocus();
        } else {
            view.clearFocus();
        }
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

    public interface AfterTextChanged {
        void afterTextChanged(SuncorTextInputLayout input, Editable s);
    }

    @BindingAdapter(value = "background")
    public static void updateBackground(View view, int color) {
        view.setBackgroundColor(color);
    }

    @BindingAdapter(value = "elevation")
    public static void updateElevation(View view, int elevation) {
        ViewCompat.setElevation(view, elevation);
    }

    @BindingAdapter(value = {"android:layout_marginEnd"})
    public static void setLayoutMargin(View view, float marginEnd) {
        if (view.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
            ConstraintLayout.LayoutParams parameter =
                    (ConstraintLayout.LayoutParams) view.getLayoutParams();
            parameter.setMargins(parameter.leftMargin, parameter.topMargin, (int) marginEnd,
                    parameter.bottomMargin);
            view.setLayoutParams(parameter);
        }
    }

    @BindingAdapter(value = {"layout_constraintWidth_default"})
    public static void setConstraintWidthDefault(View view, int matchConstraint) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.matchConstraintDefaultWidth = matchConstraint;
        view.setLayoutParams(params);
    }

    @BindingAdapter(value = {"layout_constraintEnd_toStartOf"})
    public static void setConstraineEndToStartOf(View view, int id) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.endToStart = id;
        view.setLayoutParams(params);
    }

}
