package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by zzz40500 on 15/11/14.
 */
public class InNestChildBehavior extends AppBarLayout.ScrollingViewBehavior implements View.OnTouchListener {


    private InAppBarBehavior mDependencyBehavior;

    public InNestChildBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        child.setOnTouchListener(this);
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
            CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
            if( behavior instanceof InAppBarBehavior){
                mDependencyBehavior= (InAppBarBehavior) behavior;
            }
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {




        return super.onDependentViewChanged(parent, child, dependency);


    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(mDependencyBehavior != null) {

                    mDependencyBehavior.setIsNest(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() <= 0) {
                    if(mDependencyBehavior != null) {

                        mDependencyBehavior.setIsNest(true);
                    }
                }
                break;
        }


        return false;
    }
}
