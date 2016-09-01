package com.jd.bluedragon.distribution.base.domain;

import java.util.ArrayList;

public class VtsBaseSetConfig {
	
	//分拣退货类型等
	private ArrayList<Integer> erroral;
	
	//运力编码需求相关类型
	//(1011,2,1011) //线路类型：运输类型
	//(1004,2,1004) //运力类型：承运商类型
	//(1001,2,1001) //运输方式 :运输方式
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
