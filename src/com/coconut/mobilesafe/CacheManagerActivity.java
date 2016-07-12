package com.coconut.mobilesafe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.domain.CacheInfo;
import com.coconut.mobilesafe.domain.TaskInfo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CacheManagerActivity extends Activity {

	protected static final String TAG = "CacheManagerActivity";

	/**
	 * 展示缓存信息的ListVIew
	 */
	private ListView lv_cache_infos;
	/**
	 * 保存所有获取到的cache信息的arrayList, 包括 软件名, 进程报名,软件图标,是否是用户进程.
	 */
	private List<CacheInfo> cacheInfos;

	/**
	 * 用于将cache信息保存在List<CacheInfo>中.
	 */
	private CacheInfo cacheInfo;

	/**
	 * ListView的数据适配器
	 */
	private MyAdapter adapter;
	private TextView tv_cache_stats;

	private ProgressBar pb_cache_scan;

	private PackageManager pm;

	private Method method;

	private LinearLayout ll_loading;

	private long totalSize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_manager);
		pm = getPackageManager();

		lv_cache_infos = (ListView) findViewById(R.id.lv_cache_infos);
		tv_cache_stats = (TextView) findViewById(R.id.tv_cache_stats);
		pb_cache_scan = (ProgressBar) findViewById(R.id.pb_cache_scan);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

		cacheInfos = new ArrayList<CacheInfo>();
		method = null;
		try {
			method = PackageManager.class.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadData();

	}

	

	/**
	 * @author Administrator 陈宝 ListView的数据适配器
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return cacheInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyHolder holder = new MyHolder();
			cacheInfo = cacheInfos.get(position);
			View view = View.inflate(getApplicationContext(), R.layout.listview_cache_info, null);

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (MyHolder) view.getTag();
			} else {
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_cache_name = (TextView) view.findViewById(R.id.tv_cache_name);
				holder.tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
				view.setTag(holder);
			}

			holder.tv_cache_name.setText(cacheInfo.getCacheName());
			holder.iv_icon.setImageDrawable(cacheInfo.getIcon());
			holder.tv_cache_size
					.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), cacheInfo.getCacheSize()));
			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	/**
	 * 给ListView 加载数据的方法.
	 */
	private void loadData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {
				int progress = 0;
				List<PackageInfo> packages = pm.getInstalledPackages(0);
				pb_cache_scan.setMax(packages.size());
				for (final PackageInfo packageInfo : packages) {
					try {
						method.invoke(pm, packageInfo.packageName, new MyStatsObserver());
						Thread.sleep(20);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					progress++;
					pb_cache_scan.setProgress(progress);
					Log.i(TAG, "----------------progress =" + progress + "---------------");

				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				runOnUiThread(new Runnable() {
					public void run() {
						tv_cache_stats
								.setText("扫描完成,共计缓存:" + Formatter.formatFileSize(getApplicationContext(), totalSize));
						Log.i(TAG, "扫描完成");
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_cache_infos.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	private static class MyHolder {
		private ImageView iv_icon;
		private TextView tv_cache_name;
		private TextView tv_cache_size;
	}

	private class MyStatsObserver extends IPackageStatsObserver.Stub {

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			CacheInfo cacheInfo = new CacheInfo();
			long size = pStats.cacheSize;
			totalSize += size;
			cacheInfo.setCacheSize(size);
			String cacheName = null;
			Drawable icon = null;
			final String name = pStats.packageName;
			if (size > 0) {

				try {
					cacheName = pm.getApplicationInfo(pStats.packageName, 0).loadLabel(pm).toString();
					icon = pm.getApplicationInfo(pStats.packageName, 0).loadIcon(pm);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					cacheName = "系统应用";
					icon = CacheManagerActivity.this.getResources().getDrawable(R.drawable.ic_default);
				}
				cacheInfo.setCacheName(cacheName);
				cacheInfo.setPackageName(name);
				cacheInfo.setIcon(icon);
				Log.i(TAG, cacheInfo.toString());
				cacheInfos.add(0, cacheInfo);
			}

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tv_cache_stats.setText("正在扫描:" + name);
					tv_cache_stats.setSingleLine();

				}
			});
		}

	}

	private class MyDataObserver extends IPackageDataObserver.Stub {

		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
			Log.i(TAG, packageName+"清理结果:"+succeeded);
		}

	}
	public void cleanAllCache(View view) {
		Method[] methods = PackageManager.class.getMethods();
		for (Method method : methods) {
			if ("freeStorageAndNotify".equals(method.getName())) {
				try {
					method.invoke(pm, Integer.MAX_VALUE, new MyDataObserver());
					Toast.makeText(getApplicationContext(),
							"共清理缓存:" + Formatter.formatFileSize(getApplicationContext(), totalSize), 0).show();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cacheInfos.clear();
				adapter.notifyDataSetChanged();
			}
		}
	}
}
