package com.coconut.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends SetupBaseActivity {

	private CheckBox cb_protect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		cb_protect = (CheckBox) findViewById(R.id.cb_protect);
		if(sp.getBoolean("protected", false)){
			cb_protect.setChecked(true);
			cb_protect.setText("已经开启防盗保护");
		}
		
		cb_protect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				if (isChecked) {
					cb_protect.setText("已经开启防盗保护");
					editor.putBoolean("protected", true);
				}else {
					cb_protect.setText("没有开启防盗保护");
					editor.putBoolean("protected", false);
				}
				editor.commit();
			}
		});
	}

	@Override
	public void showNextStep() {
		Intent intent = new Intent(Setup4Activity.this, LostFindActivity.class);
		startActivity(intent);
		finish();
		nextPageAnim();
	}

	@Override
	public void showPreStep() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Setup4Activity.this, Setup3Activity.class);
		startActivity(intent);
		finish();
		prePageAnim();
	}
}
