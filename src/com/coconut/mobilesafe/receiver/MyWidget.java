package com.coconut.mobilesafe.receiver;

import com.coconut.mobilesafe.service.UpdateWidgetService;
import com.coconut.mobilesafe.service.UpdateWidgetService2;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyWidget extends AppWidgetProvider {

	private static final String TAG = "MyWidget";

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent intent1 = new Intent(context, UpdateWidgetService.class);
		context.startService(intent1);
		Intent intent2 = new Intent(context, UpdateWidgetService2.class);
		context.startService(intent2);
		Log.i(TAG, "桌面控件onReceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
	}

	@Override
	public void onEnabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.startService(intent);
		Intent intent2 = new Intent(context, UpdateWidgetService2.class);
		context.startService(intent2);
		Log.i(TAG, "桌面控件onEnabled");
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		Intent intent = new Intent(context, UpdateWidgetService.class);
		context.stopService(intent);
//		Intent intent2 = new Intent(context, UpdateWidgetService2.class);
//		context.stopService(intent2);
		Log.i(TAG, "桌面控件onDisabled");
		super.onDisabled(context);
	}

}
