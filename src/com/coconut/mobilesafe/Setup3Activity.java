package com.coconut.mobilesafe;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Setup3Activity extends SetupBaseActivity {

	private EditText et_number;
	private String safeNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		safeNumber = sp.getString("safeNumber", null);
		if (!TextUtils.isEmpty(safeNumber)) {
			et_number = (EditText) findViewById(R.id.et_number);
			et_number.setText(safeNumber);
		}

	}

	public void selectContacts(View view) {
		Intent intent = new Intent(Setup3Activity.this, SelectContactsActivity.class);
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		String number = (String) data.getExtras().getCharSequence("safeNumber",
				null);
		et_number = (EditText) findViewById(R.id.et_number);
		et_number.setText(number);
		Editor editor = sp.edit();
		editor.putString("safeNumber", number);
		editor.commit();
		
	}

	@Override
	public void showNextStep() {
		// TODO Auto-generated method stub
		if (!TextUtils.isEmpty(et_number.getText().toString())) {
			Intent intent = new Intent(Setup3Activity.this, Setup4Activity.class);
			startActivity(intent);
			finish();
			nextPageAnim();
		}else {
			Toast.makeText(Setup3Activity.this, "请选择安全号码", 0).show();
		}
		
	}

	@Override
	public void showPreStep() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Setup3Activity.this, Setup2Activity.class);
		startActivity(intent);
		finish();
		prePageAnim();
	}

}
