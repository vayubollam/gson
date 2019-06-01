package suncor.com.android.ui.common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;
import suncor.com.android.R;

public class LinkableTextView extends AppCompatTextView {

    public LinkableTextView(Context context) {
        super(context);
    }

    public LinkableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinkableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        int autolinkmasks = getAutoLinkMask();
        //disable auto link, to avoid reverting to default URLSpan
        setAutoLinkMask(0);
        URLSpan[] spans = getUrls();
        Spannable s = new SpannableString(getText());
        for (URLSpan span : spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new SuncorURLSpan(span.getURL(), getContext());
            s.setSpan(span, start, end, 0);
        }
        super.setText(s, type);
        //restore autolink mask value
        setAutoLinkMask(autolinkmasks);
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    class SuncorURLSpan extends android.text.style.URLSpan {
        Context context;

        public SuncorURLSpan(String url, Context context) {
            super(url);
            this.context = context;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.gibson_semibold);
            ds.setTypeface(typeface);
            ds.setColor(getLinkTextColors().getDefaultColor());
        }
    }
}
