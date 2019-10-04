package suncor.com.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import suncor.com.android.R;

public class ActionMenuCardView extends CardView {

    public ActionMenuCardView(@NonNull Context context) {
        super(context);
    }

    public ActionMenuCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray attr = context.obtainStyledAttributes(attrs,
                R.styleable.ActionMenuCardView, 0, 0);
        String buttonName = attr.getString(R.styleable.ActionMenuCardView_button_text);
        Drawable iconSrc = attr.getDrawable(R.styleable.ActionMenuCardView_icon_src);
        attr.recycle();

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.action_menu_card_view, this, true);

        ImageView icon = findViewById(R.id.left_icon);
        TextView buttonText = findViewById(R.id.button_text);

        icon.setImageDrawable(iconSrc);
        buttonText.setText(buttonName);

        setElevation(getResources().getDimension(R.dimen.action_menu_card_view_elevation));
        setRadius(getResources().getDimension(R.dimen.action_menu_card_view_radius));

    }

}
