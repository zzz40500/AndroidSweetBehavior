package com.mingle.weiget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by zzz40500 on 15/9/21.
 */
public class LoadingToolBar extends Toolbar {


    private SwipeProgressBar mProgressBar;
    private int mProgressBarHeight;
    private static final float PROGRESS_BAR_HEIGHT = 4;
    private boolean mRefreshing;
    private  PopupIndicator mPopupIndicator;


    public LoadingToolBar(Context context) {
        super(context);
        init();
    }



    public LoadingToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadingToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        //progressbar 生成
        mProgressBar = new SwipeProgressBar(this);
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        //bar 的高度
        mProgressBarHeight = (int) (metrics.density * PROGRESS_BAR_HEIGHT);
        mPopupIndicator=new PopupIndicator(getContext());
    }



    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            mRefreshing = refreshing;
            if (mRefreshing) {
                mPopupIndicator.showIndicator(this);
//                mProgressBar.start();
            } else {
                mPopupIndicator.dismissIndicator();
//                mProgressBar.stop();
            }
        }
    }


    /**
     * Set the four colors used in the progress animation. The first color will
     * also be the color of the bar that grows in response to a user swipe
     * gesture.
     *
     * @param colorRes1 Color resource.
     * @param colorRes2 Color resource.
     * @param colorRes3 Color resource.
     * @param colorRes4 Color resource.
     */
    public void setColorScheme(int colorRes1, int colorRes2, int colorRes3, int colorRes4) {
        final Resources res = getResources();
        final int color1 = res.getColor(colorRes1);
        final int color2 = res.getColor(colorRes2);
        final int color3 = res.getColor(colorRes3);
        final int color4 = res.getColor(colorRes4);
        mProgressBar.setColorScheme(color1, color2, color3, color4);
        mPopupIndicator.setColorScheme(color1, color2, color3, color4);
    }
    public void draw(Canvas canvas) {
        super.draw(canvas);
        mProgressBar.draw(canvas);
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        mProgressBar.setBounds(0, height-mProgressBarHeight, width, height);
        super.onLayout(changed,left,top,right,bottom);
    }


}
