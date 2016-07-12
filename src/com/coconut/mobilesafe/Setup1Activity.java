package com.coconut.mobilesafe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Setup1Activity extends SetupBaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void showNextStep() {
		Intent intent = new Intent(Setup1Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		nextPageAnim();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showPreStep() {
		// TODO Auto-generated method stub
		
	}
}
