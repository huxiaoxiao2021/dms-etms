package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2014/9/4.
 */
public class InvokeResult<T> implements Serializable {

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
    public void error(Exception ex){
        this.code=500;
        this.message=ex.getMessage();
    }

    /**
     * 参数错误
     * @param message
     */
    public void parameterError(String message){
        this.code=400;
        this.message=message;
    }
}
