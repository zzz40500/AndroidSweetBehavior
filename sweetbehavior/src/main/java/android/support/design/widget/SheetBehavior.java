/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.design.widget;

import com.mingle.sweetbehavior.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;


/**
 * 在原本的基础上支持向下
 * An interaction behavior plugin for a child view of {@link CoordinatorLayout} to make it work as
 * a bottom sheet.
 */
public class SheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {


    /**
     * Callback for monitoring events about bottom sheets.
     */
    public abstract static class BottomSheetCallback {

        /**
         * Called when the bottom sheet changes its state.
         *
         * @param bottomSheet The bottom sheet view.
         * @param newState    The new state. This will be one of {@link #STATE_DRAGGING},
         *                    {@link #STATE_SETTLING}, {@link #STATE_EXPANDED},
         *                    {@link #STATE_COLLAPSED}, or {@link #STATE_HIDDEN}.
         */
        public abstract void onStateChanged(@NonNull View bottomSheet, @State int newState);

        /**
         * Called when the bottom sheet is being dragged.
         *
         * @param bottomSheet The bottom sheet view.
         * @param slideOffset The new offset of this bottom sheet within its range, from 0 to 1
         *                    when it is moving upward, and from 0 to -1 when it moving downward.
         */
        public abstract void onSlide(@NonNull View bottomSheet, float slideOffset);
    }

    /**
     * The bottom sheet is dragging.
     */
    public static final int STATE_DRAGGING = 1;

    /**
     * The bottom sheet is settling.
     */
    public static final int STATE_SETTLING = 2;

    /**
     * The bottom sheet is expanded.
     */
    public static final int STATE_EXPANDED = 3;

    /**
     * The bottom sheet is collapsed.
     */
    public static final int STATE_COLLAPSED = 4;

    /**
     * The bottom sheet is hidden.
     */
    public static final int STATE_HIDDEN = 5;


    public static final int TOP_SHEET = 1;
    public static final int BOTTOM_SHEET = 2;


