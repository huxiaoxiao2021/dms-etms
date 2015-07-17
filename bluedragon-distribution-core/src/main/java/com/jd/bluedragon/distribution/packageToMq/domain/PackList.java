package com.jd.bluedragon.distribution.packageToMq.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class PackList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ArrayList<Pack> packList = new ArrayList<Pack>();
	String waybillCode;
	
	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public void addPack(Pack pack){
		packList.add(pack);
	}
	
	public ArrayList<Pack> getPackList(){
		return packList;
	}
}
