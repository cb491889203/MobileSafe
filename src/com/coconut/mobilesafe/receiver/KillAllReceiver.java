package com.coconut.mobilesafe.receiver;

import java.util.List;

import com.coconut.mobilesafe.R;
import com.coconut.mobilesafe.engine.TaskInfoProvider;
import com.coconut.mobilesafe.service.UpdateWidgetService;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class KillAllReceiver extends BroadcastReceiver {

	private static final String TAG = "KillAllReceiver";
	private ActivityManager am;
	private AppWidgetManager awm;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "接收到Widget发来的请求,正在清理进程");
		am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
		for (AndroidAppProcess androidAppProcess : runningAppProcesses) {
			am.killBackgroundProcesses(androidAppProcess.getPackageName());
		}
		// 及时更新小控件的数据
		awm = AppWidgetManager.getInstance(context);
		ComponentName provider = new ComponentName(context, "com.coconut.mobilesafe.receiver.MyWidget");
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.process_widget);
		views.setTextViewText(R.id.process_count,
				"正在运行的软件:" + TaskInfoProvider.getRunningProcessesCounts(context) + "个");
		views.setTextViewText(R.id.process_memory,
				"可用内存:" + Formatter.formatFileSize(context, TaskInfoProvider.getAvailMem(context)));
		awm.updateAppWidget(provider, views);
		Log.i(TAG, "清理完成,手机已焕然一新!!");
		Toast.makeText(context, "清理完成,手机已焕然一新!!", 0).show();
	}

}
