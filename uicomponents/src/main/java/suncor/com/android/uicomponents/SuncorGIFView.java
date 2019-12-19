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
    private int mWidth, mHeight;
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

    private void init() {
        setFocusable(true);
        mMovie = Movie.decodeStream(mInputStream);
        mWidth = mMovie.width();
        mHeight = mMovie.height();

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
            if (duration == 0) {
                duration = 1000;
            }

            int relTime = (int) ((now - mStart) % duration);
            mMovie.setTime(relTime);

            int height = getHeight();
            int scaleFactor = height / mHeight;
            canvas.scale(scaleFactor, scaleFactor);
            int mLeft = (getWidth() - (mWidth * scaleFactor)) / 2;
            int mTop = (getHeight() - (mHeight * scaleFactor)) / 2;

            mMovie.draw(canvas, mLeft / scaleFactor, mTop / scaleFactor);

            invalidate();
        }
    }

    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

}
