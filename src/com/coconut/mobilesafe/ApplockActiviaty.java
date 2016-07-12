package com.coconut.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ApplockActiviaty extends Activity {
	private static final String TAG = "ApplockActiviaty";
	private PackageManager pm;
	private EditText et_applockpwd;
	private String packageName;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		TextView tv_desc = (TextView) findViewById(R.id.tv_desc);
		et_applockpwd = (EditText) findViewById(R.id.et_applockpwd);
		ImageView iv_icon = (ImageView) findViewById(R.id.iv_icon);
		packageName = getIntent().getStringExtra("packageName");
		pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
			Drawable icon = applicationInfo.loadIcon(pm);
			String label = applicationInfo.loadLabel(pm).toString();
			iv_icon.setImageDrawable(icon);
			tv_desc.setText(label + " 已加密,请输入密码");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enterAPP(View view) {
		String password = et_applockpwd.getText().toString().trim();
		String defaultpwd = sp.getString("applockPwd", null);
		if (TextUtils.isEmpty(password)) {
			Toast.makeText(getApplicationContext(), "密码不能为空", 0).show();
		}
		if (password.equals(defaultpwd)) {
			Intent intent = new Intent();
			intent.setAction("com.coconut.mobilesafe.enterAPP");
			intent.putExtra("packagename", packageName);
			Log.i(TAG, "进入程序,将临时包名发给服务,用于临时停止监听.");
			sendBroadcast(intent);
			finish();
		} else {
			Toast.makeText(getApplicationContext(), "密码错误", 0).show();
		}
	}

	@Override
	public void onBackPressed() {
		// 返回键直接返回桌面
		// 回桌面。
		// <action android:name="android.intent.action.MAIN" />
		// <category android:name="android.intent.category.HOME" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <category android:name="android.intent.category.MONKEY"/>
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");
		startActivity(intent);
		// 所有的activity最小化 不会执行ondestory 只执行 onstop方法。

		super.onBackPressed();
	}
}
