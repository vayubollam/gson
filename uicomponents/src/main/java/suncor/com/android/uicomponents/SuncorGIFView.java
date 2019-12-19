package suncor.com.android.uicomponents;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
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

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        float height = Resources.getSystem().getDisplayMetrics().heightPixels - convertDpToPixel(180, mContext);
//        float factor = height / mHeight;
//        float width = mWidth * factor;
//        setMeasuredDimension((int) width, (int) height);
//    }


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

            int height = getHeight();
            int scaleFactor = height / mHeight;
//            int padding = (Resources.getSystem().getDisplayMetrics().widthPixels - (mWidth * scaleFactor)) / 4;
//            Log.i("TTT", padding + " " + Resources.getSystem().getDisplayMetrics().widthPixels);

            canvas.scale(scaleFactor, scaleFactor);
            // canvas.translate(padding, 0);

            mMovie.setTime(relTime);
            mMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }

    public void setGifImageResource(int id) {
        mInputStream = mContext.getResources().openRawResource(id);
        init();
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
