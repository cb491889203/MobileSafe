package com.coconut.mobilesafe.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.coconut.mobilesafe.R;
import com.coconut.mobilesafe.TaskManagerActivity;
import com.coconut.mobilesafe.engine.TaskInfoProvider;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService2 extends Service {

	protected static final String TAG = "UpdateWidgetService2";
	private TimerTask task;
	private Timer timer;
	private AppWidgetManager awm;
	private ScreenOffReceiver offReceiver;
	private ScreenOnReceiver onReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		offReceiver = new ScreenOffReceiver();
		onReceiver = new ScreenOnReceiver();
		registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
		registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
		startTimer();
	}

	private void startTimer() {
		if (timer == null && task == null) {

			timer = new Timer();
			task = new TimerTask() {

				@Override
				public void run() {
					Log.i(TAG, "更新控件数据");
					awm = AppWidgetManager.getInstance(UpdateWidgetService2.this);
					ComponentName provider = new ComponentName(getApplicationContext(),
							"com.coconut.mobilesafe.receiver.MyWidget");
					RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
					views.setTextViewText(R.id.process_count,
							"正在运行的软件:" + TaskInfoProvider.getRunningProcessesCounts(getApplicationContext()) + "个");
					views.setTextViewText(R.id.process_memory,
							"可用内存:" + Formatter.formatFileSize(getApplicationContext(),
									TaskInfoProvider.getAvailMem(getApplicationContext())));

					// PendingIntent 没有构造方法.只有PendingIntent.getService
					// getBroadCast
					// 等方法.
					Intent intent = new Intent("com.coconut.mobilesafe.killall");
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
//					Intent Intent2 = new Intent(up);
//					views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent2);
					awm.updateAppWidget(provider, views);

				}
			};
			timer.schedule(task, 0, 10000);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopTimer();
		unregisterReceiver(offReceiver);
		unregisterReceiver(onReceiver);
		offReceiver = null;
		onReceiver = null;
//		Intent intent = new Intent(getApplicationContext(), UpdateWidgetService.class);
//		startService(intent);
	}

	private void stopTimer() {
		if (timer != null && task != null) {
			task.cancel();
			timer.cancel();
			task = null;
			timer = null;
		}
	}

	public class ScreenOffReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "接收到锁屏广播,关闭计时器");
			stopTimer();
		}
	}

	public class ScreenOnReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "接收到开启屏幕广播,打开计时器");
			startTimer();
		}
	}
}
