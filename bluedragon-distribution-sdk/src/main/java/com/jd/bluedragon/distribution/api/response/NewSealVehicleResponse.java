package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

public class NewSealVehicleResponse<T> extends JdResponse {

	private static final long serialVersionUID = -9096768337471427500L;

	public static final Integer CODE_EXCUTE_ERROR = 10000;
	public static final String MESSAGE_EXCUTE_ERROR = "推送运输数据失败！";
	public static final String MESSAGE_QUERY_ERROR = "获取待解封车数据失败！";
	public static final String MESSAGE_SEAL_SUCCESS = "封车成功！";
	public static final String MESSAGE_UNSEAL_SUCCESS = "解封车成功！";

	public static final String TIPS_BATCHCODE_PARAM_ERROR = "请输入正确的批次号!";
	public static final String TIPS_BATCHCODE_PARAM_NOTEXSITE_ERROR = "该批次号没有发货信息，无法操作封车!";
	public static final String TIPS_BATCHCODE_SEALED_ERROR = "该发货批次号已操作封车，无法重复操作!";
	public static final String TIPS_RECEIVESITE_DIFF_ERROR = "批次号与运力编码目的地不一致，请核准后重新操作!";

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
