package com.coconut.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class CacheInfo {
	private String cacheName;
	private Drawable icon;
	private long cacheSize;
	private boolean isChecked;
	private String packageName;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public long getCacheSize() {
		return cacheSize;
	}

	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	@Override
	public String toString() {
		return "CacheInfo [cacheName=" + cacheName + ", icon=" + icon + ", cacheSize=" + cacheSize + "]";
	}

}
