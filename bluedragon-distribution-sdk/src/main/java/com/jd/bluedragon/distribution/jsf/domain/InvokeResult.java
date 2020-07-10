package com.jd.bluedragon.distribution.jsf.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2014/9/4.
 */
public class InvokeResult<T> implements Serializable {

    private static final String     SERVER_ERROR_MESSAGE="服务器执行异常";
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

    public void success(){
        this.code=200;
        this.message="OK";
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
}
