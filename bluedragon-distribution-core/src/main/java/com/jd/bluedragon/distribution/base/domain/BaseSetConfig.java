package com.jd.bluedragon.distribution.base.domain;

import java.util.ArrayList;

public class BaseSetConfig {
	
	//分拣退货类型等
	private ArrayList<Integer> erroral;
	
	//运力编码需求相关类型
	//(7030,2,7030) //线路类型
	//(7031,2,7031) //运力类型
	//(7032,2,7032) //运输方式 
	private ArrayList<Integer> capacityType;
	
	private Integer sitetype;

	public ArrayList<Integer> getErroral() {
		return erroral;
	}

	public void setErroral(ArrayList<Integer> erroral) {
		this.erroral = erroral;
	}

	public Integer getSitetype() {
		return sitetype;
	}

	public void setSitetype(Integer sitetype) {
		this.sitetype = sitetype;
	}

	public ArrayList<Integer> getCapacityType() {
		return capacityType;
	}

	public void setCapacityType(ArrayList<Integer> capacityType) {
		this.capacityType = capacityType;
	}
	
	
}
