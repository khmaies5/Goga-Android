package com.esprit.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.esprit.android.util.MxxNetworkUtil;
import com.mixiaoxiao.android.view.pulltorefresh.ListBottomView;
import com.mixiaoxiao.android.view.pulltorefresh.ListHeaderView;
import com.mixiaoxiao.android.view.pulltorefresh.RefreshableListView;
import com.esprit.goga.R;
import com.nineoldandroids.animation.ObjectAnimator;
import com.todddavies.components.progressbar.ProgressWheel;

/**
 * You can pull down the refresh to load more listview improvements
 * Pull or pull the background data is completed automatically call adapter.notifydatasetChanged method
   setEnableInterceptTouchEvent Represents a horizontal swipe gesture that is dispatched to a child view
  * setBottomHasMore at the bottom of the existence of more
 * @author wangbin
 * 
 */
public class MxxRefreshableListView extends RefreshableListView {

	private final int animation_duration = 600;// Turn around for a long time
	private boolean internetEnabled = true;
	private boolean bottomHasMore = true;
	/**
	 * Position of the last motion event. Used to distribute gesture events
	 */
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean mIsBeingDragged = false;// Whether you are sliding the item horizontally
	private boolean mEnableInterceptTouchEvent = false;// Whether the horizontal slide event to the child control
	private OnTopRefreshListener onTopRefreshListener;
	private OnBottomRefreshListener onBottomRefreshListener;
	
	public interface OnTopRefreshListener{
		public void onStart();
		public void onDoinBackground();
		public void onEnd();
	}
	public interface OnBottomRefreshListener{
		public void onStart();
		public void onDoinBackground();
		public void onEnd();
	}

	
	public void setOnTopRefreshListener(OnTopRefreshListener onTopRefreshListener) {
		this.onTopRefreshListener = onTopRefreshListener;
	}

	public void setOnBottomRefreshListener(
			OnBottomRefreshListener onBottomRefreshListener) {
		this.onBottomRefreshListener = onBottomRefreshListener;
	}

	@SuppressLint("NewApi")
	public MxxRefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (android.os.Build.VERSION.SDK_INT >= 9) {
			setOverScrollMode(View.OVER_SCROLL_NEVER);
		}
		this.setFadingEdgeLength(0);
		// this.setSelector(R.drawable.list_item_selector_transparent);
		// this.setClipToPadding(false);
		// this.setFitsSystemWindows(true);
		addPullDownRefreshFeature(context);
		addPullUpRefreshFeature(context);
		initRefreshListener();
	}
	
	private void initRefreshListener(){
		setOnUpdateTask(new OnUpdateTask() {
			boolean isNetworkAvailable = false;
			public void onUpdateStart() {
				isNetworkAvailable = MxxNetworkUtil
						.isNetworkAvailable(getContext());
				setInternetEnabled(isNetworkAvailable);
				if(isNetworkAvailable){
					if(onTopRefreshListener!=null) onTopRefreshListener.onStart();
				}
			}

			public void updateBackground() {
				if (isNetworkAvailable) {
					if(onTopRefreshListener!=null) onTopRefreshListener.onDoinBackground();
				}
			}
			public void updateUI() {
				if (isNetworkAvailable) {
					if(onTopRefreshListener!=null) onTopRefreshListener.onEnd();
				}
			}
		});
		setOnPullUpUpdateTask(new OnPullUpUpdateTask() {
			boolean isNetworkAvailable = false;

			public void onUpdateStart() {
				if(!bottomHasMore) return;
				isNetworkAvailable = MxxNetworkUtil
						.isNetworkAvailable(getContext());
				setInternetEnabled(isNetworkAvailable);
				if(isNetworkAvailable){
					if(onBottomRefreshListener!=null) onBottomRefreshListener.onStart();
				}
			}

			public void updateBackground() {
				if(!bottomHasMore) return;
				if (isNetworkAvailable) {
					if(onBottomRefreshListener!=null) onBottomRefreshListener.onDoinBackground();
				}
			}

			public void updateUI() {
				if(!bottomHasMore) return;
				if (isNetworkAvailable) {
					if(onBottomRefreshListener!=null) onBottomRefreshListener.onEnd();
				}
			}

		});
	}

	/**
	 * Whether the horizontal slide event to the child control
	 * 
	 * @param enaled
	 */
	public void setEnableInterceptTouchEvent(boolean enaled) {
		this.mEnableInterceptTouchEvent = enaled;
	}

	public boolean isBottomHasMore() {
		return bottomHasMore;
	}

	/**
	 * Is there any more?
	 * 
	 * @param hasMore
	 */
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
			final float x = ev.getX();// MotionEventCompat.getX(ev,
										// pointerIndex);
			final float xDiff = Math.abs(x - mLastMotionX);
			final float y = ev.getY();// MotionEventCompat.getY(ev,
										// pointerIndex);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > yDiff) {
				mIsBeingDragged = true;
				return false;
			}
			break;
		} /* end of case */

		case MotionEvent.ACTION_DOWN: {
			mLastMotionX = ev.getX();
			mLastMotionY = ev.getY();
			mIsBeingDragged = false;
			break;
		} /* end of case */
		case MotionEvent.ACTION_CANCEL: {
			mLastMotionX = ev.getX();
			mLastMotionY = ev.getY();
			mIsBeingDragged = false;
			break;
		}
		case MotionEventCompat.ACTION_POINTER_UP:
			// onSecondaryPointerUp(ev);
			mIsBeingDragged = false;
			break;
		} /* end of switch */

		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * Whether there is "Internet connection" will be based on the adjustment of the corresponding drop-down text
	 * 
	 * @param enabled
	 */
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
		setTopContentView(R.layout.mxx_refresh_listview_header);
//		mListHeaderView.setBackgroundColor(getResources().getColor(R.color.mxx_item_theme_color_alpha));
		final TextView infoTextView = (TextView) mListHeaderView
				.findViewById(R.id.mxx_refresh_listview_header_textview);
		final ProgressWheel progressWheel = (ProgressWheel) mListHeaderView
				.findViewById(R.id.mxx_refresh_listview_header_progresswheel);
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
				progressWheel, "rotationY", 0f, 360f);// 3DX axis (ie, up and down axis) 0 degrees to 180 degrees rotation
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
					infoTextView.setText(getResources().getString(R.string.mxx_view_string_nonetwork));//"No internet connection"
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
		this.setBottomContentView(R.layout.mxx_refresh_listview_footer);
//		mListBottomView.setBackgroundColor(getResources().getColor(R.color.mxx_item_theme_color_alpha));
		final TextView infoTextView = (TextView) mListBottomView
				.findViewById(R.id.mxx_refresh_listview_header_textview);

		final ProgressWheel progressWheel = (ProgressWheel) mListBottomView
				.findViewById(R.id.mxx_refresh_listview_header_progresswheel);
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
				progressWheel, "rotationY", 0f, 360f);// 3DX axis (ie, up and down axis) 0 degrees to 180 degrees rotation
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
					infoTextView.setText(getResources().getString(R.string.mxx_view_string_nomore));
					progressWheel.setVisibility(View.GONE);
					return;
				}
				if (internetEnabled) {
					infoTextView.setText("");
					objectAnimator.start();
				} else {
					infoTextView.setText(getResources().getString(R.string.mxx_view_string_nonetwork));
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
				// TODO Auto-generated method stub
				progressWheel.setProgress((int) (360.0f * heightPercent));
			}

		});
	}
}
