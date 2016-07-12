package com.coconut.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.coconut.mobilesafe.db.dao.BlackNumberDAO;
import com.coconut.mobilesafe.service.LocationService.MyListener;

import android.Manifest.permission;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsSafeService extends Service {

	private TelephonyManager tm;
	private BlackNumberDAO dao;

	private static final String TAG = "CallSmsSafeService";
	private SmsSafeReceiver receiver;
	private MyCallListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "正在开启服务-----CallSmsSafeService");
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyCallListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		// 注册短信接收的广播接收器
		receiver = new SmsSafeReceiver();
		IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		Log.i(TAG, "正在注册接收器-----CallSmsSafeService");
	}

	/**
	 * 短信拦截在 4.4后需要成为默认短信APP才能使用的功能. 需要实现所有短信的功能才能拦截. 所以以下是方法已经无法使用.
	 * 
	 * @author Administrator
	 *
	 */
	class SmsSafeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] object = (Object[]) intent.getExtras().get("pdus");

			for (Object object2 : object) {
				SmsMessage message = SmsMessage.createFromPdu((byte[]) object2);
				String number = message.getOriginatingAddress();
				dao = new BlackNumberDAO(CallSmsSafeService.this);
				if (number.length() > 11) {
					number = number.substring(number.length() - 11);
				}
				Log.i(TAG, number);
				Log.i(TAG, dao.find(number) + "");
				String mode = dao.findMode(number);
				String body = message.getMessageBody();
				if ("2".equals(mode) || "3".equals(mode)) {
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
				if (body.contains("guanggao")) {
					Log.i(TAG, "拦截短信");
					abortBroadcast();
				}
			}
		}

	}

	/**
	 * 自定义一个电话状态监听者.继承系统的电话状态监听器.当电话状态为响铃状态时进行判断来电号码是否在黑名单中.如果在,就挂断电话.
	 * 如果不在就正常通话. 但是系统现在没有直接可以引用的挂断电话的方法.必须使用aidl文件,通过类加载器间接找到服务中的IBinder,
	 * 然后调用IBinder中的endCall();
	 * 
	 * @author Administrator
	 *
	 */
	class MyCallListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			dao = new BlackNumberDAO(CallSmsSafeService.this);
			if (incomingNumber.length() > 11) {
				incomingNumber = incomingNumber.substring(incomingNumber.length() - 11);
			}
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				String mode = dao.findMode(incomingNumber);
				if ("1".equals(mode) || "3".equals(mode)) {
					// 因为要先观察通话记录. 保持观察在前.
					// 一旦挂断电话,产生了通话记录,就出发观察者的onchange(),执行删除记录.
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true,
							new MyObserver(incomingNumber, new Handler()));
					endCall();
					// 虽然可以删除,但是endcall()是在另外一个线程中执行的.有时可能比较慢,
					// 导致下一步的删除记录执行后还没来得及生成记录,导致删除失败.
					deleteCallLog(incomingNumber);
					// 所以我们通过内容观察者来监视通话记录数据库的数据变化,一旦变化后就执行删除操作.

				}
				break;
			}
		}

		private void endCall() {
			try {
				Class<?> clazz = CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
				Method method = clazz.getDeclaredMethod("getService", String.class);
				IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
				ITelephony.Stub.asInterface(ibinder).endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		unregisterReceiver(receiver);
		receiver = null;
	}

	/**
	 * 这个方法通过内容提供者 去直接操作通话记录的数据库,删除该条记录即可.
	 * 
	 * @param number
	 */
	public void deleteCallLog(String number) {
		ContentResolver resolver = getContentResolver();
		Uri uri = Uri.parse("content://call_log/calls");
		resolver.delete(uri, "number = ?", new String[] { number });
	}

	/**实现一个自定义的观察者.用于执行通话记录相关的操作
	 * @author Administrator
	 *
	 */
	private class MyObserver extends ContentObserver {
		private String number;

		public MyObserver(String number, Handler handler) {
			super(handler);
			this.number = number;
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(number);
			getContentResolver().unregisterContentObserver(this);

		}

	}
}
