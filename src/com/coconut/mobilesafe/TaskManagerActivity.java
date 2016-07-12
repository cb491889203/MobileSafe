package com.coconut.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import com.coconut.mobilesafe.domain.TaskInfo;
import com.coconut.mobilesafe.engine.TaskInfoProvider;
import com.jaredrummler.android.processes.ProcessManager;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskManagerActivity extends Activity {

	protected static final String TAG = "TaskManagerActivity";
	/**
	 * 加载信息的进度条VIew
	 */
	private LinearLayout ll_loading;
	/**
	 * 展示进程信息的ListVIew
	 */
	private ListView lv_task_infos;
	/**
	 * 保存所有正在运行的进程信息的arrayList, 包括 软件名, 进程报名,软件图标,是否是用户进程.
	 */
	private List<TaskInfo> taskInfos;

	/**
	 * 用户进程的ListArray
	 */
	private List<TaskInfo> userTaskInfos;
	/**
	 * 系统进程的ListArray
	 */
	private List<TaskInfo> systemTaskInfos;
	/**
	 * ListView的数据适配器
	 */
	private MyAdapter adapter;
	private TextView showCounts;
	private LinearLayout ll_button;
	private ActivityManager am;
	private TextView tv_process_counts;
	private TextView tv_mem_info;
	private long availMem;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		tv_process_counts = (TextView) findViewById(R.id.tv_process_counts);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);

		ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
		lv_task_infos = (ListView) findViewById(R.id.lv_task_infos);
		showCounts = (TextView) findViewById(R.id.tv_showcounts);
		ll_button = (LinearLayout) findViewById(R.id.ll_button);
		// 加载数据
		loadData();
		// 添加滚动事件, 用于判断滚动到用户进程还是系统进程,显示进程的数量.
		lv_task_infos.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem <= userTaskInfos.size()) {
					showCounts.setText("用户进程" + userTaskInfos.size() + "个");
				} else {
					showCounts.setText("系统进程" + systemTaskInfos.size() + "个");
				}
			}
		});

		// 设置ListView条目的点击事件,并选中checkBox
		lv_task_infos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TaskInfo taskInfo = new TaskInfo();
				if (position == 0 || position == userTaskInfos.size() + 1) {
					return;
				} else if (position <= userTaskInfos.size()) {
					taskInfo = userTaskInfos.get(position - 1);
					if (getPackageName().equals(taskInfo.getTaskName())) {
						return;
					}
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
					} else {
						taskInfo.setChecked(true);
					}
				} else {
					taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
					} else {
						taskInfo.setChecked(true);
					}
				}
				// 通知ListView更新数据,将checkBox的数据展示出来.
				adapter.notifyDataSetChanged();
			}
		});

	}

	/**
	 * 全选ListView中的所有进程
	 * 
	 * @param view
	 *            传入的View
	 */
	public void slectALL(View view) {
		for (TaskInfo taskInfo : taskInfos) {
			if (getPackageName().equals(taskInfo.getTaskName())) {
				continue;
			}
			taskInfo.setChecked(true);
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 反选ListView中的选择了的进程
	 * 
	 * @param view
	 */
	public void selectReverse(View view) {
		for (TaskInfo taskInfo : taskInfos) {
			if (getPackageName().equals(taskInfo.getTaskName())) {
				continue;
			}
			if (taskInfo.isChecked()) {
				taskInfo.setChecked(false);
			} else {
				taskInfo.setChecked(true);
			}
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 停止选中了的所有进程
	 * 
	 * @param view
	 */
	public void stopPrecess(View view) {
		// List<TaskInfo> taskInfosAlive = new ArrayList<TaskInfo>();
		int totalCount = taskInfos.size();
		int count = 0;
		long totalSavedMem = 0;
		for (TaskInfo taskInfo : taskInfos) {
			if (taskInfo.isChecked()) {
				am.killBackgroundProcesses(taskInfo.getTaskName());
				// if (am. istaskInfo.getTaskName()) {
				// ProcessManager.
				// }
				count++;
				totalSavedMem += taskInfo.getOccupyMem();
				if (taskInfo.isUserTask()) {
					userTaskInfos.remove(taskInfo);
				} else {
					systemTaskInfos.remove(taskInfo);
				}
			}
		}
		// loadData();
		taskInfos.clear();
		taskInfos.addAll(userTaskInfos);
		taskInfos.addAll(systemTaskInfos);
		adapter.notifyDataSetChanged();
		// loadTitleData();

		// 手动修改TiltleData
		tv_process_counts.setText("当前总进程数:" + (totalCount - count));
		long availMem = TaskInfoProvider.getAvailMem(getApplicationContext());
		long totalMem = TaskInfoProvider.getTotalMem(getApplicationContext());
		tv_mem_info.setText("已用内存/总内存:" + Formatter.formatFileSize(getApplicationContext(), (availMem - totalSavedMem))
				+ "/" + Formatter.formatFileSize(getApplicationContext(), totalMem));

		Toast.makeText(getApplicationContext(),
				"共清理" + count + "个," + Formatter.formatFileSize(getApplicationContext(), totalSavedMem) + "内存", 0)
				.show();
	}

	/**
	 * 进入进程管理的设置页面
	 * 
	 * @param view
	 */
	public void settings(View view) {
		Intent intent = new Intent(TaskManagerActivity.this, TaskSettingActivity.class);
		startActivityForResult(intent, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}else {
			adapter = new MyAdapter();
			lv_task_infos.setAdapter(adapter);
		}
	}

	/**
	 * @author Administrator 陈宝 ListView的数据适配器
	 */
	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (sp.getBoolean("isshow", false)) {
				return taskInfos.size() + 2;
			} else {
				return userTaskInfos.size() + 1;
			}

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MyHolder holder = new MyHolder();
			TaskInfo taskInfo = null;
			View view = View.inflate(getApplicationContext(), R.layout.listview_task_info, null);

			if (position == 0) {
				TextView tv_userTask = new TextView(TaskManagerActivity.this);
				tv_userTask.setText("用户进程" + userTaskInfos.size() + "个");
				tv_userTask.setTextColor(Color.BLACK);
				tv_userTask.setTextSize(18);
				tv_userTask.setBackgroundColor(Color.GRAY);
				tv_userTask.setGravity(Gravity.CENTER_VERTICAL);
				tv_userTask.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
				return tv_userTask;
			} else if (position == userTaskInfos.size() + 1 && sp.getBoolean("isshow", false)) {
				TextView tv_systemTask = new TextView(TaskManagerActivity.this);
				tv_systemTask.setText("系统进程" + systemTaskInfos.size() + "个");
				tv_systemTask.setTextColor(Color.BLACK);
				tv_systemTask.setTextSize(18);
				tv_systemTask.setBackgroundColor(Color.GRAY);
				tv_systemTask.setGravity(Gravity.CENTER_VERTICAL);
				tv_systemTask.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
				return tv_systemTask;
			} else if (position <= userTaskInfos.size()) {
				taskInfo = userTaskInfos.get(position - 1);
			} else if (systemTaskInfos != null) {
				taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
			}

			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (MyHolder) view.getTag();
			} else {
				holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
				holder.tv_appname = (TextView) view.findViewById(R.id.tv_taskname);
				holder.tv_occupymem = (TextView) view.findViewById(R.id.tv_occupymem);
				holder.cb_checked = (CheckBox) view.findViewById(R.id.cb_checked);
				view.setTag(holder);
			}

			holder.tv_appname.setText(taskInfo.getAppName());
			holder.iv_icon.setImageDrawable(taskInfo.getIcon());
			holder.tv_occupymem
					.setText("占用内存:" + Formatter.formatFileSize(getApplicationContext(), taskInfo.getOccupyMem()));

			holder.cb_checked.setChecked(taskInfo.isChecked());
			if (getPackageName().equals(taskInfo.getTaskName())) {
				holder.cb_checked.setVisibility(View.INVISIBLE);
			} else {
				holder.cb_checked.setVisibility(View.VISIBLE);
			}

			return view;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	/**
	 * 给ListView 加载数据的方法.
	 */
	private void loadData() {

		ll_loading.setVisibility(View.VISIBLE);
		ll_button.setVisibility(View.INVISIBLE);
		userTaskInfos = new ArrayList<TaskInfo>();
		systemTaskInfos = new ArrayList<TaskInfo>();
		loadTitleData();

		new Thread(new Runnable() {
			@Override
			public void run() {
				taskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
				// List<AndroidAppProcess> runningAppProcesses =
				// ProcessManager.getRunningAppProcesses();
				Log.i(TAG, taskInfos.size() + "");

				for (TaskInfo taskInfo : taskInfos) {
					if (taskInfo.isUserTask()) {
						userTaskInfos.add(taskInfo);
					} else {
						systemTaskInfos.add(taskInfo);
					}
				}

				runOnUiThread(new Runnable() {

					public void run() {
						ll_loading.setVisibility(View.INVISIBLE);
						ll_button.setVisibility(View.VISIBLE);
						if (adapter == null) {
							adapter = new MyAdapter();
							lv_task_infos.setAdapter(adapter);
						} else {
							adapter.notifyDataSetChanged();
						}
					}
				});
			}

		}).start();
	}

	private void loadTitleData() {
		tv_process_counts.setText("当前总进程数:" + TaskInfoProvider.getRunningProcessesCounts(getApplicationContext()));
		availMem = TaskInfoProvider.getAvailMem(getApplicationContext());
		long totalMem = TaskInfoProvider.getTotalMem(getApplicationContext());
		tv_mem_info.setText("已用内存/总内存:" + Formatter.formatFileSize(getApplicationContext(), availMem) + "/"
				+ Formatter.formatFileSize(getApplicationContext(), totalMem));
	}

	private static class MyHolder {
		private ImageView iv_icon;
		private TextView tv_appname;
		private TextView tv_occupymem;
		private CheckBox cb_checked;
	}
}
