package com.jd.bluedragon.distribution.sorting.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

public class OrderDetailEntityResponse extends JdResponse {
	
	private static final long serialVersionUID = 6782494991811332161L;
	
	private List<OrdersDetailEntity> data;

	public OrderDetailEntityResponse() {
	}

	public OrderDetailEntityResponse(Integer code, String message) {
		super(code, message);
	}

	public OrderDetailEntityResponse(Integer code, String message, List<OrdersDetailEntity> data) {
		super(code, message);
		this.data = data;
	}

	public List<OrdersDetailEntity> getData() {
		return this.data;
	}

	public void setData(List<OrdersDetailEntity> data) {
		this.data = data;
	}
}
