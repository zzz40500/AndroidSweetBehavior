package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zzz40500 on 15/11/14.
 */
public class InAppBarBehavior extends AppBarLayout.Behavior {

    private static final String TAG = "InAppBarBehavior";

    private boolean mIsNested = false;

    private boolean isExpand = false;

    private float mStartY;

    public InAppBarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        final int x = (int) ev.getX();
        final int y = (int) ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getTopAndBottomOffset() == 0) {
                    isExpand = true;
                } else {
                    isExpand = false;
                }
                mStartY = ev.getRawY();
                if (!parent.isPointInChildBounds(child, x, y)) {
                    mIsNested = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (parent.isPointInChildBounds(child, x, y) || ev.getY() - mStartY > 0) {
                    mIsNested = true;
                }
        }
        return super.onInterceptTouchEvent(parent, child, ev);

    }


    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, AppBarLayout child, MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                snapScrollTo(parent, child);
                return true;
        }
        return super.onTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY) {
        if (isExpand) {
            return this.getTopAndBottomOffset() != 0;

        } else {
            return this.getTopAndBottomOffset() != getMaxDragOffset(child);
        }
    }


    public boolean isExpend() {
        return getTopAndBottomOffset() == 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {

        if (mIsNested) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
        snapScrollTo(coordinatorLayout, abl);
    }


    @Override
    public int getMaxDragOffset(AppBarLayout view) {
        return super.getMaxDragOffset(view);
    }

    public void snapScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, boolean isExpand) {

        int offset = 0;
        if (isExpand) {
            offset = 0;
        } else {
            offset = getMaxDragOffset(abl);
        }
        animateOffsetTo(coordinatorLayout, abl, offset);
    }

    private ValueAnimatorCompat mAnimator;


    private void animateOffsetTo(final CoordinatorLayout coordinatorLayout,
                                 final AppBarLayout child, int offset) {
        if (mAnimator == null) {
            mAnimator = ViewUtils.createAnimator();
            mAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
            mAnimator.setUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimatorCompat animator) {

                    setHeaderTopBottomOffset(coordinatorLayout, child,
                            animator.getAnimatedIntValue());
                }
            });
        } else {
            mAnimator.cancel();
        }
        mAnimator.setIntValues(getTopBottomOffsetForScrollingSibling(), offset);
        mAnimator.setDuration(300);
        mAnimator.start();
    }


    private void snapScrollTo(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {

        int distance = -getMaxDragOffset(abl);
        int offset = -getTopAndBottomOffset();
        if (isExpand) {
            snapScroll(coordinatorLayout, abl, offset < distance / 12);
        } else {
            snapScroll(coordinatorLayout, abl, offset < distance * 11 / 12);
        }
    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        if (mIsNested) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        }

    }


    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {

        if (mIsNested) {
            return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
        }
        return false;
    }

    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, AppBarLayout abl, int layoutDirection) {
        mCoordinatorLayout = parent;
        mAppBarLayout = abl;
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    public void setExpanded(boolean expanded, boolean animate) {

        snapScroll(mCoordinatorLayout,mAppBarLayout,expanded);
    }



    @Override
    boolean canDragView(AppBarLayout view) {
        return true;
    }

    public void setIsNest(boolean isNest) {
        this.mIsNested = isNest;
    }

}
