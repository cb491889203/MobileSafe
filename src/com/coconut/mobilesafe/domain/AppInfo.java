package com.coconut.mobilesafe.domain;

import javax.security.auth.PrivateCredentialPermission;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String name;
	private String packname;
	private Drawable icon;
	private boolean userApp;
	private boolean inRom;

	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public boolean isInRom() {
		return inRom;
	}

	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", packname=" + packname + ", icon=" + icon + "]";
	}

}
