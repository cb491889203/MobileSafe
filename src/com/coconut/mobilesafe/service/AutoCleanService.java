package com.coconut.mobilesafe.service;

import java.util.List;

import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanService extends Service {

	private ActivityManager am;
	private static final String TAG = "AutoClearService";
	private AutoCleanReceiver receiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		receiver = new AutoCleanReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		receiver = null;
	}
	
	public class AutoCleanReceiver extends BroadcastReceiver{

	

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "接收到锁屏广播,正在执行清理...");
			List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
			for (AndroidAppProcess androidAppProcess : runningAppProcesses) {
				String packageName = androidAppProcess.getPackageName();
				am.killBackgroundProcesses(packageName);
			}
		}
	}
}
