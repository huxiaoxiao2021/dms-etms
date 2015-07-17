package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午04:28:02
 *
 * POP Rest返回对象
 */
public class PopServicesResponse<T> extends JdResponse {
	private static final long serialVersionUID = 1L;
	
	public static final Integer CODE_IS_ABNORMAL = 2100;
	public static final String MESSAGE_IS_ABNORMAL = "已通过差异反馈提交，无法修改";
	
	public static final Integer CODE_IS_RECEIVE = 2200;
    public static final String MESSAGE_IS_RECEIVE = "订单已收货，无法修改";
	
	private T data;
	
	public PopServicesResponse() {
		
	}

	public PopServicesResponse(Integer code, String message) {
		super(code, message);
	}
	
	public PopServicesResponse(Integer code, String message, T data) {
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
