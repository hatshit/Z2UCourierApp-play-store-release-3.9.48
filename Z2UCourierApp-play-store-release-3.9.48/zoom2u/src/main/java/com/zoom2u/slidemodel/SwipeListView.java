package com.zoom2u.slidemodel;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeListView {
	public interface SwipeListViewCallback {
		/**
		 * 
		 * @return ListView
		 */
		ListView getListView();

		/**
		 * 
		 * @param isRight
		 *            Swiping direction
		 * @param position
		 *            which item position is swiped
		 */
		void onSwipeItemLeft(boolean isRight, int position);

		void onSwipeItemRight(boolean isleft, int position);
		/**
		 * For single tap/Click
		 * 
		 * @param adapter
		 * @param position
		 */
		void onItemClickListener(ListAdapter adapter, int position);
		void onItemLongClickListener(ListAdapter adapter, int position);
	}

	Context m_Context;
	ListView mListView;
	SwipeListViewCallback m_Callback;

	public SwipeListView(Context mContext, SwipeListViewCallback callback) {
		if (callback == null) {
			//
			try {
				throw new Exception(
						"Activity must be implement SwipeListViewCallback");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		init(mContext, callback);
	}

	public SwipeListView(Context mContext) throws Exception {
		if (!(mContext instanceof SwipeListViewCallback)) {
			throw new Exception("Activity must be implement SwipeListViewCallback");
		}
		init(mContext, (SwipeListViewCallback) mContext);
	}

	private ListView list;
	private int REL_SWIPE_MIN_DISTANCE;
	private int REL_SWIPE_MAX_OFF_PATH;
	private int REL_SWIPE_THRESHOLD_VELOCITY;

	protected void init(Context mContext, SwipeListViewCallback mCallback) {
		m_Context = mContext;
		m_Callback = mCallback;
	}

	public void exec() {
		//
		DisplayMetrics dm = m_Context.getResources().getDisplayMetrics();
		REL_SWIPE_MIN_DISTANCE = (int) (20.0f * dm.densityDpi / 400.0f + 0.5);
		REL_SWIPE_MAX_OFF_PATH = (int) (60.0f * dm.densityDpi / 500.0f + 0.5);
		REL_SWIPE_THRESHOLD_VELOCITY = (int) (60.0f * dm.densityDpi / 500.0f + 0.5);
		
		list = m_Callback.getListView();


		final GestureDetector gestureDetector = new GestureDetector(
				m_Context,new MyGestureDetector());

		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};
		list.setOnTouchListener(gestureListener);
	}

	private void myOnItemClick(int position) {
		if (position < 0)
			return;
		m_Callback.onItemClickListener(list.getAdapter(), position);

	}
	
	private void myOnItemLongClick(int position) {
		if (position < 0)
			return;
		m_Callback.onItemLongClickListener(list.getAdapter(), position);

	}

	class MyGestureDetector extends SimpleOnGestureListener {

		private int temp_position = -1;

		// Detect a single-click and call my own handler.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {

			int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
			myOnItemClick(pos);
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			int pos = list.pointToPosition((int) e.getX(), (int) e.getY());
			myOnItemLongClick(pos);
		}
		
		@Override
		public boolean onDown(MotionEvent e) {

			temp_position = list
					.pointToPosition((int) e.getX(), (int) e.getY());
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			
			try {
				if (Math.abs(e1.getY() - e2.getY()) > REL_SWIPE_MAX_OFF_PATH)
					return false;
				if (e1.getX() - e2.getX() > REL_SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

					int pos = list
							.pointToPosition((int) e1.getX(), (int) e2.getY());

					if (pos >= 0 && temp_position == pos)
						m_Callback.onSwipeItemRight(false, pos);
					
				} else if (e2.getX() - e1.getX() > REL_SWIPE_MIN_DISTANCE
						&& Math.abs(velocityX) > REL_SWIPE_THRESHOLD_VELOCITY) {

					int pos = list
							.pointToPosition((int) e1.getX(), (int) e2.getY());
					if (pos >= 0 && temp_position == pos)
						m_Callback.onSwipeItemLeft(false, pos);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
