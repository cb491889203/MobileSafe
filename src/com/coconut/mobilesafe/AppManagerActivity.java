package com.coconut.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.db.dao.AppLockDAO;
import com.coconut.mobilesafe.domain.AppInfo;
import com.coconut.mobilesafe.engine.AppInfoProvider;
import com.coconut.mobilesafe.utils.DensityUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.Layout;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AppManagerActivity extends Activity implements OnClickListener {
	private LinearLayout ll_loading;
	private ListView lv_app_infos;
	private AppManagerAdapter adapter;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private TextView tv_showcounts;
	private PopupWindow popupWindow;

	/**
	 * 气泡窗体的点击按钮
	 */
	private LinearLayout ll_openAPP;
	/**
	 * 
	 * 气泡窗体的点击按钮
	 */
	private LinearLayout ll_uninstall;
	/**
	 * 气泡窗体的点击按钮
	 */
	private LinearLayout ll_shareAPP;

	private AppInfo appInfo;
	/**
	 * 安装包资源管理器
	 */
	private PackageManager pm;
	private SharedPreferences sp;
	private AppLockDAO dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		dao = new AppLockDAO(getApplicationContext());
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_app_infos = (ListView) findViewById(R.id.lv_app_infos);
		tv_showcounts = (TextView) findViewById(R.id.tv_showcounts);
		TextView tv_rom = (TextView) findViewById(R.id.tv_rom);
		TextView tv_sdcard = (TextView) findViewById(R.id.tv_sdcard);
		long romSize = getAvailableSpace(Environment.getDataDirectory().getPath());
		long sdCardSize = getAvailableSpace(Environment.getExternalStorageDirectory().getPath());
		String rom = Formatter.formatFileSize(getApplicationContext(), romSize);
		String sdCard = Formatter.formatFileSize(getApplicationContext(), sdCardSize);
		tv_rom.setText("可用内部空间:" + rom);
		tv_sdcard.setText("可用外部空间:" + sdCard);
		loadData();

		// listView长按条目的点击事件.用于开开关闭APPLock
		lv_app_infos.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AppInfoHolder holder = (AppInfoHolder) view.getTag();
				if (position == 0 || position == userAppInfos.size() + 1) {
					return true;
				} else if (position <= userAppInfos.size()) {
					appInfo = userAppInfos.get(position - 1);
				} else {
					appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
				}
				// 默认刚开始时全都不加锁的状态,长按后就换成加锁图标,并且将该APP名称存入数据库.
				// 以后打开后,先从数据库中查询哪些是加锁的,listView中getView查询.长按后将该软件的lock图标换成open或者locked状态.
				String packagename = appInfo.getPackname();
				if (dao.find(packagename)) {
					dao.delete(packagename);
					holder.iv_applock.setImageDrawable(getResources().getDrawable(R.drawable.applock_open));
				} else {
					dao.insert(packagename);
					holder.iv_applock.setImageDrawable(getResources().getDrawable(R.drawable.applock_locked));
				}
				return true;
			}
		});

		// listView滚动事件,用于永久展示用户应用或系统应用的个数.
		lv_app_infos.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				dismissPopupWindow();
				if (firstVisibleItem > userAppInfos.size()) {
					tv_showcounts.setText("系统应用" + systemAppInfos.size() + "个");
				} else {
					tv_showcounts.setText("用户应用" + userAppInfos.size() + "个");
				}
			}
		});

		// listView的点击事件,用于点击一个条目展示更多信息,且是使用弹出气泡的方式.
		lv_app_infos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0 || position == userAppInfos.size() + 1) {
					return;
				} else if (position <= userAppInfos.size()) {
					appInfo = userAppInfos.get(position - 1);
				} else {
					appInfo = systemAppInfos.get(position - userAppInfos.size() - 2);
				}
				dismissPopupWindow();

				// TextView contentView = new TextView(getApplicationContext());
				// contentView.setText(appInfo.getPackname());
				View contentView = View.inflate(getApplicationContext(), R.layout.view_popupwindow, null);
				popupWindow = new PopupWindow(contentView, -2, -2);
				// 动画效果的播放必须要求窗体有背景颜色。
				// 透明颜色
				popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				int[] location = new int[2];
				view.getLocationInWindow(location);
				int dp = 60;
				int px = DensityUtils.dip2px(getApplicationContext(), dp);
				int px1 = DensityUtils.dip2px(getApplicationContext(), 40);
				popupWindow.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, px, location[1] - px1);
				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
				aa.setDuration(300);
				ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 1.0f);
				sa.setDuration(300);
				AnimationSet as = new AnimationSet(false);
				as.addAnimation(sa);
				as.addAnimation(aa);
				contentView.startAnimation(as);

				ll_openAPP = (LinearLayout) contentView.findViewById(R.id.ll_openAPP);
				ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
				ll_shareAPP = (LinearLayout) contentView.findViewById(R.id.ll_shareAPP);
				ll_openAPP.setOnClickListener(AppManagerActivity.this);
				ll_uninstall.setOnClickListener(AppManagerActivity.this);
				ll_shareAPP.setOnClickListener(AppManagerActivity.this);

			}

		});

	}

	/**
	 * 去除popupwindow
	 */
	private void dismissPopupWindow() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
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
				}

				runOnUiThread(new Runnable() {
					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new AppManagerAdapter();
							lv_app_infos.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}

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
				TextView tv_userApp = new TextView(AppManagerActivity.this);
				tv_userApp.setText("用户应用" + userAppInfos.size() + "个");
				tv_userApp.setTextColor(Color.BLACK);
				tv_userApp.setTextSize(18);
				tv_userApp.setBackgroundColor(Color.GRAY);
				tv_userApp.setGravity(Gravity.CENTER_VERTICAL);
				tv_userApp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
				return tv_userApp;
			} else if (position == userAppInfos.size() + 1) {
				TextView tv_systemApp = new TextView(AppManagerActivity.this);
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

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (AppInfoHolder) view.getTag();
			} else {
				holder = new AppInfoHolder();
				view = View.inflate(getApplicationContext(), R.layout.listview_app_info, null);
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
				holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
				holder.iv_applock = (ImageView) view.findViewById(R.id.iv_applock);
				view.setTag(holder);
			}
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_appname.setText(appInfo.getName());
			if (appInfo.isInRom()) {
				holder.tv_location.setText("手机内存");
			} else {
				holder.tv_location.setText("外部存储");
			}
			// 查询applock数据,该appinfo的名称是否在数据库中,在的话就将iv_applock设为locked,不在就是open;
			if (dao.find(appInfo.getPackname())) {
				holder.iv_applock.setImageDrawable(getResources().getDrawable(R.drawable.applock_locked));
			} else {
				holder.iv_applock.setImageDrawable(getResources().getDrawable(R.drawable.applock_open));
			}

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
		private TextView tv_location;
		private ImageView iv_icon;
		private ImageView iv_applock;

	}

	/**
	 * 获取磁盘可用空间的大小.
	 * 
	 * @param path
	 *            传入一个路径,该路径是要获取大小的一个磁盘路径
	 * @return 返回可用空间的大小值
	 */
	private long getAvailableSpace(String path) {
		StatFs statFs = new StatFs(path);
		long count = statFs.getBlockCount();
		long availableBlocks = statFs.getAvailableBlocks();
		long blockSize = statFs.getBlockSize();
		return availableBlocks * blockSize;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dismissPopupWindow();
	}

	/*
	 * (non-Javadoc) popupWindow弹出气泡的三个点击事件, 打开软件 , 卸载软件,分享软件
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_openAPP:
			openAPP();
			break;
		case R.id.ll_uninstall:
			if (appInfo.isUserApp()) {
				uninstallAPP();
			} else {
				Toast.makeText(getApplicationContext(), "系统软件只有ROOT权限才能卸载", 0).show();
			}
			break;
		case R.id.ll_shareAPP:
			shareAPP();
			break;
		default:
			break;
		}
	}

	/**
	 * 打开气泡中点开的应用
	 */
	private void openAPP() {
		pm = getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(appInfo.getPackname());
		if (intent != null) {
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "不能启动该应用", 0).show();
		}
	}

	/**
	 * 卸载气泡中点开的应用
	 */
	private void uninstallAPP() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + appInfo.getPackname()));
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 删除软件后,更新界面.
		loadData();
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 分享气泡中点开的应用
	 */
	private void shareAPP() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "自己开发的手机卫士,好用干净,无广告!!!欢迎使用!!");
		startActivity(intent);
	}

	public void modifyPwd(View view) {
		AlertDialog.Builder builder = new Builder(AppManagerActivity.this);
		final AlertDialog dialog = builder.create();
		View view2 = View.inflate(AppManagerActivity.this, R.layout.dialog_applock_modifypwd, null);
		final EditText et_password = (EditText) view2.findViewById(R.id.et_password);
		final EditText et_confirmPwd = (EditText) view2.findViewById(R.id.et_confirm_password);
		Button btn_confirm = (Button) view2.findViewById(R.id.btn_confirm);
		Button btn_cancel = (Button) view2.findViewById(R.id.btn_cancel);
		dialog.setView(view2, 0, 0, 0, 0);
		dialog.show();
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				String password = et_password.getText().toString();
				String confirmPwd = et_confirmPwd.getText().toString();
				if (password.equals(confirmPwd) && !TextUtils.isEmpty(password)) {
					Toast.makeText(getApplicationContext(), "密码一致,密码修改成功", 0).show();
					editor.putString("applockPwd", confirmPwd);
					editor.commit();
					dialog.dismiss();
				} else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPwd)) {
					Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
				} else {
					Toast.makeText(getApplicationContext(), "密码不一致", 0).show();
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

	}

}
