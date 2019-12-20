package suncor.com.android.uicomponents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

public class SuncorGIFView extends View {

    private InputStream mInputStream;
    private Movie mMovie;
    private float movieWidth, movieHeight, viewWidth, viewHeight;
    private float scaleFactor;
    private long mStart;
    private Context mContext;

    public SuncorGIFView(Context context) {
        super(context);
        this.mContext = context;
    }

    public SuncorGIFView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SuncorGIFView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        if (attrs.getAttributeName(1).equals("background")) {
            int id = Integer.parseInt(attrs.getAttributeValue(1).substring(1));
            setGifImageResource(id);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        scaleFactor = viewHeight / movieHeight;
        setMeasuredDimension((int) viewWidth, (int) viewHeight);
    }

    private void init() {
        setFocusable(true);
        mMovie = Movie.decodeStream(mInputStream);
        movieWidth = mMovie.width();
        movieHeight = mMovie.height();

        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = SystemClock.uptimeMillis();

        if (mStart == 0) {
            mStart = now;
        }

        if (mMovie != null) {

            int duration = mMovie.duration();
            if (duration == 0) duration = 1000;
            int relTime = (int) ((now - mStart) % duration);
            mMovie.setTime(relTime);

            canvas.scale(scaleFactor, scaleFactor);
            float mLeft = (viewWidth - (movieWidth * scaleFactor)) / 2f;
            float mTop = (viewHeight - (movieHeight * scaleFactor)) / 2f;
            mMovie.draw(canvas, mLeft / scaleFactor, mTop / scaleFactor);

            invalidate();
        }
    }

    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

}
