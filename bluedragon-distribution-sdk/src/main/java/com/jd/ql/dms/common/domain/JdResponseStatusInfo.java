package com.jd.ql.dms.common.domain;

import java.io.Serializable;

/**
 * Created by xumei3 on 2018/3/27.
 */
public class JdResponseStatusInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer statusCode;

    /**
     * 状态信息
     */
    private String statusMessage;

    public JdResponseStatusInfo(Integer code, String message){
        this.statusCode = code;
        this.statusMessage = message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
