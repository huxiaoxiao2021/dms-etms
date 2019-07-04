package com.jd.bluedragon.common.dto.base.response;

import java.io.Serializable;

/**
 * 只用于后台服务与android 相关业务交互使用
 * android 端使用gradle 依赖
 * @ClassName: JdCResponse
 * @Description: 基础返回结果
 * @author wuyoude
 * @date 2017年6月1日 下午6:03:02
 *
 */
public class JdCResponse<E> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public static final Integer CODE_SUCCESS = 200;
    public static final String MESSAGE_SUCCESS = "success";
    
    public static final Integer CODE_FAIL = 400;
    public static final String MESSAGE_FAIL = "fail";

    public static final Integer CODE_ERROR = 500;
    public static final String MESSAGE_ERROR = "error";

	public static final Integer CODE_CONFIRM = 30001;
	public static final String  MESSAGE_CONFIRM = "confirm";

	/**
	 * 部分成功 交易码
	 */
	public static final Integer CODE_PARTIAL_SUCCESS = 600;
	public static final String  MESSAGE_PARTIAL_SUCCESS = "partial success";

    /** 响应状态码 */
    protected Integer code;
    
    /** 响应消息 */
    protected String message;
    
    /** 响应数据 */
    protected E data;
    /**
     * 构造方法，默认为成功
     */
    public JdCResponse() {
    	super();
    }
    /**
     * 指定code和message的构造方法
     * @param code
     * @param message
     */
    public JdCResponse(Integer code, String message) {
        super();
        this.init(code, message);
    }
    /**
     * 指定code、message、data的构造方法
     * @param code
     * @param message
     * @param data
     */
	public JdCResponse(Integer code, String message, E data) {
		super();
		this.init(code, message, data);
	}
	/**
	 * 初始化方法
	 * @param code
	 */
	public void init(Integer code) {
		this.code = code;
	}
	public void init(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	public void init(Integer code, String message, E data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	/**
	 * 状态判断
	 * @return
	 */
	public boolean isSucceed() {
		return CODE_SUCCESS.equals(this.code);
	}

	public boolean isFail() {
		return CODE_FAIL.equals(this.code);
	}

	public boolean isError() {
		return CODE_ERROR.equals(this.code);
	}
	/**
	 * 状态转换
	 */
	public void toSucceed() {
		init(CODE_SUCCESS);
	}
	/**
	 * 状态转换并设置返回信息
	 * @param message
	 */
	public void toSucceed(String message) {
		init(CODE_SUCCESS,message);
	}
	public void toFail() {
		init(CODE_FAIL);
	}
	public void toFail(String message) {
		init(CODE_FAIL,message);
	}
	public void toError() {
		init(CODE_ERROR);
	}
	public void toError(String message) {
		init(CODE_ERROR,message);
	}

	public void toConfirm(){
		init(CODE_CONFIRM);
	}
	public void toConfirm(String message){
		init(CODE_CONFIRM,message);

	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the data
	 */
	public E getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(E data) {
		this.data = data;
	}
}
