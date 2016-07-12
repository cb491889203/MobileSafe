package com.coconut.mobilesafe.ui;

import com.coconut.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;

	private void initView(Context context) {
		View view = View.inflate(context, R.layout.view_setting_click, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
	}

	public SettingClickView(Context context) {
		super(context);
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public SettingClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.coconut.mobilesafe", "title");
		String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.coconut.mobilesafe", "desc_on");
		tv_title.setText(title);
		tv_desc.setText(desc);
		// setDesc(desc_off);
	}

	public void setChecked(boolean checked) {
		if (checked) {
			tv_desc.setText("这是空置");
		}
	}
	public void setDesc(String str) {
		// TODO Auto-generated method stub
		tv_desc.setText(str);
	}
}
