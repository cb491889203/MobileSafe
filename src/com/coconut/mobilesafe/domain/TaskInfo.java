package com.coconut.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class TaskInfo {
	private String appName;
	private String taskName;
	private long occupyMem;
	private boolean isUserTask;
	private Drawable icon;
	private boolean isChecked;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public long getOccupyMem() {
		return occupyMem;
	}

	public void setOccupyMem(long occupyMem) {
		this.occupyMem = occupyMem;
	}

	public boolean isUserTask() {
		return isUserTask;
	}

	public void setUserTask(boolean isUserTask) {
		this.isUserTask = isUserTask;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "TaskInfo [taskName=" + taskName + ", occupyMem=" + occupyMem + ", isUserTask=" + isUserTask + "]";
	}

}
