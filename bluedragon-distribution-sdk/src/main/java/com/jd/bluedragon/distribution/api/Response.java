package com.jd.bluedragon.distribution.api;

import java.io.Serializable;

/**
 * 对外接口交互 统一返回参数包装对象
 * @param <T>
 */
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int CODE_SUCCESS = 1;
    public static final String MESSAGE_SUCCESS = "success";

    public static final int CODE_NONE = -1;
    public static final String MESSAGE_NONE = "结果为空";

    public static final int CODE_WARN = -2;
    public static final String MESSAGE_WARN = "参数非法";

    public static final int CODE_ERROR = -3;
    public static final String MESSAGE_ERROR = "error";


    /** 响应状态码 */
    protected Integer code;

    /** 响应消息 */
    protected String message;

    /** 响应数据 */
    protected T data;
    /**
     * 构造方法，默认为成功
     */
    public Response() {
    	super();
    }
    /**
     * 指定code和message的构造方法
     * @param code
     * @param message
     */
    public Response(Integer code, String message) {
        super();
        this.init(code, message);
    }
    /**
     * 指定code、message、data的构造方法
     * @param code
     * @param message
     * @param data
     */
	public Response(Integer code, String message, T data) {
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
	public void init(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	/**
	 * 状态判断
	 * @return
	 */
	public boolean isSucceed() {
		return CODE_SUCCESS == this.code;
	}

    public boolean isNone() {
        return CODE_NONE == this.code;
    }

	public boolean isFail() {
		return CODE_WARN == this.code;
	}

	public boolean isError() {
		return CODE_ERROR == this.code;
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
    public void toNone() {
        init(CODE_NONE);
    }
    public void toNone(String message) {
        init(CODE_NONE,message);
    }
	public void toWarn() {
		init(CODE_WARN);
	}
	public void toWarn(String message) {
		init(CODE_WARN,message);
	}
	public void toError() {
		init(CODE_ERROR);
	}
	public void toError(String message) {
		init(CODE_ERROR,message);
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
	public T getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(T data) {
		this.data = data;
	}
}
