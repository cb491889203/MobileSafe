package com.coconut.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.coconut.mobilesafe.ui.SettingItemView;

public abstract class SetupBaseActivity extends Activity {

	private GestureDetector detector;
	protected SharedPreferences sp;
	private SettingItemView siv_bindSIM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_setup1);
		
		detector = new GestureDetector(SetupBaseActivity.this,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {
						if ((e1.getX() - e2.getX() > 150)
								&& Math.abs(e1.getY() - e2.getY()) < 150) {
							showNextStep();
							return true;
						}
						if ((e2.getX() - e1.getX() > 150)
								&& Math.abs(e1.getY() - e2.getY()) < 150) {
							showPreStep();
							return true;
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}
				});
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void nextStep(View view) {
		showNextStep();
	}

	public void preStep(View view) {
		showPreStep();
	}

	public abstract void showNextStep();

	public abstract void showPreStep();

	public void nextPageAnim() {
		//这个方法 要求在 finish()或者StartActivity()方法后面执行
		overridePendingTransition(R.anim.next_in, R.anim.next_out);
	}

	public void prePageAnim() {
		overridePendingTransition(R.anim.pre_in, R.anim.pre_out);
	}

}
