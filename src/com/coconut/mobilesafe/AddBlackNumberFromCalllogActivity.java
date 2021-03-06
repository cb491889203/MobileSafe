package com.coconut.mobilesafe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coconut.mobilesafe.db.dao.BlackNumberDAO;
import com.coconut.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class AddBlackNumberFromCalllogActivity extends Activity {

	private List<Map<String, String>> data;
	private BlackNumberDAO dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_number_from_calllog);
		dao = new BlackNumberDAO(getApplicationContext());
		ListView lv_add_from_calllog = (ListView) findViewById(R.id.lv_add_from_calllog);
		data = showCalllog();
		lv_add_from_calllog.setAdapter(new SimpleAdapter(this, data, R.layout.listview_blacknumber_calllog,
				new String[] { "number", "time" }, new int[] { R.id.tv_calllog_number, R.id.tv_calllog_date }));
		lv_add_from_calllog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// View view2 = findViewById(R.layout.dialog_add_blacknumber);
				View view2 = View.inflate(AddBlackNumberFromCalllogActivity.this,
						R.layout.dialog_add_blacknumber_calllog, null);
				final CheckBox cb_savedDenyCall = (CheckBox) view2.findViewById(R.id.cb_deny_call);
				final CheckBox cb_savedDenySms = (CheckBox) view2.findViewById(R.id.cb_deny_sms);
				Button btn_confirm = (Button) view2.findViewById(R.id.btn_confirm);
				Button btn_cancel = (Button) view2.findViewById(R.id.btn_cancel);
				final String number = data.get(position).get("number");
				AlertDialog.Builder builder = new Builder(AddBlackNumberFromCalllogActivity.this);
				final AlertDialog dialog2 = builder.create();
				dialog2.setView(view2, 0, 0, 0, 0);
				dialog2.show();

				btn_confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dao.find(number)) {
							Toast.makeText(AddBlackNumberFromCalllogActivity.this, "该号码已经存在", 0).show();
							return;
						}
						int i = 0;
						if (!cb_savedDenyCall.isChecked() && !cb_savedDenySms.isChecked()) {
							Toast.makeText(AddBlackNumberFromCalllogActivity.this, "请选择拦截模式", 0).show();
							return;
						}
						if (cb_savedDenyCall.isChecked()) {
							i += 1;
						}
						if (cb_savedDenySms.isChecked()) {
							i += 2;
						}
						BlackNumberInfo info = new BlackNumberInfo();
						info.setBlackNumber(number);
						info.setMode(String.valueOf(i));
						dao.insert(number, String.valueOf(i));
						dialog2.dismiss();
						Toast.makeText(AddBlackNumberFromCalllogActivity.this, "添加成功", 0).show();
					}
				});
				btn_cancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog2.dismiss();
					}
				});
			}
		});
	}

	private List<Map<String, String>> showCalllog() {
		List<Map<String, String>> calllogs = new ArrayList<Map<String, String>>();
		Map<String,String> calllog = null;
		Cursor cursor = this.getContentResolver().query(CallLog.Calls.CONTENT_URI,
				new String[] { CallLog.Calls.NUMBER, CallLog.Calls.DATE }, null, null, "date desc");
		label: while (cursor.moveToNext()) {
			String number = cursor.getString(0);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date(Long.parseLong(cursor.getString(1)));
			// 呼叫时间
			String time = sfd.format(date);
			calllog = new HashMap<String, String>();
			for (Map<String, String> calllog1 : calllogs) {
				if (number.equals(calllog1.get("number"))) {
					continue label;
				}
			}
			calllog.put("number", number);
			calllog.put("time", time);
			calllogs.add(calllog);
		}
		return calllogs;

	}
}
