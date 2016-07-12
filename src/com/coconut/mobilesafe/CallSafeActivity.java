package com.coconut.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.db.dao.BlackNumberDAO;
import com.coconut.mobilesafe.domain.BlackNumberInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CallSafeActivity extends Activity {

	protected static final String TAG = "CallSafeActivity";
	private List<BlackNumberInfo> infos;
	private BlackNumberDAO dao;
	private MyAdapter adapter;
	private ListView lv_black_number;
	private AlertDialog dialog;
	private LinearLayout ll_loading;
	private int offset = 0;
	private int maxNumber = 20;
	private AlertDialog selectionDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_safe);
		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		dao = new BlackNumberDAO(getApplicationContext());
		infos = new ArrayList<BlackNumberInfo>();
		// 设置进度条为可见.
		firstLoadData();
		lv_black_number = (ListView) findViewById(R.id.lv_black_number);

		// 设置listView滚动事件监听器. 用于滚动刷新数据.
		// 刷新数据的开始位置.
		lv_black_number.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 空闲状态
					int lastVisiblePosition = lv_black_number.getLastVisiblePosition();

					if (lastVisiblePosition == infos.size() - 1) {
						Log.i(TAG, "数据拉倒最后一个了......");
						offset += maxNumber;
						loadData();
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING: // 惯性滚动状态

					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 触摸状态

					break;
				default:
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		// 点击每一个条目,进入修改号码的对话框中.可以修改黑名单号码, 2种拦截模式.
		lv_black_number.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// View view2 = findViewById(R.layout.dialog_add_blacknumber);
				View view2 = View.inflate(CallSafeActivity.this, R.layout.dialog_modifi_blacknumber, null);
				final EditText et_savedBlackNumber = (EditText) view2.findViewById(R.id.et_blacknumber);
				final CheckBox cb_savedDenyCall = (CheckBox) view2.findViewById(R.id.cb_deny_call);
				final CheckBox cb_savedDenySms = (CheckBox) view2.findViewById(R.id.cb_deny_sms);
				Button btn_confirm = (Button) view2.findViewById(R.id.btn_confirm);
				Button btn_cancel = (Button) view2.findViewById(R.id.btn_cancel);
				et_savedBlackNumber.setText(infos.get(position).getBlackNumber());
				String mode = infos.get(position).getMode();
				if ("3".equals(mode)) {
					cb_savedDenyCall.setChecked(true);
					cb_savedDenySms.setChecked(true);
				} else if ("2".equals(mode)) {
					cb_savedDenySms.setChecked(true);
				} else if ("1".equals(mode)) {
					cb_savedDenyCall.setChecked(true);
				}

				AlertDialog.Builder builder = new Builder(CallSafeActivity.this);
				final AlertDialog dialog2 = builder.create();
				dialog2.setView(view2, 0, 0, 0, 0);
				dialog2.show();

				btn_confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String blacknumber = et_savedBlackNumber.getText().toString().trim();
						Log.i(TAG, blacknumber);
						int i = 0;
						if (TextUtils.isEmpty(blacknumber)) {
							Log.i("黑名单号码=============", blacknumber);
							Toast.makeText(CallSafeActivity.this, "号码不能为空", 0).show();
							return;
						} else if (!cb_savedDenyCall.isChecked() && !cb_savedDenySms.isChecked()) {
							Toast.makeText(CallSafeActivity.this, "请选择拦截模式", 0).show();
							return;
						}
						if (cb_savedDenyCall.isChecked()) {
							i += 1;
						}
						if (cb_savedDenySms.isChecked()) {
							i += 2;
						}
						BlackNumberInfo info = new BlackNumberInfo();
						info.setBlackNumber(blacknumber);
						info.setMode(String.valueOf(i));
						dao.insert(blacknumber, String.valueOf(i));
						infos.add(0, info);
						;
						adapter.notifyDataSetChanged();
						dialog2.dismiss();
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

	/**
	 * 加载数据.
	 * 
	 * @param offset
	 *            从什么位置加载数据. 一次加载固定的20个数据.
	 */
	private void loadData() {
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (infos != null) {
					infos.addAll(dao.findPart(offset, maxNumber));
				} else {
					infos = dao.findPart(offset, maxNumber);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						// 设置进度条为不可见.
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_black_number.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			View view;
			if (convertView == null) {
				view = View.inflate(CallSafeActivity.this, R.layout.listview_black_number, null);
				holder = new ViewHolder();
				holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.tv_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			holder.tv_number.setText(infos.get(position).getBlackNumber());
			String mode = infos.get(position).getMode();
			if ("1".equals(mode)) {
				holder.tv_mode.setText("电话拦截");
			} else if ("2".equals(mode)) {
				holder.tv_mode.setText("短信拦截");
			} else {
				holder.tv_mode.setText("全部拦截");
			}
			holder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CallSafeActivity.this);
					builder.setTitle("请确认");
					builder.setMessage("确定要删除这条记录吗?");
					builder.setNegativeButton("取消", null);
					builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(infos.get(position).getBlackNumber());
							infos.remove(position);
							adapter.notifyDataSetChanged();
						}

					});
					builder.show();
				}
			});

			return view;
		}

		class ViewHolder {
			public TextView tv_number;
			public TextView tv_mode;
			public ImageView iv_delete;
		}

	}

	/**
	 * 点击后弹出一个对话框,可以选择添加方式.一共有 手动添加模式, 通讯录添加,短信添加.
	 * 
	 * @param view
	 */
	public void add(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		selectionDialog = builder.create();
		View dialog_addmode_selection = View.inflate(this, R.layout.dialog_addmode_selection, null);

		Button btn_cancel = (Button) dialog_addmode_selection.findViewById(R.id.btn_cancel);
		selectionDialog.setView(dialog_addmode_selection, 0, 0, 0, 0);
		selectionDialog.show();
		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				selectionDialog.dismiss();
			}
		});
	}

	/**
	 * 在弹出的对话框中点击手动添加,添加一个黑名单号码 到 数据库中,点击后弹出一个对话框,在对话框中输入要添加的号码,及拦截模式.
	 * 拦截模式用checkbox展示. 最后一行放2个按钮,确定和取消.
	 * 
	 * @param view
	 *            传入的view
	 */
	public void addHandly(View view) {
		selectionDialog.dismiss();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		dialog = builder.create();
		View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);

		final EditText et_blacknumber = (EditText) contentView.findViewById(R.id.et_blacknumber);
		final CheckBox cb_deny_call = (CheckBox) contentView.findViewById(R.id.cb_deny_call);
		final CheckBox cb_deny_sms = (CheckBox) contentView.findViewById(R.id.cb_deny_sms);
		Button confirm = (Button) contentView.findViewById(R.id.btn_confirm);
		Button cancel = (Button) contentView.findViewById(R.id.btn_cancel);
		dialog.setView(contentView, 0, 0, 0, 0);

		dialog.show();
		confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String blacknumber = et_blacknumber.getText().toString().trim();
				Log.i(TAG, blacknumber);
				if (dao.find(blacknumber)) {
					Toast.makeText(CallSafeActivity.this, "该号码已经存在", 0).show();
					return;
				}
				int i = 0;
				if (TextUtils.isEmpty(blacknumber)) {
					Log.i("黑名单号码=============", blacknumber);
					Toast.makeText(CallSafeActivity.this, "号码不能为空", 0).show();
					return;
				} else if (!cb_deny_call.isChecked() && !cb_deny_sms.isChecked()) {
					Toast.makeText(CallSafeActivity.this, "请选择拦截模式", 0).show();
					return;
				}
				if (cb_deny_call.isChecked()) {
					i += 1;
				}
				if (cb_deny_sms.isChecked()) {
					i += 2;
				}
				BlackNumberInfo info = new BlackNumberInfo();
				info.setBlackNumber(blacknumber);
				info.setMode(String.valueOf(i));
				dao.insert(blacknumber, String.valueOf(i));
				infos.add(0, info);
				;
				adapter.notifyDataSetChanged();
				dialog.dismiss();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 从通话记录中添加
	 * 
	 * @param view
	 */
	public void addFromCalllog(View view) {
		selectionDialog.dismiss();
		Intent intent = new Intent(this, AddBlackNumberFromCalllogActivity.class);
		startActivity(intent);

	}

	/**
	 * 从短信中添加
	 * 
	 * @param view
	 */
	public void addFromSms(View view) {
		selectionDialog.dismiss();
		Intent intent = new Intent(this, AddBlackNumberFromSmsActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		firstLoadData();
	}

	/**
	 * 刚打开页面时加载的20条数据.
	 * 
	 * @param offset
	 * 
	 */
	private void firstLoadData() {
		infos.clear();
		ll_loading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {
			@Override
			public void run() {

				if (infos != null) {
					infos.addAll(dao.findPart(0, maxNumber));
				} else {
					infos = dao.findPart(0, maxNumber);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						// 设置进度条为不可见.
						ll_loading.setVisibility(View.INVISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_black_number.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}
		}).start();
	}

}
