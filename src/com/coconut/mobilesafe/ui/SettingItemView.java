package com.coconut.mobilesafe.ui;

import com.coconut.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemView extends RelativeLayout {
	private TextView tv_title;
	private TextView tv_desc;
	private CheckBox cb_confirm;
	private String desc_on;
	private String desc_off;
	

	private void initView(Context context) {
		View view = View.inflate(context, R.layout.view_setting_item, this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_desc = (TextView) findViewById(R.id.tv_desc);
		cb_confirm = (CheckBox) findViewById(R.id.cb_confirm);
	}

	public SettingItemView(Context context) {
		super(context);
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.coconut.mobilesafe", "title");
		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.coconut.mobilesafe", "desc_on");
		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.coconut.mobilesafe", "desc_off");
		tv_title.setText(title);
		tv_desc.setText(desc_off);
//		setDesc(desc_off);
	}

	/**
	 * @return 判断组合控件是否被选中了 。
	 */
	public boolean isChecked() {
		return cb_confirm.isChecked();
	}

	/**
	 * @param checked
	 *            手动设置组合控件的选中状态
	 */
	public void setChecked(boolean checked) {
		if (checked) {
			tv_desc.setText(desc_on);
		}else {
			tv_desc.setText(desc_off);
		}
		cb_confirm.setChecked(checked);
	}
//	public void setDesc(String str){
//		tv_desc.setText(str);
//		
//	}
}
