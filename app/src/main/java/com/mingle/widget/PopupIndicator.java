package com.mingle.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.support.v4.view.GravityCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by zzz40500 on 15/9/25.
 */
public class PopupIndicator implements SwipeProgressBar.AnimListener {



    private final WindowManager mWindowManager;
    private boolean mShowing;
    Point screenSize = new Point();
    private int[] mDrawingLocation = new int[2];

    private SwipeProgressView mSwipeProgressView;



    public PopupIndicator(Context context) {
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenSize.set(displayMetrics.widthPixels, displayMetrics.heightPixels);
        mSwipeProgressView=new SwipeProgressView(context);
        mSwipeProgressView.setOnAnimListener(this);
    }
    public boolean isShowing() {
        return mShowing;
    }


    public void showIndicator(View parent) {
        if (isShowing()) {
//            mPopupView.mMarker.animateOpen();
            return;
        }

        IBinder windowToken = parent.getWindowToken();

        if (windowToken != null) {
            WindowManager.LayoutParams p = createPopupLayout(windowToken);

            p.gravity = Gravity.TOP | GravityCompat.START;
            updateLayoutParamsForPosiion(parent, p);
            mShowing = true;
            invokePopup(p);
        }

    }

    public void dismissIndicator(){

        mSwipeProgressView.
                setRefreshing(false);

    }


    private void measureFloater() {
        int specWidth = View.MeasureSpec.makeMeasureSpec(screenSize.x, View.MeasureSpec.EXACTLY);
        int specHeight = View.MeasureSpec.makeMeasureSpec(screenSize.y, View.MeasureSpec.AT_MOST);
        mSwipeProgressView.measure(specWidth, specHeight);
    }

    private void invokePopup(WindowManager.LayoutParams p) {
        mWindowManager.addView(mSwipeProgressView, p);
        mSwipeProgressView.
                setRefreshing(true);
    }
    private void updateLayoutParamsForPosiion(View anchor, WindowManager.LayoutParams p) {
//        measureFloater();
        int measuredHeight = mSwipeProgressView.getMeasuredHeight();
        anchor.getLocationInWindow(mDrawingLocation);
        p.x = 0;
        p.y = mDrawingLocation[1]+anchor.getMeasuredHeight() ;
        p.width = screenSize.x;
        p.height = 12;


    }


    private WindowManager.LayoutParams createPopupLayout(IBinder token) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.MATCH_PARENT;
        p.format = PixelFormat.TRANSLUCENT;
        p.flags = computeFlags(p.flags);
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.token = token;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN;
        return p;
    }

    /**
     * I'm NOT completely sure how all this bitwise things work...
     *
     * @param curFlags
     * @return
     */
    private int computeFlags(int curFlags) {
        curFlags &= ~(
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        curFlags |= WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        curFlags |= WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        return curFlags;
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        mShowing = false;
        if(mSwipeProgressView.getParent()!= null) {
            mWindowManager.removeView(mSwipeProgressView);
        }
    }

    void setColorScheme(int color1, int color2, int color3, int color4) {
        mSwipeProgressView.setColorScheme(color1, color2, color3, color4);

    }
}
