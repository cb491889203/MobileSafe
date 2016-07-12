package com.coconut.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

public class ServiceRunningUtils {

	public static boolean isServiceRunning(Context context, String service) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String name = runningServiceInfo.service.getClassName();
			Log.i("运行的服务", "服务名:"+ name);
			if (name.equals(service)) {
				return true;
			}
		}
		return false;
	}
}
