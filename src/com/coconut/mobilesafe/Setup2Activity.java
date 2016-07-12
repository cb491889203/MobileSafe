package com.coconut.mobilesafe;

import com.coconut.mobilesafe.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class Setup2Activity extends SetupBaseActivity {
	private SettingItemView siv_bingSIM;
	private TelephonyManager tm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);

		siv_bingSIM = (SettingItemView) findViewById(R.id.siv_bindSIM);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String sim = sp.getString("SimSerialNumber", null);
		if (TextUtils.isEmpty(sim)) {
			siv_bingSIM.setChecked(false);
		} else {
			siv_bingSIM.setChecked(true);
		}
		siv_bingSIM.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_bingSIM.isChecked()) {
					siv_bingSIM.setChecked(false);
					// siv_autoupdate.setDesc("自动升级已经关闭");
					editor.putString("SimSerialNumber", null);
				} else {
					siv_bingSIM.setChecked(true);
					// siv_autoupdate.setDesc("自动升级已经打开");
					editor.putString("SimSerialNumber", tm.getSimSerialNumber());
				}
				editor.commit();
			}
		});
	}

	@Override
	public void showNextStep() {
		// TODO Auto-generated method stub
		if (siv_bingSIM.isChecked()) {
			Intent intent = new Intent(Setup2Activity.this,
					Setup3Activity.class);
			startActivity(intent);
			finish();
			nextPageAnim();
		}else {
			Toast.makeText(Setup2Activity.this, "请确定绑定SIM卡", 0).show();
		}
	}

	@Override
	public void showPreStep() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Setup2Activity.this, Setup1Activity.class);
		startActivity(intent);
		finish();
		prePageAnim();
	}
}
