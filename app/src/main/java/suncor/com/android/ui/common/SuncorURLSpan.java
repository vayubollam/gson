package suncor.com.android.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;

import androidx.core.content.res.ResourcesCompat;
import suncor.com.android.R;

public class SuncorURLSpan extends android.text.style.URLSpan {
    Context context;

    public SuncorURLSpan(String url, Context context) {
        super(url);
        this.context = context;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.gibson_semibold);
        ds.setTypeface(typeface);
        ds.setColor(context.getResources().getColor(R.color.red));
    }
}