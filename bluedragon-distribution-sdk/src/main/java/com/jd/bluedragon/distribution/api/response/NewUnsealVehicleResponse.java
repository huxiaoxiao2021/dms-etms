package com.jd.bluedragon.distribution.api.response;

import java.io.Serializable;

/**
 * 解封车结果集
 *
 * @author hujiping
 * @date 2021/9/22 10:33 上午
 */
public class NewUnsealVehicleResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 解封车提示抽检术语
     */
    public static final Integer SPOT_CHECK_UNSEAL_HINT_CODE = 20001;
    public static final String SPOT_CHECK_UNSEAL_HINT_MESSAGE = "该任务为%s任务，解封车时需要进行整车抽检，烦请称重量方抽检，谢谢！";

    private Integer code;

    private String message;

    private Integer businessCode;

    private String businessMessage;

    private T data;

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

    public Integer getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(Integer businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessMessage() {
        return businessMessage;
    }

    public void setBusinessMessage(String businessMessage) {
        this.businessMessage = businessMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
