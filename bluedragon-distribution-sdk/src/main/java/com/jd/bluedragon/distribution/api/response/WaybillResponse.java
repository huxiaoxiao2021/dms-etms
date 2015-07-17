package com.jd.bluedragon.distribution.api.response;

import java.util.List;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * 通过运单号获得运单地址以及包裹号
 * @author wangzichen
 *
 */
public class WaybillResponse<T> extends JdResponse{

	private static final long serialVersionUID = 1343639712974467228L;

	public static final Integer CODE_NO_DATA = 1001;
	public static final String MESSAGE_NO_DATA = "没有数据";

	public static final Integer CODE_NO_BOX = 10002;
	public static final String MESSAGE_NO_BOX = "没有箱号";
	
	public static final Integer CODE_NO_WAYBILL = 1002;
	public static final String MESSAGE_NO_WAYBILL="没有运单信息";
	
	public static final Integer CODE_NO_RECEIVE_SITE = 10003;
	public static final String MESSAGE_NO_RECEIVE_SITE="箱号或者接收站点不正确";
	
	private T data;

	public WaybillResponse(Integer code, String message) {
		super(code, message);
		// TODO Auto-generated constructor stub
	}
	
	public WaybillResponse(Integer code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public WaybillResponse() {
		super();
	}
	
	private List<PackageResponse> packages;

	public List<PackageResponse> getPackages() {
		return packages;
	}

	public void setPackages(List<PackageResponse> packages) {
		this.packages = packages;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
