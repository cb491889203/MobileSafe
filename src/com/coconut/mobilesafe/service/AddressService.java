package com.coconut.mobilesafe.service;

import com.coconut.mobilesafe.R;
import com.coconut.mobilesafe.db.dao.QueryAddress;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressService extends Service {

	protected static final String TAG = "AddressService";
	private TelephonyManager tm;
	private WindowManager wm;
	private View view;
	private MyListener listener;
	private OutCallReceiver receiver;
	private SharedPreferences sp;
	private int[] ids = new int[] { R.drawable.call_locate_blue, R.drawable.call_locate_gray,
			R.drawable.call_locate_green, R.drawable.call_locate_orange, R.drawable.call_locate_white };
	private WindowManager.LayoutParams params;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", MODE_PRIVATE);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		//注册打电话的广播接收者.
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("销毁来电显示服务", "来电显示服务已经关闭");
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		unregisterReceiver(receiver);
		receiver = null;

	}

	private class MyListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Log.i("监听电话", "进入监听中...执行查找");
				Log.i("监听电话", incomingNumber);

				String query = QueryAddress.query(incomingNumber);

				// Toast.makeText(AddressService.this, "号码是:" + query,
				// 1).show();
				Log.i("监听", query);
				myToast(query);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				// 移除吐司
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;

			default:
				break;
			}
		}

	}

	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = getResultData();
			String address = QueryAddress.query(number);
			// Toast.makeText(context, address, 1).show();

			myToast(address);
		}

	}

	long[] mHits = new long[2];
	public void myToast(String address) {
		// view = View.inflate(this, R.layout.toast_layout, null);
		// TextView tv_address = (TextView) view.findViewById(R.id.tv_address);
		// tv_address.setText(address);
		// int i = sp.getInt("toast_bg", 0);
		// view.setBackgroundResource(ids[i]);
		// params = new WindowManager.LayoutParams();
		//
		// view.setOnTouchListener(new OnTouchListener() {
		// int x;
		// int y;
		//
		// @SuppressWarnings("deprecation")
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		//
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// x = (int) event.getRawX();
		// y = (int) event.getRawY();
		//
		// break;
		// case MotionEvent.ACTION_MOVE:
		// int newX = (int) event.getRawX();
		// int newY = (int) event.getRawY();
		// int dX = newX - x;
		// int dY = newY - y;
		// params.x += dX;
		// params.y += dY;
		// if (params.x < 0) {
		// params.x = 0;
		// }
		// if (params.y < 0) {
		// params.y = 0;
		// }
		// if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth()))
		// {
		// params.x = (wm.getDefaultDisplay().getWidth() - view.getWidth());
		// }
		// if (params.y > (wm.getDefaultDisplay().getHeight() -
		// view.getHeight())) {
		// params.y = (wm.getDefaultDisplay().getHeight() - view.getHeight());
		// }
		// wm.updateViewLayout(view, params);
		// // 重新定位初始位置.
		// // x = (int) event.getRawX();
		// // y = (int) event.getRawY();
		// x = newX;
		// y = newY;
		//
		// break;
		// case MotionEvent.ACTION_UP:
		// Editor editor = sp.edit();
		// editor.putInt("lastX", params.x);
		// editor.putInt("lastY", params.y);
		// editor.commit();
		// break;
		// default:
		// break;
		// }
		// return false;
		// }
		// });
		//
		// params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		// params.gravity = Gravity.TOP + Gravity.LEFT;
		// params.x = sp.getInt("lastX", 0);
		// params.y = sp.getInt("lastY", 0);
		// params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		// params.format = PixelFormat.TRANSLUCENT;
		// params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		// wm.addView(view, params);

		view = View.inflate(this, R.layout.toast_layout, null);
		TextView textview = (TextView) view.findViewById(R.id.tv_address);
		textview.setText(address);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
				mHits[mHits.length - 1] = SystemClock.uptimeMillis();
				if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
					// 双击居中了。。。
					params.x = wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastx", params.x);
					editor.commit();
				}
			}
		});
		view.setBackgroundResource(ids[sp.getInt("toast_bg", 0)]);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.BOTTOM + Gravity.CENTER_HORIZONTAL;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(view, params);
	}
}
