package com.link.bianmi.widget;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.utility.NetworkUtil;
import com.link.bianmi.widget.rlistview.ListBottomView;
import com.link.bianmi.widget.rlistview.ListHeaderView;
import com.link.bianmi.widget.rlistview.RefreshableListView;
import com.nineoldandroids.animation.ObjectAnimator;

public class RListView extends RefreshableListView {

	private final int animation_duration = 600;
	private boolean internetEnabled = true;
	private boolean bottomHasMore = true;
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean mIsBeingDragged = false;
	private boolean mEnableInterceptTouchEvent = false;
	private OnTopRefreshListener onTopRefreshListener;
	private OnBottomRefreshListener onBottomRefreshListener;

	public interface OnTopRefreshListener {
		public void onStart();

		public void onDoinBackground();

		public void onEnd();
	}

	public interface OnBottomRefreshListener {
		public void onStart();

		public void onDoinBackground();

		public void onEnd();
	}

	public void setOnTopRefreshListener(
			OnTopRefreshListener onTopRefreshListener) {
		this.onTopRefreshListener = onTopRefreshListener;
	}

	public void setOnBottomRefreshListener(
			OnBottomRefreshListener onBottomRefreshListener) {
		this.onBottomRefreshListener = onBottomRefreshListener;
	}

	public RListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		this.setFadingEdgeLength(0);
		addPullDownRefreshFeature(context);
		addPullUpRefreshFeature(context);
		initRefreshListener();
	}

	private void initRefreshListener() {
		setOnUpdateTask(new OnUpdateTask() {
			boolean isNetworkAvailable = false;

			public void onUpdateStart() {
				isNetworkAvailable = NetworkUtil
						.isNetworkAvailable(getContext());
				setInternetEnabled(isNetworkAvailable);
				if (isNetworkAvailable) {
					if (onTopRefreshListener != null)
						onTopRefreshListener.onStart();
				}
			}

			public void updateBackground() {
				if (isNetworkAvailable) {
					if (onTopRefreshListener != null)
						onTopRefreshListener.onDoinBackground();
				}
			}

			public void updateUI() {
				if (isNetworkAvailable) {
					if (onTopRefreshListener != null)
						onTopRefreshListener.onEnd();
				}
			}
		});
		setOnPullUpUpdateTask(new OnPullUpUpdateTask() {
			boolean isNetworkAvailable = false;

			public void onUpdateStart() {
				if (!bottomHasMore)
					return;
				isNetworkAvailable = NetworkUtil
						.isNetworkAvailable(getContext());
				setInternetEnabled(isNetworkAvailable);
				if (isNetworkAvailable) {
					if (onBottomRefreshListener != null)
						onBottomRefreshListener.onStart();
				}
			}

			public void updateBackground() {
				if (!bottomHasMore)
					return;
				if (isNetworkAvailable) {
					if (onBottomRefreshListener != null)
						onBottomRefreshListener.onDoinBackground();
				}
			}

			public void updateUI() {
				if (!bottomHasMore)
					return;
				if (isNetworkAvailable) {
					if (onBottomRefreshListener != null)
						onBottomRefreshListener.onEnd();
				}
			}

		});
	}

	public void setEnableInterceptTouchEvent(boolean enaled) {
		this.mEnableInterceptTouchEvent = enaled;
	}

	public boolean isBottomHasMore() {
		return bottomHasMore;
	}

	public void setBottomHasMore(boolean hasMore) {
		bottomHasMore = hasMore;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!mEnableInterceptTouchEvent) {
			return super.onInterceptTouchEvent(ev);
		}
		final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
		switch (action) {
		case MotionEvent.ACTION_UP:
			mIsBeingDragged = false;
			break;
		case MotionEvent.ACTION_MOVE: {
			if (mIsBeingDragged) {
				return false;
			}
			final float x = ev.getX();
			final float xDiff = Math.abs(x - mLastMotionX);
			final float y = ev.getY();
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > yDiff) {
				mIsBeingDragged = true;
				return false;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			mLastMotionX = ev.getX();
			mLastMotionY = ev.getY();
			mIsBeingDragged = false;
			break;
		}
		case MotionEvent.ACTION_CANCEL: {
			mLastMotionX = ev.getX();
			mLastMotionY = ev.getY();
			mIsBeingDragged = false;
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			mIsBeingDragged = false;
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	public void setInternetEnabled(boolean enabled) {
		this.internetEnabled = enabled;
	}

	public ListHeaderView getListHeaderView() {
		return mListHeaderView;
	}

	public ListBottomView getListBottomView() {
		return mListBottomView;
	}

	private void addPullDownRefreshFeature(final Context context) {
		setTopContentView(R.layout.rlistview_header);
		final TextView infoTextView = (TextView) mListHeaderView
				.findViewById(R.id.refresh_listview_header_textview);
		final ProgressWheel progressWheel = (ProgressWheel) mListHeaderView
				.findViewById(R.id.refresh_listview_header_progresswheel);
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
				progressWheel, "rotationY", 0f, 360f);
		objectAnimator.setDuration(animation_duration);
		objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);

		setOnHeaderViewChangedListener(new OnHeaderViewChangedListener() {

			@Override
			public void onViewChanged(View v, boolean canUpdate) {
				objectAnimator.end();
			}

			@Override
			public void onViewUpdating(View v) {
				if (internetEnabled) {
					infoTextView.setText("");
					objectAnimator.start();
				} else {
					infoTextView.setText(getResources().getString(
							R.string.no_network));
					progressWheel.setVisibility(View.GONE);
				}

			}

			@Override
			public void onViewUpdateFinish(View v) {
				infoTextView.setText("");
				objectAnimator.end();
				progressWheel.setVisibility(View.VISIBLE);
			}

			@Override
			public void onViewHeightChanged(float heightPercent) {
				progressWheel.setProgress((int) (360.0f * heightPercent));
			}

		});
	}

	private void addPullUpRefreshFeature(final Context context) {
		this.setBottomContentView(R.layout.rlistview_footer);
		final TextView infoTextView = (TextView) mListBottomView
				.findViewById(R.id.refresh_listview_header_textview);

		final ProgressWheel progressWheel = (ProgressWheel) mListBottomView
				.findViewById(R.id.refresh_listview_header_progresswheel);
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
				progressWheel, "rotationY", 0f, 360f);
		objectAnimator.setDuration(animation_duration);
		objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		setOnBottomViewChangedListener(new OnBottomViewChangedListener() {

			@Override
			public void onViewChanged(View v, boolean canUpdate) {
				objectAnimator.end();
			}

			@Override
			public void onViewUpdating(View v) {
				if (!bottomHasMore) {
					infoTextView.setText(getResources().getString(
							R.string.no_more_to_load));
					progressWheel.setVisibility(View.GONE);
					return;
				}
				if (internetEnabled) {
					infoTextView.setText("");
					objectAnimator.start();
				} else {
					infoTextView.setText(getResources().getString(
							R.string.no_network));
					
					TranslateAnimation transAnim = new TranslateAnimation(0, 0, 48, 0);
					transAnim.setDuration(400);
					progressWheel.startAnimation(transAnim);
					progressWheel.setVisibility(View.GONE);
				}
			}

			@Override
			public void onViewUpdateFinish(View v) {
				infoTextView.setText("");
				objectAnimator.end();
				progressWheel.setVisibility(View.VISIBLE);
			}

			@Override
			public void onViewHeightChanged(float heightPercent) {
				progressWheel.setProgress((int) (360.0f * heightPercent));
			}

		});
	}
}
