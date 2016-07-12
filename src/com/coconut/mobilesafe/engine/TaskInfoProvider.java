package com.coconut.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.R;
import com.coconut.mobilesafe.domain.TaskInfo;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;
import com.jaredrummler.android.processes.models.AndroidProcess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class TaskInfoProvider {

	private static final String TAG = "TaskInfoProvider";

	/**
	 * 获取所有运行中的进程数量
	 * 
	 * @param context
	 *            传入一个上下文
	 * @return 返回所有进程数量
	 */
	public static int getRunningProcessesCounts(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		// List<RunningAppProcessInfo> runningAppProcesses =
		// am.getRunningAppProcesses();
		List<AndroidAppProcess> runningAppProcesses = ProcessManager.getRunningAppProcesses();
		return runningAppProcesses.size();
	}

	/**
	 * 获取所有内存中的可用内存.
	 * 
	 * @param context
	 * @return
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * 获取总共内存.
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("NewApi")
	public static long getTotalMem(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		// 4.0以上完全可以用这个方法, 4.0 以下需要找到/meminfo/读取其中的数据.
		return outInfo.totalMem;
	}

	public static List<TaskInfo> getTaskInfos(Context context) {
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		TaskInfo taskInfo = null;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		// 这是开源项目里的获取方法.
		// 返回所有运行的APP进程:是用户的APP还是什么呢?--------答案是:用户APP和系统APP都可以查找出来.
		// 这是我们需要的
		List<AndroidAppProcess> appProcess = ProcessManager.getRunningAppProcesses();
		for (AndroidAppProcess androidAppProcess : appProcess) {
			taskInfo = new TaskInfo();
			// Log.i(TAG, androidAppProcess.name) ;
			// Log.i(TAG, androidAppProcess.getPackageName()) ;
			// Log.i(TAG, "以下是androidAppProcess.getPackageInfo()的信息");

			// 获取该进程的占用空间
			android.os.Debug.MemoryInfo[] processMemoryInfo = am
					.getProcessMemoryInfo(new int[] { androidAppProcess.pid });
			long occupyMem = processMemoryInfo[0].getTotalPrivateDirty() * 1024l;
			taskInfo.setOccupyMem(occupyMem);
			// 获取进程名,应用包名,图标,是否是用户进程等信息.
			PackageInfo packageInfo = null;
			try {
				packageInfo = androidAppProcess.getPackageInfo(context, 0);
//				Log.i(TAG, packageInfo.packageName);
				String name = packageInfo.applicationInfo.loadLabel(pm).toString();
				taskInfo.setAppName(name);
				taskInfo.setTaskName(androidAppProcess.name);
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				taskInfo.setIcon(icon);
				int flag = packageInfo.applicationInfo.flags;
				if ((flag & packageInfo.applicationInfo.FLAG_SYSTEM) == 0) {
					taskInfo.setUserTask(true);
				} else {
					taskInfo.setUserTask(false);
				}
				// Log.i(TAG,name) ;
				// Log.i(TAG,"一个进程信息结束---------------------") ;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				taskInfo.setAppName(androidAppProcess.name);
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
			}
			taskInfos.add(taskInfo);
		}

		// 返回所有的设备上的进程: 包含用户APP和系统进程吗?
		// 答案:----也只是显示一些系统级的信息.
		// List<AndroidProcess> runningProcesses =
		// ProcessManager.getRunningProcesses();
		// for (AndroidProcess androidProcess : runningProcesses) {
		// String processName = androidProcess.name;
		// androidProcess.
		// }

		// 返回APP进程信息.a list of RunningAppProcessInfo records, or null if there
		// are no running processes (it will not return an empty list). This
		// list ordering is not specified.
		// 答案:----------只是显示一些系统级的信息.
		// List<RunningAppProcessInfo> appProcessInfo =
		// ProcessManager.getRunningAppProcessInfo(context);
		// for (RunningAppProcessInfo runningAppProcessInfo : appProcessInfo) {
		// runningAppProcessInfo.processName;
		// }

		// 以下是5.0以下SDK的使用方法,现在已经失效!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

		// List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
		// for (RunningAppProcessInfo runningAppProcessInfo : processes) {
		// taskInfo = new TaskInfo();
		// int pid = runningAppProcessInfo.pid;
		// String taskName = runningAppProcessInfo.processName;
		// android.os.Debug.MemoryInfo[] memoryInfos =
		// am.getProcessMemoryInfo(new int[] { pid });
		// long totalPrivateDirty = memoryInfos[0].getTotalPrivateDirty() *
		// 1024;
		// taskInfo.setTaskName(taskName);
		// taskInfo.setOccupyMem(totalPrivateDirty);
		// try {
		// ApplicationInfo applicationInfo = pm.getApplicationInfo(taskName, 0);
		// taskInfo.setIcon(applicationInfo.loadIcon(pm));
		// taskInfo.setAppName(applicationInfo.loadLabel(pm).toString());
		// if ((applicationInfo.flags & applicationInfo.FLAG_SYSTEM) == 0) {
		// taskInfo.setUserTask(true);
		// } else {
		// taskInfo.setUserTask(false);
		// }
		// } catch (NameNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
		// taskInfo.setAppName(taskName);
		// }
		// taskInfos.add(taskInfo);
		// }
		return taskInfos;
	}
}
