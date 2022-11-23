package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2014/9/4.
 */
public class InvokeResult<T> implements Serializable {

    private static final String     SERVER_ERROR_MESSAGE="服务器执行异常";

    public static final int RESULT_SUCCESS_CODE = 200;
    public static final String RESULT_SUCCESS_MESSAGE = "OK";
    public static final int PARAMETER_ERROR_CODE = 4120;
    public static final String PARAMETER_ERROR_MESSAGE = "请求参数错误";
    public static final int SERVICE_ERROR_CODE = 5000;
    public static final String SERVICE_ERROR_MESSAGE = "服务内部错误";
    public static final int SERVICE_FAIL_CODE = 5001;
    public static final String SERVICE_FAIL_MESSAGE = "服务处理失败";

    public static final int EASY_FROZEN_TIPS_CODE= 340;
    public static final String EASY_FROZEN_TIPS_MESSAGE ="此运单为易冻品!";

    public static final int EASY_FROZEN_TIPS_STORAGE_CODE= 341;
    public static final String EASY_FROZEN_TIPS_STORAGE_MESSAGE ="此运单为易冻品，请放至保温储存区等待发货!";

    public static final int LUXURY_SECURITY_TIPS_CODE= 342;

    public static final String LUXURY_SECURITY_TIPS_MESSAGE ="此运单为特保单，请对包裹进行拍照!";

    public InvokeResult(){
        this.code=200;
    }
    /**
     * 状态码
     */
    private int code;

    /**
     * 状态描述
     */
    private String message;

    /**
     * 执行结果
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

    public void success() {
        this.code = RESULT_SUCCESS_CODE;
        this.message = RESULT_SUCCESS_MESSAGE;
    }

    /**
     * 发生异常
     * @param ex
     */
    public void error(Throwable ex){
        this.code=500;
        this.message= SERVER_ERROR_MESSAGE;
    }

    /**
     * 参数错误
     * @param message
     */
    public void parameterError(String message){
        this.code=400;
        this.message=message;
    }

    /**
     * 设置用户自定义消息
     * @param code      消息代号
     * @param message   消息内容
     */
    public void customMessage(int code,String message){
        this.code=code;
        this.message=message;
    }

    /**
     * 成功
     */
    public boolean codeSuccess() {
        return this.code == RESULT_SUCCESS_CODE;
    }
}
