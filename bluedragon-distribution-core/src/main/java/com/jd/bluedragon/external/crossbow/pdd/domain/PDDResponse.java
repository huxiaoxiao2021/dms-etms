package com.jd.bluedragon.external.crossbow.pdd.domain;

/**
 * <p>
 *     PDD接口的返回值通用对象
 *
 * @author wuzuxiang
 * @since 2019/10/14
 **/
public class PDDResponse<T> {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 结果对象
     */
    private T result;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
