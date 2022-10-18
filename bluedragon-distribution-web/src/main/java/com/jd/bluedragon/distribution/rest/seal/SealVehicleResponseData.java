package com.jd.bluedragon.distribution.rest.seal;

import java.io.Serializable;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/18 11:16
 * @Description:
 */
public class SealVehicleResponseData implements Serializable {

    private Integer code;

    private String message;

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
}
