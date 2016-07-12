package com.coconut.mobilesafe;

import com.coconut.mobilesafe.service.AddressService;
import com.coconut.mobilesafe.service.ApplockService;
import com.coconut.mobilesafe.service.CallSmsSafeService;
import com.coconut.mobilesafe.ui.SettingClickView;
import com.coconut.mobilesafe.ui.SettingItemView;
import com.coconut.mobilesafe.utils.ServiceRunningUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingActivity extends Activity {

	protected static final String TAG = "SettingActivity";
	private SettingItemView siv_autoupdate;
	private SharedPreferences sp;
	private SettingItemView siv_address;
	private Intent intent;
	private Intent intent2;
	private SettingClickView scv_changebg;
	private String[] items = { "卫士蓝", "金属灰", "苹果绿", "活力橙", "半透明" };
	private SettingItemView siv_blacknumber_deny;
	private SettingItemView siv_applock;
	private Intent intent3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		siv_address = (SettingItemView) findViewById(R.id.siv_number_address);
		siv_autoupdate = (SettingItemView) findViewById(R.id.siv_autoupdate);
		scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
		
		int int1 = sp.getInt("toast_bg", 0);
		scv_changebg.setDesc(items[int1]);
		// tv_desc = (TextView) scv_changebg.findViewById(R.id.tv_desc);
		// tv_desc.setText(items[int1]);

		boolean b = sp.getBoolean("update", false);
		siv_autoupdate.setChecked(b);
		// 设置自动更新的控件点击事件
		siv_autoupdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_autoupdate.isChecked()) {
					siv_autoupdate.setChecked(false);
					// siv_autoupdate.setDesc("自动升级已经关闭");
					editor.putBoolean("update", false);
				} else {
					siv_autoupdate.setChecked(true);
					// siv_autoupdate.setDesc("自动升级已经打开");
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});

		// 设置来电显示
		intent = new Intent(SettingActivity.this, AddressService.class);
		// 通过一个工具类判断服务是否在后台开启.
		boolean running = ServiceRunningUtils.isServiceRunning(this, AddressService.class.getName());
		siv_address.setChecked(running);
		// 设置来电显示的控件点击事件
		siv_address.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Editor editor = sp.edit();
				if (siv_address.isChecked()) {
					siv_address.setChecked(false);
					// editor.putBoolean("showAddress", false);
					stopService(intent);
				} else {
					siv_address.setChecked(true);
					// editor.putBoolean("showAddress", true);
					startService(intent);

				}
				// editor.commit();
			}

		});
		// 设置来电显示背景颜色的点击事件
		scv_changebg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changebg();
			}
		});

		// 设置 黑名单拦截 是否打开!!
		siv_blacknumber_deny = (SettingItemView) findViewById(R.id.siv_blacknumber_deny);
		intent2 = new Intent(SettingActivity.this, CallSmsSafeService.class);
		// 通过一个工具类判断服务是否在后台开启.
		boolean running2 = ServiceRunningUtils.isServiceRunning(this, CallSmsSafeService.class.getName());
		siv_blacknumber_deny.setChecked(running2);

		// 设置黑名单拦截的点击事件
		siv_blacknumber_deny.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Editor editor = sp.edit();
				if (siv_blacknumber_deny.isChecked()) {
					siv_blacknumber_deny.setChecked(false);
					// editor.putBoolean("denyBlackNumber", false);
					stopService(intent2);
				} else {
					siv_blacknumber_deny.setChecked(true);
					// editor.putBoolean("denyBlackNumber", true);
					startService(intent2);

				}
				// editor.commit();
			}

		});
		
		//设置软件加密点击事件
		siv_applock = (SettingItemView) findViewById(R.id.siv_applock);
		boolean running3 = ServiceRunningUtils.isServiceRunning(this, ApplockService.class.getName());
		siv_applock.setChecked(running3);
		intent3 = new Intent(SettingActivity.this, ApplockService.class);
		siv_applock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (siv_applock.isChecked()) {
					siv_applock.setChecked(false);
					Log.i(TAG, "停止软甲加密服务");
					stopService(intent3);
				} else {
					siv_applock.setChecked(true);
					Log.i(TAG, "打开软甲加密服务");
					startService(intent3);
				}
			}
		});
	}

	protected void changebg() {
		AlertDialog.Builder builder = new Builder(SettingActivity.this);
		builder.setTitle("请选择背景颜色");

		int which = sp.getInt("toast_bg", 0);
		builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = sp.edit();
				editor.putInt("toast_bg", which);
				scv_changebg.setDesc(items[which]);
				editor.commit();
				dialog.dismiss();
			}

		});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean running = ServiceRunningUtils.isServiceRunning(this, AddressService.class.getName());
		siv_address.setChecked(running);
	}

}
