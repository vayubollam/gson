package suncor.com.android.ui.home.stationlocator;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class StationCardTouchListener implements RecyclerView.OnItemTouchListener {

    private ArrayList<StationItem> items;
    private BottomSheetBehavior bottomSheetBehavior;
    private View child;
    private StationItem item;


    GestureDetectorCompat swipeUpDetector;

    StationCardTouchListener(Fragment fragment, ArrayList<StationItem> items, BottomSheetBehavior behavior) {
        this.items = items;
        this.bottomSheetBehavior = behavior;
        swipeUpDetector = new GestureDetectorCompat(fragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
            boolean isSwipeUpDetected;

            @Override
            public boolean onDown(MotionEvent e) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }
                isSwipeUpDetected = false;
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }

                if (!isSwipeUpDetected) {
                    if (velocityY > 10 && Math.abs(velocityX) < 10) {
                        isSwipeUpDetected = true;
                        StationDetailsDialog.showCard(fragment, item, child);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    return false;
                }

                if (!isSwipeUpDetected) {
                    if (distanceY > 10 && Math.abs(distanceX) < 10) {
                        isSwipeUpDetected = true;
                        StationDetailsDialog.showCard(fragment, item, child);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            child = rv.findChildViewUnder(event.getX(), event.getY());
            item = items.get(rv.getChildAdapterPosition(child));
        }
        return swipeUpDetector.onTouchEvent(event);
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}