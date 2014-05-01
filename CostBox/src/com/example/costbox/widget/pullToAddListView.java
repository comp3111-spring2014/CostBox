package com.example.costbox.widget;

import java.sql.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.costbox.R;

public class pullToAddListView extends ListView implements OnScrollListener 
	{

		private static final String TAG = "PullToRefreshListView";

		private final static int RELEASE_TO_REFRESH = 0;
		private final static int PULL_TO_REFRESH = 1;
		private final static int REFRESHING = 2;
		private final static int DONE = 3;
		private final static int LOADING = 4;

		private final static int RATIO = 3;
		private LayoutInflater inflater;

		private LinearLayout headView;
		private TextView tipsTextview;
		//private ImageView arrowImageView;

		private RotateAnimation animation;
		private RotateAnimation reverseAnimation;

		// to maintain the only one touch during an event 
		private boolean isRecored;
		private int headContentWidth;
		private int headContentHeight;
		private int startY;
		private int firstItemIndex;
		private int state;
		private boolean isBack;

		private OnRefreshListener refreshListener;

		private boolean isRefreshable;

		public pullToAddListView(Context context) 
		{
			super(context);
			init(context);
		}

		public pullToAddListView(Context context, AttributeSet attrs) 
		{
			super(context, attrs);
			init(context);
		}

		private void init(Context context)
		{
			//setCacheColorHint(context.getResources().getColor(R.color.transparent));
			inflater = LayoutInflater.from(context);
			headView = (LinearLayout) inflater.inflate(R.layout.head, null);

			//arrowImageView = (ImageView) headView.findViewById(R.id.head_arrowImageView);
			//arrowImageView.setMinimumWidth(70);
			//arrowImageView.setMinimumHeight(50);
			
			tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
			
			measureView(headView);
			headContentHeight = headView.getMeasuredHeight();
			headContentWidth = headView.getMeasuredWidth();

			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			headView.invalidate();

			Log.v("size", "width:" + headContentWidth + " height:"
					+ headContentHeight);

			addHeaderView(headView, null, false);
			setOnScrollListener(this);
            /*
			animation = new RotateAnimation(0, -180,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			animation.setInterpolator(new LinearInterpolator());
			animation.setDuration(250);
			animation.setFillAfter(true);

			reverseAnimation = new RotateAnimation(-180, 0,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f,
					RotateAnimation.RELATIVE_TO_SELF, 0.5f);
			reverseAnimation.setInterpolator(new LinearInterpolator());
			reverseAnimation.setDuration(200);
			reverseAnimation.setFillAfter(true);
            */
			state = DONE;
			isRefreshable = false;
		}
       
		@Override
		public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2, int arg3) 
		{
		     firstItemIndex = firstVisiableItem;
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int arg1)
		{
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) 
		{
			
			if (isRefreshable) 
			{
				switch (event.getAction()) 
				{
				case MotionEvent.ACTION_DOWN:
					if (firstItemIndex == 0 && !isRecored) 
					{
						isRecored = true;
						startY = (int) event.getY();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (state != REFRESHING && state != LOADING) {
						if (state == DONE) {
						}
						if (state == PULL_TO_REFRESH) {
							state = DONE;
							changeHeaderViewByState();
						}
						if (state == RELEASE_TO_REFRESH) {
							state = REFRESHING;
							changeHeaderViewByState();
							onRefresh();
						}
					}
					isRecored = false;
					isBack = false;
					break;
				case MotionEvent.ACTION_MOVE:
					int tempY = (int) event.getY();
					if (!isRecored && firstItemIndex == 0) {
						isRecored = true;
						startY = tempY;
					}
					if (state != REFRESHING && isRecored && state != LOADING) {
						if (state == RELEASE_TO_REFRESH) {
							setSelection(0);
                           //push back to the top
							if (((tempY - startY) / RATIO < headContentHeight)
									&& (tempY - startY) > 0) {
								state = PULL_TO_REFRESH;
								changeHeaderViewByState();
							}
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
							}
						}
						if (state == PULL_TO_REFRESH) {
							setSelection(0);
							if ((tempY - startY) / RATIO >= headContentHeight) {
								state = RELEASE_TO_REFRESH;
								isBack = true;
								changeHeaderViewByState();
							}
							else if (tempY - startY <= 0) {
								state = DONE;
								changeHeaderViewByState();
							}
						}
						if (state == DONE) {
							if (tempY - startY > 0) {
								state = PULL_TO_REFRESH;
								changeHeaderViewByState();
							}
						}
						if (state == PULL_TO_REFRESH) {
							headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);
						}
						if (state == RELEASE_TO_REFRESH) {
							headView.setPadding(0, (tempY - startY) / RATIO
									- headContentHeight, 0, 0);
						}
					}
					break;
				}
			}

			return super.onTouchEvent(event);
		}
/*
 * This method is used to change the view of header according to the state
 */
		private void changeHeaderViewByState()
		{
			switch (state) 
			{
			case RELEASE_TO_REFRESH:
				//arrowImageView.setVisibility(View.VISIBLE);
				tipsTextview.setVisibility(View.VISIBLE);

				//arrowImageView.clearAnimation();
				//arrowImageView.startAnimation(animation);
				tipsTextview.setText("Release To Add...");
				Log.v(TAG, "release to add a new record");
				break;
			case PULL_TO_REFRESH:
				//progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				//lastUpdatedTextView.setVisibility(View.VISIBLE);
				//arrowImageView.clearAnimation();
				//arrowImageView.setVisibility(View.VISIBLE);
				if (isBack) {
					isBack = false;
					//arrowImageView.clearAnimation();
					//arrowImageView.startAnimation(reverseAnimation);
					tipsTextview.setText("Pulling...");
				} else {
					tipsTextview.setText("pulling...");
				}
				break;

			case REFRESHING:
				headView.setPadding(0, 0, 0, 0);
				//progressBar.setVisibility(View.VISIBLE);
				//arrowImageView.clearAnimation();
				//arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText("Adding...");
				//lastUpdatedTextView.setVisibility(View.VISIBLE);
				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);
				//progressBar.setVisibility(View.GONE);
				//arrowImageView.clearAnimation();
				//arrowImageView.setImageResource(R.drawable.arrow);
				tipsTextview.setText("Pulling...");
				break;
			}
		}

		public void setonRefreshListener(OnRefreshListener refreshListener)
		{
			this.refreshListener = refreshListener;
			isRefreshable = true;
		}

		public interface OnRefreshListener 
		{
			public void onRefresh();
		}
        
		public void onRefreshComplete()
		{
			state = DONE;
			changeHeaderViewByState();
		}
        
		private void onRefresh() 
		{
			if (refreshListener != null)
			{
				refreshListener.onRefresh();
			}
		}
        // get the layout measurement
		private void measureView(View child) {
			ViewGroup.LayoutParams p = child.getLayoutParams();
			if (p == null) {
				p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			}
			int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
			int lpHeight = p.height;
			int childHeightSpec;
			if (lpHeight > 0) {
				childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
						MeasureSpec.EXACTLY);
			} else {
				childHeightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			child.measure(childWidthSpec, childHeightSpec);
		}
        
		public void setAdapter(BaseAdapter adapter)
		{
			super.setAdapter(adapter);
		}
		

	
}
