package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-10-19 下午04:28:02
 *
 * POP交接清单Rest返回对象
 */
public class PopJoinResponse<T> extends JdResponse {
	private static final long serialVersionUID = 1L;
	
	public static final Integer CODE_OK_NULL = 2200;
	
	public static final String MESSAGE_OK_NULL = "调用服务成功，数据为空";
	
	private T data;
	
	public PopJoinResponse() {
		
	}

	public PopJoinResponse(Integer code, String message) {
		super(code, message);
	}
	
	public PopJoinResponse(Integer code, String message, T data) {
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
