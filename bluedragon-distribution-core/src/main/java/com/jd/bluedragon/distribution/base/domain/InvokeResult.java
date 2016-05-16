package com.jd.bluedragon.distribution.base.domain;

import com.jd.bluedragon.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * Created by wangtingwei on 2014/9/4.
 */
public class InvokeResult<T> implements Serializable {

    private static final Log        logger= LogFactory.getLog(InvokeResult.class);
    public static final int RESULT_NULL_CODE=0;
    public static final String  RESULT_NULL_MESSAGE="结果为空！";

    public static final int RESULT_SUCCESS_CODE=200;
    public static final String RESULT_SUCCESS_MESSAGE="OK";

    public static final int SERVER_ERROR_CODE=500;
    public static final String SERVER_ERROR_MESSAGE="服务器执行异常";

    public static final int RESULT_PARAMETER_ERROR_CODE=400;
    public static final String PARAM_ERROR = "参数错误";

    public static final int RESULT_MULTI_ERROR=600;
    public static final String MULTI_ERROR = "数据已存在";

    public InvokeResult(){
        this.code=RESULT_SUCCESS_CODE;
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
        this.code=RESULT_SUCCESS_CODE;
        this.message=RESULT_SUCCESS_MESSAGE;
    }

    /**
     * 发生异常
     * @param ex
     */
    public void error(Throwable ex){
        this.code=SERVER_ERROR_CODE;
        this.message= SERVER_ERROR_MESSAGE;
        //logger.error(ex);
    }


    /**
     * 发生异常
     * @param message
     */
    public void error(String message){
        this.code=SERVER_ERROR_CODE;
        this.message= message;
        //logger.error(ex);
    }

    /**
     * 参数错误
     * @param message
     */
    public void parameterError(String message){
        this.code=RESULT_PARAMETER_ERROR_CODE;
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
