package com.coconut.mobilesafe;

import com.coconut.mobilesafe.service.AutoCleanService;
import com.coconut.mobilesafe.utils.ServiceRunningUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class TaskSettingActivity extends Activity {
	protected static final String TAG = "TaskSettingActivity";
	private RelativeLayout rl_showsystem;
	private RelativeLayout rl_autoclear;
	private CheckBox cb_isshow;
	private CheckBox cb_isauto;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		rl_showsystem = (RelativeLayout) findViewById(R.id.rl_showsystem);
		rl_autoclear = (RelativeLayout) findViewById(R.id.rl_autoclear);
		cb_isshow = (CheckBox) rl_showsystem.findViewById(R.id.cb_isshow);
		cb_isauto = (CheckBox) rl_autoclear.findViewById(R.id.cb_isauto);
		cb_isshow.setChecked(sp.getBoolean("isshow", false));
		cb_isauto.setChecked(
				ServiceRunningUtils.isServiceRunning(getApplicationContext(), AutoCleanService.class.getName()));

		// 显示系统进程条目的点击事件,点击选中.
		rl_showsystem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (cb_isshow.isChecked()) {
					cb_isshow.setChecked(false);
					editor.putBoolean("isshow", false);
				} else {
					cb_isshow.setChecked(true);
					editor.putBoolean("isshow", true);
				}
				editor.commit();
			}
		});

		// 锁屏自动清理进程的点击事件,点击设置自动清理.
		// 开启一个服务,在服务中接收锁屏广播,开始自动清理.
		Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
		rl_autoclear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
				if (cb_isauto.isChecked()) {
					cb_isauto.setChecked(false);
					if (ServiceRunningUtils.isServiceRunning(getApplicationContext(),
							AutoCleanService.class.getName())) {
						Log.i(TAG, "关闭锁屏清理服务");
						stopService(intent);
					}
				} else {
					cb_isauto.setChecked(true);
					Log.i(TAG, "开启锁屏清理服务");
					startService(intent);
				}
			}
		});
	}

}
