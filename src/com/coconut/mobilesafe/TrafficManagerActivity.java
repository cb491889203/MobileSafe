package com.coconut.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.domain.AppInfo;
import com.coconut.mobilesafe.engine.AppInfoProvider;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TrafficManagerActivity extends Activity {
	protected static final String TAG = "TrafficManagerActivity";
	private LinearLayout ll_loading;
	private ListView lv_traffic_infos;
	private AppManagerAdapter adapter;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private TextView tv_showcounts;

	/**
	 * 手机接收的总流量
	 */
	private long totalReceive = 0;
	/**
	 * 手机上传的总流量
	 */
	private long totalSend = 0;

	/**
	 * 安装包资源管理器
	 */
	private PackageManager pm;
	private TextView tv_total_traffic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_traffic_manager);
		pm = getPackageManager();
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_traffic_infos = (ListView) findViewById(R.id.lv_traffic_infos);
		tv_total_traffic = (TextView) findViewById(R.id.tv_total_traffic);
		tv_showcounts = (TextView) findViewById(R.id.tv_showcounts);

		loadData();

		// listView滚动事件,用于永久展示用户应用或系统应用的个数.
		lv_traffic_infos.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem > userAppInfos.size()) {
					tv_showcounts.setText("系统应用" + systemAppInfos.size() + "个");
				} else {
					tv_showcounts.setText("用户应用" + userAppInfos.size() + "个");
				}
			}
		});

	}

	/**
	 * 加载安装的软件信息,将数据存入List集合中,并对listView设置adapter
	 * 
	 */
	private void loadData() {
		// 开始加载安装的软件信息
		ll_loading.setVisibility(View.VISIBLE);
		userAppInfos = new ArrayList<AppInfo>();
		systemAppInfos = new ArrayList<AppInfo>();
		new Thread(new Runnable() {
			public void run() {
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				// 遍历集合,判断每一个应用是否为用户应用,如果是就存入用户应用集合中.
				for (AppInfo appInfo : appInfos) {
					if (appInfo.isUserApp()) {
						userAppInfos.add(appInfo);
					} else {
						systemAppInfos.add(appInfo);
					}
					try {
						long receive = TrafficStats.getUidRxBytes(pm.getApplicationInfo(appInfo.getPackname(), 0).uid);
						long send = TrafficStats.getUidTxBytes(pm.getApplicationInfo(appInfo.getPackname(), 0).uid);
						totalReceive += receive;
						totalSend += send;
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new AppManagerAdapter();
							lv_traffic_infos.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
						// 获取所有的流量使用量, 但是返回的是0,不知道怎么用.
						// long receive = TrafficStats.getMobileRxBytes();
						// long send = TrafficStats.getMobileTxBytes();
						tv_total_traffic.setText(
								"已用总流量:" + Formatter.formatFileSize(getApplicationContext(), totalReceive + totalSend));

					}
				});
			}
		}).start();
	}

	/**
	 * 展示安装应用的信息的listView适配器.
	 * 
	 * @author Administrator
	 *
	 */
	private class AppManagerAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return appInfos.size() + 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			AppInfoHolder holder = null;
			AppInfo appInfo = null;

			// 分别添加用户应用和系统应用个数的展示textView信息.
			if (position == 0) {
				TextView tv_userApp = new TextView(TrafficManagerActivity.this);
				tv_userApp.setText("用户应用" + userAppInfos.size() + "个");
				tv_userApp.setTextColor(Color.BLACK);
				tv_userApp.setTextSize(18);
				tv_userApp.setBackgroundColor(Color.GRAY);
				tv_userApp.setGravity(Gravity.CENTER_VERTICAL);
				tv_userApp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
				return tv_userApp;
			} else if (position == userAppInfos.size() + 1) {
				TextView tv_systemApp = new TextView(TrafficManagerActivity.this);
				tv_systemApp.setText("系统应用" + systemAppInfos.size() + "个");
				tv_systemApp.setTextColor(Color.BLACK);
				tv_systemApp.setBackgroundColor(Color.GRAY);
				tv_systemApp.setTextSize(18);
				tv_systemApp.setGravity(Gravity.CENTER_VERTICAL);
				tv_systemApp.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, 100));
				return tv_systemApp;
			} else if (position <= userAppInfos.size()) {
				appInfo = userAppInfos.get(position - 1);
			} else if (position > userAppInfos.size() + 1) {
				appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
			}

			// //将用户应用放置在前面.判断当前listView的位置是否小于用户应用的个数.
			// if (position < userAppInfos.size()) {
			// appInfo = userAppInfos.get(position);
			// }else{
			// appInfo = systemAppInfos.get(position);
			// }

			// 根据appInfo中的包名获取应用程序的uid,然后通过trafficManager类获取该应用的数据流量.
			long receive = 0;
			long send = 0;
			try {
				ApplicationInfo applicationInfo = pm.getApplicationInfo(appInfo.getPackname(), 0);
				int uid = applicationInfo.uid;
				receive = TrafficStats.getUidRxBytes(uid);
				send = TrafficStats.getUidTxBytes(uid);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (AppInfoHolder) view.getTag();
			} else {
				holder = new AppInfoHolder();
				view = View.inflate(getApplicationContext(), R.layout.listview_traffic_info, null);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
				holder.tv_traffic_size = (TextView) view.findViewById(R.id.tv_traffic_size);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_appname.setText(appInfo.getName());
			holder.tv_traffic_size.setText("已用流量:" + Formatter.formatFileSize(getApplicationContext(), send + receive));

			return view;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	public class AppInfoHolder {
		private TextView tv_appname;
		private TextView tv_traffic_size;
		private ImageView iv_icon;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

}
