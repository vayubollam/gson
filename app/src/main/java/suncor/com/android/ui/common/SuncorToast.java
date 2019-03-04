package suncor.com.android.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;
import suncor.com.android.R;

public class SuncorToast {

    public static Toast makeText(Context context, @StringRes int text, int duration) {
        return makeText(context, context.getString(text), duration);
    }

    public static Toast makeText(Context context, String text, int duration) {
        Toast toast = new Toast(context);
        View view = LayoutInflater.from(context).inflate(R.layout.toast_ui, null);
        TextView textView = view.findViewById(R.id.text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(duration);
        return toast;
    }
}
