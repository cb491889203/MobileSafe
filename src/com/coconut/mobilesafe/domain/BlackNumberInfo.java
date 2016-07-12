package com.coconut.mobilesafe.domain;

public class BlackNumberInfo {
	private String blackNumber;
	private String mode;
	public BlackNumberInfo(String blackNumber, String mode) {
		super();
		this.blackNumber = blackNumber;
		this.mode = mode;
	}
	public BlackNumberInfo() {
		super();
	}
	public String getBlackNumber() {
		return blackNumber;
	}
	public void setBlackNumber(String blackNumber) {
		this.blackNumber = blackNumber;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
}	
