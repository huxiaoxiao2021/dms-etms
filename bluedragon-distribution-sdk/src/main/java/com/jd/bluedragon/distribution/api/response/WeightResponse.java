package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class WeightResponse extends JdResponse {

	private static final long serialVersionUID = -657844061003111624L;

	public static final Integer WEIGHT_TRACK_OK = 1;
	
	public WeightResponse() {
		super();
	}

	public WeightResponse(Integer code, String message) {
		super(code, message);
	}

	/** 返回数据 */
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
