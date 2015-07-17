package com.jd.bluedragon.distribution.api.request;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

public class ExpressResponse extends JdResponse{

	private static final long serialVersionUID = 5531259448432186200L;
	public List data;
	public List getData() {
		return data;
	}
	public void setData(List data) {
		this.data = data;
	}
	

}
