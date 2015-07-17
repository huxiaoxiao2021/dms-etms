package com.jd.bluedragon.distribution.send.domain;

import java.io.Serializable;

public class OrdersEntity implements Serializable{
	
	private static final long serialVersionUID = 4831337335421636691L;

	/** 包裹号 */
	private String packNo;
	
	public String getPackNo() {
		return packNo;
	}

	public void setPackNo(String packNo) {
		this.packNo = packNo;
	}
}