    /**
     * @hide
     */
    @IntDef({TOP_SHEET, BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SlideMode {
    }

    /**
     * @hide
     */
    @IntDef({STATE_EXPANDED, STATE_COLLAPSED, STATE_DRAGGING, STATE_SETTLING, STATE_HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }


    private static final float HIDE_THRESHOLD = 0.5f;

    private static final float HIDE_FRICTION = 0.1f;

    private float mMaximumVelocity;

    private int mPeekHeight;


    private boolean mHideable;

    @State
    private int mState = STATE_COLLAPSED;

    private ViewDragHelper mViewDragHelper;

    private boolean mIgnoreEvents;

    private int mLastNestedScrollDy;

    private boolean mNestedScrolled;

    private int mParentHeight;

    private WeakReference<V> mViewRef;

    private WeakReference<View> mNestedScrollingChildRef;

    private BottomSheetCallback mCallback;

    private VelocityTracker mVelocityTracker;

    private int mActivePointerId;

    private int mInitialY;

    private
    @SheetBehavior.SlideMode
    int mSlideModel = BOTTOM_SHEET;

    private SlideHelper mSlideHelper;


    private boolean mTouchingScrollingChild;

    /**
     * Default constructor for instantiating BottomSheetBehaviors.
     */
    public SheetBehavior() {
    }

    /**
     * Default constructor for inflating BottomSheetBehaviors from layout.
     *
     * @param context The {@link Context}.
     * @param attrs   The {@link AttributeSet}.
     */
    public SheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SheetBehavior);
        setPeekHeight(a.getDimensionPixelSize(
                R.styleable.SheetBehavior_peekHeight, 0));
        setHideable(a.getBoolean(R.styleable.SheetBehavior_hiddenEnable, true));
        mSlideModel = a.getInt(R.styleable.SheetBehavior_slideMode, BOTTOM_SHEET);
        a.recycle();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    @Override
    public Parcelable onSaveInstanceState(CoordinatorLayout parent, V child) {
        return new SavedState(super.onSaveInstanceState(parent, child), mState);
    }

    @Override
    public void onRestoreInstanceState(CoordinatorLayout parent, V child, Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(parent, child, ss.getSuperState());
        // Intermediate states are restored as collapsed state
        if (ss.state == STATE_DRAGGING || ss.state == STATE_SETTLING) {
            mState = STATE_COLLAPSED;
        } else {
            mState = ss.state;
        }
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // First let the parent lay it out
        if (mState != STATE_DRAGGING && mState != STATE_SETTLING) {
            parent.onLayoutChild(child, layoutDirection);
        }
        if (mSlideHelper == null) {
            mSlideHelper = createSlideHelper(mSlideModel);
        }
        if (mViewDragHelper == null) {
            mViewDragHelper = ViewDragHelper.create(parent, mDragCallback);
        }

        // Offset the bottom sheet
        mSlideHelper.onLayoutChild(child);

        mViewRef = new WeakReference<>(child);
        mNestedScrollingChildRef = new WeakReference<>(findScrollingChild(child));
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchingScrollingChild = false;
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                // Reset the ignore flag
                if (mIgnoreEvents) {
                    mIgnoreEvents = false;
                    return false;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mInitialY = (int) event.getY();
                int initialX = (int) event.getX();
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && parent.isPointInChildBounds(scroll, initialX, mInitialY)) {
                    mActivePointerId = event.getPointerId(event.getActionIndex());
                    mTouchingScrollingChild = true;
                }
                mIgnoreEvents = mActivePointerId == MotionEvent.INVALID_POINTER_ID &&
                        !parent.isPointInChildBounds(child, initialX, mInitialY);
                break;
        }
        if (!mIgnoreEvents &&
                mViewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        // We have to handle cases that the ViewDragHelper does not capture the bottom sheet because
        // it is not the top most view of its parent. This is not necessary when the touch event is
        // happening over the scrolling content as nested scrolling logic handles that case.
        View scroll = mNestedScrollingChildRef.get();
        return action == MotionEvent.ACTION_MOVE
                &&
                scroll != null &&
                !mIgnoreEvents &&
                mState != STATE_DRAGGING &&
                !parent.isPointInChildBounds(scroll, (int) event.getX(), (int) event.getY()) &&
                Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop();
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, V child, MotionEvent event) {
        if (!child.isShown()) {
            return false;
        }
        int action = MotionEventCompat.getActionMasked(event);
        if (mState == STATE_DRAGGING && action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        mViewDragHelper.processTouchEvent(event);
        // Record the velocity
        if (action == MotionEvent.ACTION_DOWN) {
            reset();
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        // The ViewDragHelper tries to capture only the top-most View. We have to explicitly tell it
        // to capture the bottom sheet in case it is not captured and the touch slop is passed.
        if (action == MotionEvent.ACTION_MOVE) {


            if (mSlideHelper.canScrollHorizontally() != 0 && Math.abs(mInitialY - event.getX()) > mViewDragHelper.getTouchSlop()) {
                mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            } else if (mSlideHelper.canScrollVertically() != 0 && Math.abs(mInitialY - event.getY()) > mViewDragHelper.getTouchSlop()) {
                mViewDragHelper.captureChildView(child, event.getPointerId(event.getActionIndex()));
            }
        }
        return true;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V child,
                                       View directTargetChild, View target, int nestedScrollAxes) {
        mLastNestedScrollDy = 0;

        View scrollingChild = mNestedScrollingChildRef.get();

        return target == scrollingChild && mSlideHelper.canScrollVertically() != 0 && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                  int dx,
                                  int dy, int[] consumed) {
        mSlideHelper.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }


    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
        mSlideHelper.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V child, View target,
                                    float velocityX, float velocityY) {
        return (mState != STATE_EXPANDED ||
                super.onNestedPreFling(coordinatorLayout, child, target,
                        velocityX, velocityY));
    }

    /**
     * Sets the height of the bottom sheet when it is collapsed.
     *
     * @param peekHeight The height of the collapsed bottom sheet in pixels.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_peekHeight
     */

    public final void setPeekHeight(int peekHeight) {

        mPeekHeight = peekHeight;
        if (mSlideHelper != null) {
            mSlideHelper.setPeekHeight(peekHeight);
        }
    }

    /**
     * Gets the height of the bottom sheet when it is collapsed.
     *
     * @return The height of the collapsed bottom sheet.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_peekHeight
     */
    public final int getPeekHeight() {
        return mPeekHeight;
    }

    /**
     * Sets whether this bottom sheet can hide when it is swiped down.
     *
     * @param hideable {@code true} to make this bottom sheet hideable.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_hideable
     */
    public void setHideable(boolean hideable) {
        mHideable = hideable;
    }

    public void setSlideModel(int slideModel) {

        mSlideModel = slideModel;
        mSlideHelper = createSlideHelper(slideModel);
        if (mViewRef.get() != null) {
            mSlideHelper.onLayoutChild(mViewRef.get());
        }
    }

    /**
     * Gets whether this bottom sheet can hide when it is swiped down.
     *
     * @return {@code true} if this bottom sheet can hide.
     * @attr ref android.support.design.R.styleable#BottomSheetBehavior_Params_behavior_hideable
     */
    public boolean isHideable() {
        return mHideable;
    }

    /**
     * Sets a callback to be notified of bottom sheet events.
     *
     * @param callback The callback to notify when bottom sheet events occur.
     */
    public void setBottomSheetCallback(BottomSheetCallback callback) {
        mCallback = callback;
    }

    /**
     * Sets the state of the bottom sheet. The bottom sheet will transition to that state with
     * animation.
     *
     * @param state One of {@link #STATE_COLLAPSED}, {@link #STATE_EXPANDED}, or
     *              {@link #STATE_HIDDEN}.
     */
    public final void setState(@State int state) {
        mSlideHelper.setState(state);
    }

    /**
     * Gets the current state of the bottom sheet.
     *
     * @return One of {@link #STATE_EXPANDED}, {@link #STATE_COLLAPSED}, {@link #STATE_DRAGGING},
     * and {@link #STATE_SETTLING}.
     */
    @State
    public final int getState() {
        return mState;
    }

    private void setStateInternal(@State int state) {
        if (mState == state) {
            return;
        }
        mState = state;
        View bottomSheet = mViewRef.get();
        if (bottomSheet != null && mCallback != null) {
            mCallback.onStateChanged(bottomSheet, state);
        }
    }

    private void reset() {
        mActivePointerId = ViewDragHelper.INVALID_POINTER;
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private View findScrollingChild(View view) {
        if (view instanceof NestedScrollingChild) {
            return view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0, count = group.getChildCount(); i < count; i++) {
                View scrollingChild = findScrollingChild(group.getChildAt(i));
                if (scrollingChild != null) {
                    return scrollingChild;
                }
            }
        }
        return null;
    }

    private float getYVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
        return VelocityTrackerCompat.getYVelocity(mVelocityTracker, mActivePointerId);
    }

    private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            if (mState == STATE_DRAGGING) {
                return false;
            }
            if (mTouchingScrollingChild) {
                return false;
            }
            if (mState == STATE_EXPANDED && mActivePointerId == pointerId) {
                View scroll = mNestedScrollingChildRef.get();
                if (scroll != null && ViewCompat.canScrollVertically(scroll, mSlideHelper.canScrollVertically())) {
                    // Let the content scroll up
                    return false;
                }
            }
            return mViewRef != null && mViewRef.get() == child;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mSlideHelper.dispatchOnSlide(top);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == ViewDragHelper.STATE_DRAGGING) {
                setStateInternal(STATE_DRAGGING);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            mSlideHelper.onViewReleased(releasedChild, xvel, yvel);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return mSlideHelper.clampViewPositionVertical(child, top, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return mSlideHelper.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mSlideHelper.getViewVerticalDragRange(child);
        }
    };


    private class SettleRunnable implements Runnable {

        private final View mView;

        @State
        private final int mTargetState;

        SettleRunnable(View view, @State int targetState) {
            mView = view;
            mTargetState = targetState;
        }

        @Override
        public void run() {
            if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(mView, this);
            } else {
                setStateInternal(mTargetState);
            }
        }
    }

