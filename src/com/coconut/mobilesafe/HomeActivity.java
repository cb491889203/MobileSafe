package com.coconut.mobilesafe;

import com.coconut.mobilesafe.utils.MD5Encode;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private GridView gv_home;
	private static String names[] = { "手机防盗", "通话卫士", "软件管理", "进程管理", "流量统计", "手机杀毒", "缓存管理", "高级工具", "设置中心" };
	private static int[] itemId = { R.drawable.home_safe, R.drawable.home_callmsgsafe, R.drawable.home_app,
			R.drawable.home_taskmanager, R.drawable.home_netmanager, R.drawable.home_trojan,
			R.drawable.home_sysoptimize, R.drawable.home_atools, R.drawable.home_settings };

	private View view;
	private EditText et_password;
	private EditText et_confirm_password;
	private Button btn_confirm;
	private Button btn_cancel;
	private SharedPreferences sp;
	private AlertDialog enterPasswordDialog;
	private AlertDialog setupPasswordDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.list_home);
		MyAdapter adapter = new MyAdapter();
		gv_home.setAdapter(adapter);

		gv_home.setOnItemClickListener(new OnItemClickListener() {

			private Intent intent;

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					showLostFindDialog();
					break;

				case 1:
					intent = new Intent(HomeActivity.this, com.coconut.mobilesafe.CallSafeActivity.class);
					startActivity(intent);
					break;

				case 2:
					intent = new Intent(HomeActivity.this, AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(HomeActivity.this, TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4:
					intent = new Intent(HomeActivity.this, TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 5:
					intent = new Intent(HomeActivity.this, AntiVirusActitivy.class);
					startActivity(intent);
					break;
				case 6:
					intent = new Intent(HomeActivity.this, CacheManagerActivity.class);
					startActivity(intent);
					break;


				case 7:
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;

				case 8:
					intent = new Intent(HomeActivity.this, com.coconut.mobilesafe.SettingActivity.class);
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		});

	}

	protected void showLostFindDialog() {
		if (isSetupPassword()) {
			showEnterDialog();
		} else {
			showSetupPasswordDialog();
		}
	}

	private boolean isSetupPassword() {
		String password = sp.getString("password", null);
		return !TextUtils.isEmpty(password);
	}

	private void showSetupPasswordDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		setupPasswordDialog = builder.create();
		view = View.inflate(HomeActivity.this, R.layout.dialog_setup_password, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		setupPasswordDialog.setView(view, 0, 0, 0, 0);
		setupPasswordDialog.show();
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setupPasswordDialog.dismiss();
			}
		});
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				String confirmPassword = et_confirm_password.getText().toString().trim();
				// 验证密码是否正确
				if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
				} else if (password.equals(confirmPassword)) {
					// 密码正确，将密码保存在sp中，可以点击确认进入页面，也可点击取消返回主页。
					Editor editor = sp.edit();
					editor.putString("password", MD5Encode.encode(password));
					editor.commit();
					Toast.makeText(HomeActivity.this, "密码保存成功，正进入页面", 0).show();
					setupPasswordDialog.dismiss();
					enterLostFind();
				} else {
					Toast.makeText(HomeActivity.this, "密码不一致", 0).show();
				}

			}

		});

	}

	private void enterLostFind() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean b = sp.getBoolean("configed", false);
		if (b) {
			Intent intent = new Intent(HomeActivity.this, com.coconut.mobilesafe.LostFindActivity.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(HomeActivity.this, Setup1Activity.class);
			startActivity(intent);
		}

	}

	private void showEnterDialog() {
		AlertDialog.Builder builder = new Builder(HomeActivity.this);
		enterPasswordDialog = builder.create();
		view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
		et_password = (EditText) view.findViewById(R.id.et_password);
		et_confirm_password = (EditText) view.findViewById(R.id.et_confirm_password);
		btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
		btn_cancel = (Button) view.findViewById(R.id.btn_cancel);

		enterPasswordDialog.setView(view, 0, 0, 0, 0);
		enterPasswordDialog.show();
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				enterPasswordDialog.dismiss();
			}
		});
		btn_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = et_password.getText().toString().trim();
				// 验证密码是否正确
				if (TextUtils.isEmpty(password)) {
					Toast.makeText(HomeActivity.this, "密码为空", 0).show();
					return;
				}
				String savedPassword = sp.getString("password", null);
				if (savedPassword.equals(MD5Encode.encode(password))) {
					Toast.makeText(HomeActivity.this, "密码正确，正在进入页面", 0).show();
					enterLostFind();
					enterPasswordDialog.dismiss();
				} else {
					Toast.makeText(HomeActivity.this, "密码错误", 0).show();
				}

			}

		});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = getLayoutInflater();
			View view = inflater.inflate(R.layout.list_item_home, null);
			ImageView iv_list_item = (ImageView) view.findViewById(R.id.iv_list_item);
			TextView tv_list_item = (TextView) view.findViewById(R.id.tv_list_item);
			tv_list_item.setText(names[position]);
			iv_list_item.setImageResource(itemId[position]);
			return view;

		}

	}
}
