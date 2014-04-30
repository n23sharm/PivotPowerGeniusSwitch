package com.neha.pivotpowergeniusswitch;

import java.io.Serializable;
import java.util.ArrayList;

public class Powerstrip implements Serializable {
	private static final long serialVersionUID = -8603666726333016610L;
	
	public String mId;
	public String mName;
	public ArrayList<Outlet> mOutletList;
	
	public Powerstrip(String id, String name, ArrayList<Outlet> outlets) {
		this.mId = id;
		this.mName = name;
		this.mOutletList = outlets;
	}
	
	public String getId() {
		return mId;
	}
	
	public String getName() {
		return mName;
	}
	
	public ArrayList<Outlet> getOutletList() {
		return mOutletList;
	}
	
	@Override
	public String toString() {
		String text = this.getName() + " [" + this.getId() + "]";
		return text;
	}
}
