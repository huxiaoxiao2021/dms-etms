package com.jd.bluedragon.distribution.base;

import com.jd.bluedragon.distribution.constants.ServiceMessageEnum;

import java.io.Serializable;

/**
 * @ClassName ServiceResult
 * @Description
 * @Author wyh
 * @Date 2021/1/4 16:04
 **/
public class ServiceResult<T> implements Serializable {

    private static final long serialVersionUID = 6900545658811516638L;

    /**
     * 请求成功失败标识；true：成功；false：失败
     */
    private Boolean success;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 状态描述
     */
    private String message;

    /**
     * 请求数据
     */
    private T data;

    /**
     * 默认请求成功
     */
    public ServiceResult() {
        this.success = Boolean.TRUE;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

    /**
     * 请求成功
     * @return
     */
    public Boolean retSuccess() {
        return Boolean.TRUE.equals(this.success);
    }

    /**
     * 请求失败
     * @return
     */
    public Boolean retFail() {
        return !retSuccess();
    }

    /**
     *
     */
    public void toFail() {
        this.success = Boolean.FALSE;
    }

    /**
     * 参数错误
     */
    public void toParamError() {
        this.toFail();
        this.code = ServiceMessageEnum.CODE_PARAM_ERROR.getCode();
        this.message = ServiceMessageEnum.CODE_PARAM_ERROR.getMessage();
    }

    /**
     * 服务器异常
     */
    public void toSystemError() {
        this.toFail();
        this.code = ServiceMessageEnum.CODE_FAIL.getCode();
        this.message = ServiceMessageEnum.CODE_FAIL.getMessage();
    }

    /**
     * 自定义失败
     */
    public void customFail(Integer code, String message) {
        this.toFail();
        this.code = code;
        this.message = message;
    }
}
