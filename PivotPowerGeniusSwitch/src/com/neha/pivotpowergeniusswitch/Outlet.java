package com.neha.pivotpowergeniusswitch;

import java.io.Serializable;

public class Outlet implements Serializable {
	private static final long serialVersionUID = -5835214846832634271L;
	
	public String mId;
	public String mName;
	public boolean mPowered;
	
	public Outlet(String id, String name, boolean powered) {
		this.mId = id;
		this.mName = name;
		this.mPowered = powered;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public boolean isPowered() {
		return mPowered;
	}
	
	@Override
	public String toString() {
		String poweredState = mPowered ? "ON" : "OFF";
		String text = "[" + poweredState + "] " +  this.getName();
		return text;
	}
}
