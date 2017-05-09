package com.example.administrator.myscrollviewdemo;

import android.content.Context;
import android.support.annotation.Px;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollView extends ViewGroup {
    private int height;
    private float mLastY;
    private int mTouchSlop;
    private String TAG = "MyScrollView";
    private Scroller mScroller;
    private int mStartY;
    private int mEndY;

    public MyScrollView(Context context) {
        super(context);
        init();
    }

    private void init() {
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        // 获取TouchSlop值
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mScroller = new Scroller(getContext());

    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (height == 0)
            height = getMeasuredHeight();
        Log.i(TAG, "height: " + height);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        layoutParams.height = height * getChildCount();
        setLayoutParams(layoutParams);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                childView.layout(l, i * height, r, (i + 1) * height);

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = event.getY();
                mStartY = getScrollY();
                break;
            case MotionEvent.ACTION_MOVE:

                float dy = event.getY() - mLastY;
                Log.i(TAG, "dy: " + dy + ",getY(): " + event.getY() + ",mLastY: " + mLastY);
                Log.i(TAG, "getScrollY(): " + getScrollY());
                if (getScrollY() < 0) {
                    dy = 0;
                }
                Log.i(TAG, "getHeight(): " + getHeight() + "height: " + height);
                if (getScrollY() > getHeight() - height) {
                    dy = getHeight() - height;
                }
                scrollBy(0, -(int) dy);
                mLastY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mEndY = getScrollY();
                int dScrollY = mEndY - mStartY;
                if (dScrollY>0){
                    if (dScrollY<height/3){
                        mScroller.startScroll(0,getScrollY(),0,-dScrollY);
                    }else {
                        mScroller.startScroll(0,getScrollY(),0,height - dScrollY);
                    }
                }else {
                    if (-dScrollY<height/3){
                        mScroller.startScroll(0,getScrollY(),0,-dScrollY);
                    }else {
                        mScroller.startScroll(0,getScrollY(),0,-height-dScrollY);
                    }
                }

                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public void scrollTo(@Px int x, @Px int y) {
        if (y<0){
            y = 0;
        }
        if (y>getHeight() - height)
            y = getHeight()-height;
        super.scrollTo(x, y);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()){
            scrollTo(0,mScroller.getCurrY());
            postInvalidate();
        }
    }
}
