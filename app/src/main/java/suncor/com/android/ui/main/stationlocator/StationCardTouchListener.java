package suncor.com.android.ui.main.stationlocator;

import android.content.Context;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import suncor.com.android.utilities.AnalyticsUtils;

public class StationCardTouchListener implements RecyclerView.OnItemTouchListener {

    GestureDetectorCompat swipeUpDetector;
    private ArrayList<StationItem> items;
    private BottomSheetBehavior bottomSheetBehavior;
    private View child;
    private int position = RecyclerView.NO_POSITION;
    private StationItem item;

    StationCardTouchListener(Fragment fragment, ArrayList<StationItem> items, BottomSheetBehavior behavior) {
        this.items = items;
        this.bottomSheetBehavior = behavior;
        swipeUpDetector = new GestureDetectorCompat(fragment.getContext(), new GestureDetector.SimpleOnGestureListener() {
            boolean isSwipeUpDetected;
            int touchSlop = ViewConfiguration.get(fragment.getContext()).getScaledTouchSlop();
            int flingVelocity = ViewConfiguration.get(fragment.getContext()).getScaledMinimumFlingVelocity();

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
                    if (velocityY > flingVelocity && Math.abs(velocityX) < 10) {
                        isSwipeUpDetected = true;
                        StationDetailsDialog.showCard(fragment, item, child, true);

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
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    AnalyticsUtils.logEvent(fragment.getContext(), "station_details", new Pair<>("location", item.getStation().getAddress().getAddressLine()));

                }

                if (!isSwipeUpDetected) {
                    if (distanceY > touchSlop && Math.abs(distanceX) < 10) {
                        isSwipeUpDetected = true;
                        StationDetailsDialog.showCard(fragment, item, child, true);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
        boolean ret = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = rv.findChildViewUnder(event.getX(), event.getY());
            position = rv.getChildAdapterPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                child = view;
                item = items.get(position);
            }
        }
        if (child != null && item != null) {
            if (swipeUpDetector.onTouchEvent(event) && event.getAction() != MotionEvent.ACTION_DOWN) {
                child.getParent().requestDisallowInterceptTouchEvent(true);
                rv.scrollToPosition(position);
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        //Do nothing
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //Do nothing
    }
}