package com.coconut.mobilesafe.service;

import java.util.List;

import com.coconut.mobilesafe.ApplockActiviaty;
import com.coconut.mobilesafe.db.dao.AppLockDAO;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;

public class ApplockService extends Service {

	public static final String TAG = "ApplockService";
	private PackageManager pm;
	private ActivityManager am;
	private AppLockDAO dao;
	private boolean flag ;
	private List<AndroidAppProcess> runningForegroundApps;
	private Intent intent1;
	private String tempPackageName;
	private EnterAPPReceiver enterReceiver;
	private ScreenOnReceiver screenOnReceiver;
	private ScreenOffReceiver screenOffReceiver;
	private List<String> lockedAPPs;
	private LockedChangeReceiver changeReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		dao = new AppLockDAO(getApplicationContext());
		pm = getPackageManager();
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		flag = true;
		lockedAPPs = dao.findAll();
		enterReceiver = new EnterAPPReceiver();
		IntentFilter filter = new IntentFilter("com.coconut.mobilesafe.enterAPP");
		registerReceiver(enterReceiver, filter);

		screenOnReceiver = new ScreenOnReceiver();
		registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));

		screenOffReceiver = new ScreenOffReceiver();
		registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		changeReceiver = new LockedChangeReceiver();
		registerReceiver(changeReceiver, new IntentFilter("com.coconut.mobilesafe.lockedAPPsChangfe"));

		intent1 = new Intent(getApplicationContext(), ApplockActiviaty.class);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//		Log.i(TAG, "onstartCommand中的flag:"+flag+"------------");
		
		new Thread(new Runnable() {

			@Override
			public void run() {
//				Log.i(TAG, "-----Thread中的flag:"+flag+"-------------");
				while (flag) {

					runningForegroundApps = ProcessManager.getRunningForegroundApps(getApplicationContext());
					String packageName = runningForegroundApps.get(0).getPackageName();
//					Log.i(TAG, "-----------运行的包名:"+packageName+"-------------");
//					Log.i(TAG, "-----------临时包名:"+tempPackageName+"-----------");
					
					if (lockedAPPs.contains(packageName)) {
						if (!packageName.equals(tempPackageName)) {
							intent1.putExtra("packageName", packageName);
							ApplockService.this.startActivity(intent1);
						}
					} else {
						tempPackageName = null;
					}
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		return Service.START_FLAG_REDELIVERY;
	}

	public class EnterAPPReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempPackageName = intent.getStringExtra("packagename");
			Log.i(TAG, "已经进入软件,需要马上停止监听加密软件");
		}
	}

	public class LockedChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			lockedAPPs = dao.findAll();
		}
	}

	public class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			tempPackageName = null;
			flag = false;
			Log.i(TAG, "屏幕关闭,停止监听加密应用.");
		}
	}

	public class ScreenOnReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			flag = true;
//			Log.i(TAG, "屏幕打开,开始监听加密应用.");
//			Log.i(TAG, "广播接受者中的flag=="+flag);
			
			
			new Thread(new Runnable() {

				@Override
				public void run() {
//					Log.i(TAG, "-----Thread中的flag:"+flag+"-------------");
					while (flag) {

						runningForegroundApps = ProcessManager.getRunningForegroundApps(getApplicationContext());
						String packageName = runningForegroundApps.get(0).getPackageName();
//						Log.i(TAG, "-----------运行的包名:"+packageName+"-------------");
//						Log.i(TAG, "-----------临时包名:"+tempPackageName+"-----------");
						
						if (lockedAPPs.contains(packageName)) {
							if (!packageName.equals(tempPackageName)) {
								intent1.putExtra("packageName", packageName);
								ApplockService.this.startActivity(intent1);
							}
						} else {
							tempPackageName = null;
						}
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}).start();
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		unregisterReceiver(enterReceiver);
		unregisterReceiver(screenOffReceiver);
		unregisterReceiver(screenOnReceiver);
		unregisterReceiver(changeReceiver);
		enterReceiver = null;
		screenOffReceiver = null;
		screenOnReceiver = null;
		changeReceiver = null;

	}
}
