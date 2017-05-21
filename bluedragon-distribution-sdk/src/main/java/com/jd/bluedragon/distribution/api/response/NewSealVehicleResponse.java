package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class NewSealVehicleResponse<T> extends JdResponse {

	private static final long serialVersionUID = -9096768337471427500L;

	public static final Integer CODE_EXCUTE_ERROR = 10000;
	public static final String MESSAGE_EXCUTE_ERROR = "推送运输数据异常！";
	public static final String MESSAGE_QUERY_ERROR = "获取待解封车数据异常！";

	public NewSealVehicleResponse(){

	}

	public NewSealVehicleResponse(Integer code, String message){
		super(code, message);
	}

	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
