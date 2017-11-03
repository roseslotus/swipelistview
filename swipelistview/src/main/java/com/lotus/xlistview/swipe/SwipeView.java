package com.lotus.xlistview.swipe;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by tianhl on 2016/3/30.
 * 自定义左侧滑控件
 * 在Layout文件中包含两部分ViewGroup，第一个子View用于正常显示，第二个子View用于显示滑出的可操作项
 * 注意：可操作项必须是固定宽度，否则无法滑动
 */
public class SwipeView extends LinearLayout {

    public static final String TAG = "SwipeView";
    private ViewGroup actionView;
    private int actionViewWidth;
    private float mLastX = 0;
    private float mLastY = 0;
    private Scroller scroller;
    private float xs=-1;
    private boolean isSliding;
    private boolean enableSlide;

    public SwipeView(Context context, Resources resources) {
        super(context);
        initView();
    }

    public SwipeView(Context context) {
        super(context);
        initView();
    }

    public SwipeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!enableSlide){
            return super.onInterceptTouchEvent(ev);
        }

        if(ev.getAction() == MotionEvent.ACTION_UP && isSliding){
            int deleteStartXPosition=getMeasuredWidth()-actionView.getMeasuredWidth();
            if(ev.getX()>deleteStartXPosition){
                return false;
            }else {
                reset();
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void initView(){
        setOrientation(LinearLayout.HORIZONTAL);
        setTag(TAG);
        scroller = new Scroller(getContext());
    }

    /**
     * 设置当前是否可滑动
     */
    public void enableSlide(boolean flag){
        if(flag){
            this.actionView = (ViewGroup)getChildAt(1);
        }
        this.enableSlide = flag;
    }

    /**
     * 复位
     */
    public void reset() {
        int offset = getScrollX();
        if (offset == 0) {
            return;
        }
        isSliding =false;
        smoothScrollTo(0, 0);
    }

    /**
     * 侧滑
     */
    public void adjust(boolean left) {
        int offset = getScrollX();
        if (offset == 0) {
            return;
        }

        actionViewWidth = getActionViewWidth();
        if (offset < 10) {
            this.smoothScrollTo(0, 0);
        } else if (offset < actionViewWidth - 10) {
            if (left) {
                this.smoothScrollTo(actionViewWidth, 0);
            } else {
                this.smoothScrollTo(0, 0);
            }
        } else {
            this.smoothScrollTo(actionViewWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if(!enableSlide){
                    break;
                }

                float x = event.getX();
                float y = event.getY();
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                mLastX = x;
                mLastY = y;

                actionViewWidth = getActionViewWidth();
                getParent().requestDisallowInterceptTouchEvent(!(deltaY>70&&deltaX<10));

                if (deltaX != 0) {
                    float newScrollX = getScrollX() - deltaX;
                    if (newScrollX < 0) {
                        newScrollX = 0;
                    } else if (newScrollX > actionViewWidth) {
                        newScrollX = actionViewWidth;
                    }
                    if(!(xs==0||xs== actionViewWidth)){
                        this.scrollTo((int) newScrollX, 0);
                    }

                    isSliding = !(newScrollX==0);
                    xs=newScrollX;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        scroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }


    private int getActionViewWidth(){
        int childCount = actionView.getChildCount();
        int visibleCount = 0;

        for (int i= 0; i<childCount; i++){
            if(actionView.getChildAt(i).getVisibility() == VISIBLE){
                visibleCount++;
            }
        }

        return (actionView.getMeasuredWidth()/childCount*visibleCount);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }

        super.computeScroll();
    }

}
