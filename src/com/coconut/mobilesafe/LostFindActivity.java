package com.coconut.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	private ImageView iv_lock;
	private TextView tv_safenumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lostfind);
		iv_lock = (ImageView) findViewById(R.id.iv_lock);
		tv_safenumber = (TextView) findViewById(R.id.tv_safenumber);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putBoolean("configed", true);
		editor.commit();
		String string = sp.getString("safeNumber", null);
		if (TextUtils.isEmpty(string)) {
			tv_safenumber.setText(null);
		}else {
			tv_safenumber.setText(string);
		}
		
		boolean b = sp.getBoolean("protected", false);
		if (b) {
			iv_lock.setImageResource(R.drawable.lostfind_locked);
		}else {
			iv_lock.setImageResource(R.drawable.lostfind_unlock);
		}
		
	}
	
	public void reSetup(View view){
		Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
		startActivity(intent);
	}
	
	public void backToHome(View view) {
		Intent intent = new Intent(LostFindActivity.this, HomeActivity.class);
		startActivity(intent);
	}
}