    protected static class SavedState extends View.BaseSavedState {

        @State
        final int state;

        public SavedState(Parcel source) {
            super(source);
            //noinspection ResourceType
            state = source.readInt();
        }

        public SavedState(Parcelable superState, @State int state) {
            super(superState);
            this.state = state;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(state);
        }

        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    @Override
                    public SavedState createFromParcel(Parcel source) {
                        return new SavedState(source);
                    }

                    @Override
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    /**
     * A utility function to get the {@link SheetBehavior} associated with the {@code view}.
     *
     * @param view The {@link View} with {@link SheetBehavior}.
     * @return The {@link SheetBehavior} associated with the {@code view}.
     */
    @SuppressWarnings("unchecked")
    public static <V extends View> SheetBehavior<V> from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof SheetBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with BottomSheetBehavior");
        }
        return (SheetBehavior<V>) behavior;
    }

    public SlideHelper createSlideHelper(int slideMode) {

        switch (slideMode) {
            case TOP_SHEET:
                return new TopSlideHelper();
            case BOTTOM_SHEET:
                return new BottomSlideHelper();

        }
        throw new IllegalArgumentException("invalid orientation");
    }


    private abstract class SlideHelper {

        int mMinOffset;
        int mMaxOffset;
        int mParentHeight;

        abstract void onLayoutChild(V child);

        abstract void setPeekHeight(int peekHeight);

        abstract void setState(@State int state);

        /**
         * @return <0 表示支持往下滑, 0 表示不能滑动,  >0 表示往上滑动
         */
        public int canScrollVertically() {
            return 0;
        }

        /**
         * @return >0 表示支持往左滑, 0 表示不能滑动, < 0 表示往右边滑动
         */
        public int canScrollHorizontally() {
            return 0;
        }

        public void dispatchOnSlide(int top) {
            View bottomSheet = mViewRef.get();
            if (bottomSheet != null && mCallback != null) {
                if (top > mMaxOffset) {
                    mCallback.onSlide(bottomSheet, (float) (mMaxOffset - top) / mPeekHeight);
                } else {
                    mCallback.onSlide(bottomSheet,
                            (float) (mMaxOffset - top) / ((mMaxOffset - mMinOffset)));
                }
            }
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                      int dx,
                                      int dy, int[] consumed) {
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {

        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {

        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        public int getViewVerticalDragRange(View child) {
            return 0;
        }

    }


    private class TopSlideHelper extends SlideHelper {


        private int mChildHeight;

        @Override
        void onLayoutChild(V child) {
            mChildHeight = child.getHeight();

            mMaxOffset = -mChildHeight + mPeekHeight;
            mMinOffset = 0;
            if (mState == STATE_EXPANDED) {
                ViewCompat.offsetTopAndBottom(child, mMinOffset);
            } else if (mHideable && mState == STATE_HIDDEN) {
                ViewCompat.offsetTopAndBottom(child, mMaxOffset - mPeekHeight);
            } else if (mState == STATE_COLLAPSED) {
                ViewCompat.offsetTopAndBottom(child, mMaxOffset);
            }
        }

        @Override
        void setPeekHeight(int peekHeight) {
            mPeekHeight = Math.max(0, peekHeight);
            mMaxOffset = -mChildHeight + mPeekHeight;
        }

        public int canScrollVertically() {
            return 1;
        }

        public void setState(@State int state) {
            V child = mViewRef.get();
            if (child == null) {
                return;
            }
            int top;
            if (state == STATE_COLLAPSED) {
                top = mMaxOffset;
            } else if (state == STATE_EXPANDED) {
                top = mMinOffset;
            } else if (mHideable && state == STATE_HIDDEN) {
                top = mMaxOffset - mPeekHeight;
            } else {
                throw new IllegalArgumentException("Illegal state argument: " + state);
            }
            setStateInternal(STATE_SETTLING);
            if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
            }
        }


        public boolean shouldHide(View child, float yvel) {
            if (Math.abs(child.getTop()) < Math.abs(mMaxOffset)) {
                // It should not hide, but collapse.
                return false;
            }
            final float newTop = child.getTop() + yvel * HIDE_FRICTION;
            return Math.abs(newTop - mMaxOffset) / (float) mPeekHeight > HIDE_THRESHOLD;
        }


        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                      int dx,
                                      int dy, int[] consumed) {

            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            if (dy > 0) { // Upward
                if (!ViewCompat.canScrollVertically(target, 1)) {
                    if (newTop >= mMaxOffset || mHideable) {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        setStateInternal(STATE_DRAGGING);
                    } else {
                        consumed[1] = currentTop - mMaxOffset;
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        setStateInternal(STATE_COLLAPSED);
                    }
                }


            } else if (dy < 0) { // Downward

                if (newTop > mMinOffset) {
                    consumed[1] = currentTop - mMinOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(STATE_EXPANDED);
                } else {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                }
            }
            dispatchOnSlide(child.getTop());
            mLastNestedScrollDy = dy;
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
            if (child.getTop() == mMinOffset) {
                setStateInternal(STATE_EXPANDED);
                return;
            }
            int top;
            int targetState;
            if (mLastNestedScrollDy < 0) { //down
                top = mMinOffset;
                targetState = STATE_EXPANDED;
            } else if (mHideable && shouldHide(child, getYVelocity())) {
                top = mMaxOffset - mPeekHeight;
                targetState = STATE_HIDDEN;
            } else if (mLastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
            } else {
                setStateInternal(targetState);
            }
        }


        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;
            @State int targetState;
            if (yvel > 0) { // Moving down
                if (mHideable && releasedChild.getTop() < mMaxOffset) {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                } else {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                }
            } else if (mHideable && shouldHide(releasedChild, yvel)) {
                top = mMaxOffset - mPeekHeight;
                targetState = STATE_HIDDEN;
            } else if (yvel == 0.f) {
                int currentTop = releasedChild.getTop();
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild,
                        new SettleRunnable(releasedChild, targetState));
            } else {
                setStateInternal(targetState);
            }
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return MathUtils.constrain(top, mHideable ? mMaxOffset - mPeekHeight : mMaxOffset, mMinOffset);
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(View child) {
            if (mHideable) {
                return mMinOffset - mMaxOffset + mPeekHeight;
            } else {
                return mMinOffset - mMaxOffset;
            }
        }

        public void dispatchOnSlide(int top) {
            View bottomSheet = mViewRef.get();
            if (bottomSheet != null && mCallback != null) {
                if (top < mMaxOffset) {
                    mCallback.onSlide(bottomSheet, (float) (top - mMaxOffset) / mPeekHeight);
                } else {
                    mCallback.onSlide(bottomSheet,
                            (float) (top - mMaxOffset) / ((mMinOffset - mMaxOffset)));
                }
            }
        }


    }

    private class BottomSlideHelper extends SlideHelper {


        public void onLayoutChild(V child) {

            View parent = child.getParent() instanceof View ? (View) child.getParent() : null;

            if (parent != null) {
                mParentHeight = parent.getHeight();
                mMinOffset = Math.max(0, mParentHeight - child.getHeight());
                mMaxOffset = mParentHeight - mPeekHeight;
                if (mState == STATE_EXPANDED) {
                    ViewCompat.offsetTopAndBottom(child, mMinOffset);
                } else if (mHideable && mState == STATE_HIDDEN) {
                    ViewCompat.offsetTopAndBottom(child, mParentHeight);
                } else if (mState == STATE_COLLAPSED) {
                    ViewCompat.offsetTopAndBottom(child, mMaxOffset);
                }
            }
        }


        public int canScrollVertically() {
            return -1;
        }


        public void setPeekHeight(int peekHeight) {
            mPeekHeight = Math.max(0, peekHeight);
            mMaxOffset = mParentHeight - peekHeight;
        }

        public void setState(@State int state) {
            V child = mViewRef.get();
            if (child == null) {
                return;
            }
            int top;
            if (state == STATE_COLLAPSED) {
                top = mMaxOffset;
            } else if (state == STATE_EXPANDED) {
                top = mMinOffset;
            } else if (mHideable && state == STATE_HIDDEN) {
                top = mParentHeight;
            } else {
                throw new IllegalArgumentException("Illegal state argument: " + state);
            }
            setStateInternal(STATE_SETTLING);
            if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, state));
            }
        }


        public boolean shouldHide(View child, float yvel) {
            if (child.getTop() < mMaxOffset) {
                // It should not hide, but collapse.
                return false;
            }
            final float newTop = child.getTop() + yvel * HIDE_FRICTION;
            return Math.abs(newTop - mMaxOffset) / (float) mPeekHeight > HIDE_THRESHOLD;
        }

        public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V child, View target,
                                      int dx,
                                      int dy, int[] consumed) {

            int currentTop = child.getTop();
            int newTop = currentTop - dy;
            if (dy > 0) { // Upward
                if (newTop < mMinOffset) {
                    consumed[1] = currentTop - mMinOffset;
                    ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                    setStateInternal(STATE_EXPANDED);
                } else {
                    consumed[1] = dy;
                    ViewCompat.offsetTopAndBottom(child, -dy);
                    setStateInternal(STATE_DRAGGING);
                }
            } else if (dy < 0) { // Downward
                if (!ViewCompat.canScrollVertically(target, -1)) {
                    if (newTop <= mMaxOffset || mHideable) {
                        consumed[1] = dy;
                        ViewCompat.offsetTopAndBottom(child, -dy);
                        setStateInternal(STATE_DRAGGING);
                    } else {
                        consumed[1] = currentTop - mMaxOffset;
                        ViewCompat.offsetTopAndBottom(child, -consumed[1]);
                        setStateInternal(STATE_COLLAPSED);
                    }
                }
            }
            dispatchOnSlide(child.getTop());
            mLastNestedScrollDy = dy;
        }

        public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V child, View target) {
            if (child.getTop() == mMinOffset) {
                setStateInternal(STATE_EXPANDED);
                return;
            }
            int top;
            int targetState;
            if (mLastNestedScrollDy > 0) {
                top = mMinOffset;
                targetState = STATE_EXPANDED;
            } else if (mHideable && shouldHide(child, getYVelocity())) {
                top = mParentHeight;
                targetState = STATE_HIDDEN;
            } else if (mLastNestedScrollDy == 0) {
                int currentTop = child.getTop();
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.smoothSlideViewTo(child, child.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(child, new SettleRunnable(child, targetState));
            } else {
                setStateInternal(targetState);
            }
        }

        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            int top;
            @State int targetState;
            if (yvel < 0) { // Moving up
                if (mHideable && releasedChild.getTop() < mMaxOffset) {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                } else {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                }
            } else if (mHideable && shouldHide(releasedChild, yvel)) {
                top = mParentHeight;
                targetState = STATE_HIDDEN;
            } else if (yvel == 0.f) {
                int currentTop = releasedChild.getTop();
                if (Math.abs(currentTop - mMinOffset) < Math.abs(currentTop - mMaxOffset)) {
                    top = mMinOffset;
                    targetState = STATE_EXPANDED;
                } else {
                    top = mMaxOffset;
                    targetState = STATE_COLLAPSED;
                }
            } else {
                top = mMaxOffset;
                targetState = STATE_COLLAPSED;
            }
            if (mViewDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top)) {
                setStateInternal(STATE_SETTLING);
                ViewCompat.postOnAnimation(releasedChild,
                        new SettleRunnable(releasedChild, targetState));
            } else {
                setStateInternal(targetState);
            }
        }

        public int clampViewPositionVertical(View child, int top, int dy) {
            return MathUtils.constrain(top, mMinOffset, mHideable ? mParentHeight : mMaxOffset);
        }

        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return child.getLeft();
        }

        public int getViewVerticalDragRange(View child) {
            if (mHideable) {
                return mParentHeight - mMinOffset;
            } else {
                return mMaxOffset - mMinOffset;
            }
        }
    }

}