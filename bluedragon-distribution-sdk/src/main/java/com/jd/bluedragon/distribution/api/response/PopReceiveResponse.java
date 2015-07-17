package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午04:28:02
 *
 * POP托寄收货Rest返回对象
 */
public class PopReceiveResponse extends JdResponse {
	private static final long serialVersionUID = 1L;
	
	public static final Integer CODE_OK_NULL = 2200;
	
	public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
	
	public static final Integer CODE_IS_REPEAT = 2300;
	
	public static final String MESSAGE_IS_REPEAT = "调用服务成功， 此订单与三方运单已经收货，是否确认收货？";
	
	public static final Integer CODE_OK_REPEAT = 2500;
	
	public static final String MESSAGE_OK_REPEAT = "调用服务成功，数据重复";
	
	public PopReceiveResponse() {
		
	}

	public PopReceiveResponse(Integer code, String message) {
		super(code, message);
	}
}
