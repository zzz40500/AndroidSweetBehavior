package android.support.design.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by zzz40500 on 15/11/14.
 */
public class InNestChildBehavior extends AppBarLayout.ScrollingViewBehavior implements View.OnTouchListener {


    private InAppBarBehavior mDependencyBehavior;
    private View mSelfView;

    public InNestChildBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        child.setOnTouchListener(this);
        mSelfView = child;
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {

        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
            CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
            if (behavior instanceof InAppBarBehavior) {
                mDependencyBehavior = (InAppBarBehavior) behavior;
            }
        }
        return super.layoutDependsOn(parent, child, dependency);
    }


    /**
     * rv 可以设置最大的 paddingBottom
     */
    private int mMaxPadding;


    @Override
    public boolean onDependentViewChanged(final CoordinatorLayout parent, final View child, final View dependency) {



        final CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) dependency.getLayoutParams()).getBehavior();

        super.onDependentViewChanged(parent, child, dependency);


        if (mMaxPadding == 0)

            mMaxPadding = dependency.getMeasuredHeight() + mDependencyBehavior.getMaxDragOffset((AppBarLayout) dependency);

        if (mOffset == 0) {
            mSelfView.setPadding(0, 0, 0, mOffset);
        } else {
            ViewCompat.postOnAnimation(mSelfView, new Runnable() {
                @Override
                public void run() {

                    mSelfView.removeCallbacks(mRunnable);
                    mSelfView.post(mRunnable);

//                    mDependencyBehavior.setIsNest(false);
                }
            });
        }
        return true;
    }


    public void smoothScrollToView(final View view, final RecyclerView rv) {



        if(rv !=  mSelfView){
            return ;
        }

        Rect childRect = new Rect();
        view.getGlobalVisibleRect(childRect);
        Rect rVRect = new Rect();
        rv.getGlobalVisibleRect(rVRect);

        final int scrollBy = Math.max(childRect.top, childRect.bottom - view.getHeight()) - rVRect.top;


        if (scrollBy <= 0) {
            rv.smoothScrollBy(0, scrollBy);

        } else {


            if (mDependencyBehavior.isExpend()) {
                rv.smoothScrollBy(0, scrollBy);

            } else {
                rv.smoothScrollBy(0, scrollBy);
                rv.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Rect childRect = new Rect();
                        view.getGlobalVisibleRect(childRect);
                        Rect rVRect = new Rect();
                        rv.getGlobalVisibleRect(rVRect);


                        final int scrollBy = Math.max(childRect.top, childRect.bottom - view.getHeight()) - rVRect.top;
                        if (scrollBy != 0) {
                            rv.smoothScrollBy(0, scrollBy);
                        }

                    }
                }, 300);
            }
        }


    }


    public Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mSelfView.getPaddingBottom() != mOffset - mMaxPadding) {


                if (mSelfView.getPaddingBottom() >= mOffset - mMaxPadding) {

                    mSelfView.setPadding(0, 0, 0,
                            mSelfView.getPaddingBottom() + (mOffset - mMaxPadding - mSelfView.getPaddingBottom()) / 9
                    );
                } else {
                    mSelfView.setPadding(0, 0, 0,
                            mOffset - mMaxPadding
                    );
                }

                mSelfView.postDelayed(this, 10);
            }
        }
    };


    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mDependencyBehavior != null) {
                    mDependencyBehavior.setIsNest(false);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (ev.getY() <= 0) {
                    if (mDependencyBehavior != null) {
                        mDependencyBehavior.setIsNest(true);
                    }
                }
                break;
        }
        return false;
    }


    private int mOffset;

    @Override
    public boolean setTopAndBottomOffset(int offset) {

        mOffset = offset;
        return super.setTopAndBottomOffset(offset);
    }


}
