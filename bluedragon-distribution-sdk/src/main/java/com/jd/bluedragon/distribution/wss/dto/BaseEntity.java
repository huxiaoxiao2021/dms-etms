package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-7 下午04:32:35
 *
 * 接口返回数据基本类
 */
public class BaseEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
    /**
     * 调用接口成功代码
     */
    public static final Integer CODE_SUCCESS = 200;
    /**
     * 调用接口成功
     */
    public static final String MESSAGE_SUCCESS = "调用接口成功，有数据";
    /**
     * 调用接口成功代码
     */
    public static final Integer CODE_SUCCESS_NO = 2200;
    /**
     * 调用接口成功，无数据
     */
    public static final String MESSAGE_SUCCESS_NO = "调用接口成功，无数据";
    /**
     * 传入参数错误代码
     */
    public static final Integer CODE_PARAM_ERROR = 10000;
    /**
     * 传入参数错误
     */
    public static final String MESSAGE_PARAM_ERROR = "传入参数错误";
    /**
     * 传入数组或集合大小超过限制
     */
    public static final Integer CODE_SIZE_ERROR = 12000;
    /**
     * 传入数组或集合大小超过限制错误
     */
    public static final String MESSAGE_SIZE_ERROR = "传入数组或集合大小超过限制";
    /**
     * 调用服务异常代码
     */
    public static final Integer CODE_SERVICE_ERROR = 20000;
    /**
     * 调用服务异常
     */
    public static final String MESSAGE_SERVICE_ERROR = "调用服务异常";

	public BaseEntity() {
		
	}
	
	public BaseEntity(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
	/**
	 * 返回状态码
	 */
	private int code;
	
	/**
	 * 返回消息
	 */
	private String message;
	
	/**
	 * 返回数据
	 */
	private T data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
