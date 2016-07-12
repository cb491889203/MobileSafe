package com.coconut.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.domain.AppInfo;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * 用于提供手机中所有应用的信息.
 * 
 * @author Administrator
 *
 */
public class AppInfoProvider {
	// public static List<AppInfo> getAppInfos(Context context) {
	// PackageManager pm = context.getPackageManager();
	// List<PackageInfo> packages = pm.getInstalledPackages(0);
	// List<AppInfo> appInfos = new ArrayList<AppInfo>();
	// AppInfo appInfo = null;
	// for (PackageInfo packageInfo : packages) {
	// String packname = packageInfo.packageName;
	// ApplicationInfo info = packageInfo.applicationInfo;
	// String name = (String) info.loadLabel(pm);
	// Drawable icon = info.loadIcon(pm);
	// appInfo = new AppInfo();
	// appInfo.setName(name);
	// appInfo.setIcon(icon);
	// appInfo.setPackname(packname);
	// appInfos.add(appInfo);
	// }
	// return appInfos;
	// }

	/**
	 * 获取所有的安装的应用程序信息。
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();
		AppInfo appInfo;
		// 所有的安装在系统上的应用程序包信息。
		List<PackageInfo> packInfos = pm.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		for (PackageInfo packInfo : packInfos) {
			appInfo = new AppInfo();
			// packInfo 相当于一个应用程序apk包的清单文件
			String packname = packInfo.packageName;
			Drawable icon = packInfo.applicationInfo.loadIcon(pm);
			String name = packInfo.applicationInfo.loadLabel(pm).toString();
			int flags = packInfo.applicationInfo.flags;// 应用程序信息的标记 里面保存了所有可以相加的数据.
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户程序
				appInfo.setUserApp(true);
			} else {
				// 系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 手机的内存
				appInfo.setInRom(true);
			} else {
				// 手机外存储设备
				appInfo.setInRom(false);
			}
			appInfo.setPackname(packname);
			appInfo.setIcon(icon);
			appInfo.setName(name);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
