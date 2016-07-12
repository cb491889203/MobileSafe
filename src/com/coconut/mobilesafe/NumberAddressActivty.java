package com.coconut.mobilesafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.coconut.mobilesafe.db.dao.QueryAddress;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

public class NumberAddressActivty extends Activity {
	private EditText et_number;
	private TextView tv_location;
	private Vibrator vibrator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_number_address);
		et_number = (EditText) findViewById(R.id.editText1);
		tv_location = (TextView) findViewById(R.id.textView1);
		et_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() >= 3) {
					String query = QueryAddress.query(s.toString());
					tv_location.setText(query);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void queryNumber(View view) {
		if (TextUtils.isEmpty(tv_location.getText())) {
			 Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			 et_number.startAnimation(shake);
			 vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			 vibrator.vibrate(1000);
		}
		// 在数据库中查找相应的号码.
		// 先获得 号码
		String number = et_number.getText().toString();
		String address = QueryAddress.query(number);
		tv_location.setText(address);
	}
}
