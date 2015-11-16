package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zzz40500 on 15/11/14.
 */
public class InAppBarBehavior extends AppBarLayout.Behavior {


    private boolean isNest = false;
    private boolean mWasFlung;

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
                    isNest = false;
                } else {
                    return super.onInterceptTouchEvent(parent, child, ev);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (parent.isPointInChildBounds(child, x, y) || ev.getY() - mStartY > 0) {

                    isNest = true;

                    return super.onInterceptTouchEvent(parent, child, ev);
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

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }


    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dx, int dy, int[] consumed) {

        if (isNest) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        }
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, View target) {
//        if(!mWasFlung){

//        target.setPadding(0,0,0,getMaxDragOffset(abl));
        snapScrollTo(coordinatorLayout, abl);
//        }else {
//
//            super.onStopNestedScroll(coordinatorLayout, abl, target);
//        }

    }


    @Override
    public int getMaxDragOffset(AppBarLayout view) {
        return super.getMaxDragOffset(view);
    }

    public void snapScroll(CoordinatorLayout coordinatorLayout, AppBarLayout abl, boolean isExpand){



//        if(this.isExpand != isExpand) {

            int offset =0;


            if(isExpand){
                offset=0;
            }else{
                offset = getMaxDragOffset(abl);

            }

            try {
                Method method = AppBarLayout.Behavior.class.getDeclaredMethod("animateOffsetTo", CoordinatorLayout.class, AppBarLayout.class, int.class);
                method.setAccessible(true);
                method.invoke(this, coordinatorLayout, abl, offset);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
//        }







    }


    private void snapScrollTo(CoordinatorLayout coordinatorLayout, AppBarLayout abl) {







            int distance = -getMaxDragOffset(abl);

            int offset = -getTopAndBottomOffset();
            if (isExpand) {
                snapScroll(coordinatorLayout,abl, offset < distance / 5 );
            } else {
                snapScroll(coordinatorLayout,abl, offset < distance * 4 / 5  );

            }


    }


    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {



        if(dyUnconsumed>0){
            isNest=true;
            snapScroll(coordinatorLayout,child,true);
        }
//        if (isNest) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);

//        }

    }


    @Override
    public boolean onNestedFling(CoordinatorLayout coordinatorLayout, AppBarLayout child, View target, float velocityX, float velocityY, boolean consumed) {


        if (isNest) {

            mWasFlung = super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
            return mWasFlung;
        }
        return false;
    }


    @Override
    boolean canDragView(AppBarLayout view) {
        return true;
    }

    public void setIsNest(boolean isNest) {
        this.isNest = isNest;
    }

}
