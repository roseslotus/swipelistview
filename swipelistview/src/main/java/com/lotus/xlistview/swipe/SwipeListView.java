package com.lotus.xlistview.swipe;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lotus.xlistview.XListView;

/**
 * 自定义左侧滑的ListView，需要和SwipeView配合使用
 */
public class SwipeListView extends XListView {

	private static final String TAG = "SwipeListView";

	private SwipeView mFocusedItemView;

	private float mX = 0;
	private float mY = 0;
	private int mPosition = -1;
	private boolean isSliding = false;
	/**
	 * 滑动部分是否在展示，用于防止item按键事件冲突
	 */
	private boolean slideShowing = false;
	private final int HANDLER_FALSE_SLIDESHOWING = 1;
	/**
	 * 增加一个延时，延时时间是滑动需要的时间
	 */
	private final int TIME_FALSE_SLIDESHOWING = 100;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == HANDLER_FALSE_SLIDESHOWING) {
				slideShowing = false;
			}
		}
	};

	public SwipeListView(Context context) {
		super(context);
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDetachedFromWindow() {
		mHandler.removeMessages(HANDLER_FALSE_SLIDESHOWING);
		super.onDetachedFromWindow();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isSliding = false;
				mX = x;
				mY = y;
				int position = pointToPosition((int) x, (int) y);
				if (mPosition != position) {
					mPosition = position;
					if (mFocusedItemView != null) {
						mFocusedItemView.reset();
						setSlideShowing(false);
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mPosition != -1) {
					int first = this.getFirstVisiblePosition();
					int index = mPosition - first;
					View view = getChildAt(index);
					if(view == null){
						break;
					}

					mFocusedItemView = (SwipeView)view.findViewWithTag(SwipeView.TAG);
					if (mFocusedItemView == null) {
						break;
					}

					if ((Math.abs(mY - y) < 30 && Math.abs(mX - x) > 20) || isSliding) {
						mFocusedItemView.onTouchEvent(event);
						isSliding = true;
						return true;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isSliding) {
					isSliding = false;
					if (mFocusedItemView != null) {
						mFocusedItemView.adjust(mX - x > 0);
						mHandler.removeMessages(HANDLER_FALSE_SLIDESHOWING);
						setSlideShowing(true);
						return true;
					}
				}else {
					if (mFocusedItemView != null) {
						mFocusedItemView.reset();
						setSlideShowing(false);
					}
				}
				break;
		}
		return super.onTouchEvent(event);
	}

	public boolean isSlideShowing() {
		return slideShowing;
	}

	/**
	 * 在adapter中设置滑动button的按键事件时，需要调用setSlideShowing(false)
	 * 重置一下slideShowing变量
     */
	public void setSlideShowing(boolean show) {
		if (show) {
			mHandler.removeMessages(HANDLER_FALSE_SLIDESHOWING);
			slideShowing = true;
		} else {
			mHandler.removeMessages(HANDLER_FALSE_SLIDESHOWING);
			mHandler.sendEmptyMessageDelayed(HANDLER_FALSE_SLIDESHOWING, TIME_FALSE_SLIDESHOWING);
		}
	}

}