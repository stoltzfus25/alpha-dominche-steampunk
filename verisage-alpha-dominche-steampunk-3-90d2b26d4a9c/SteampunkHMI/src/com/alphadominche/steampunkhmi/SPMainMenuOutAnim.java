package com.alphadominche.steampunkhmi;

import java.util.TimerTask;

import android.view.View;
import android.widget.FrameLayout;

public class SPMainMenuOutAnim implements Runnable {
	private static final int LEFT_SCALE_DENOMINATOR = 2;
	private int mLeft;
	private boolean mShowingMenu;
	private TimerTask mMenuAnimTask;
	private View mView;
	private float mViewDensity;
	private SPMainMenuHaver mParentView;
	private int mOffset;
	
	SPMainMenuOutAnim(TimerTask task, View v, float density, SPMainMenuHaver haver, int menuOffset) {
		mShowingMenu = false;
		mLeft = menuOffset;
		mMenuAnimTask = task;
		mView = v;
		mViewDensity = density;
		mParentView = haver;
		mOffset = menuOffset;
	}
	
	public void run() {
		if (mShowingMenu) {
			mLeft /= LEFT_SCALE_DENOMINATOR;
			FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(mView.getLayoutParams());
			containerParams.setMargins(
					(int)((-mOffset + mLeft) * mViewDensity),
					containerParams.topMargin,
					containerParams.rightMargin,
					containerParams.bottomMargin);
			mView.setLayoutParams(containerParams);
		}
		if (mLeft == 0) {
			mMenuAnimTask.cancel();
			mShowingMenu = false;
			mParentView.setShowingMenu(mShowingMenu);
		}
	}
	
	public void reset() {
		if (mMenuAnimTask != null) mMenuAnimTask.cancel();
		mShowingMenu = true;
		mLeft = mOffset;
	}
	
	public void setTask(TimerTask task) {
		mMenuAnimTask = task;
	}
}
