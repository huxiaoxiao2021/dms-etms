package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午04:28:02
 *
 * 备件条码返回对象
 */
public class ReverseSpareResponse<T> extends JdResponse {
	private static final long serialVersionUID = 1L;
	
	public static final Integer CODE_OK_NULL = 2200;
	
	public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
	
	public static final String MESSAGE_OK_NULL_SORTING = "无分拣数据";
	
	private T data;
	
	public ReverseSpareResponse() {
		
	}

	public ReverseSpareResponse(Integer code, String message) {
		super(code, message);
	}
	
	public ReverseSpareResponse(Integer code, String message, T data) {
		super(code, message);
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
