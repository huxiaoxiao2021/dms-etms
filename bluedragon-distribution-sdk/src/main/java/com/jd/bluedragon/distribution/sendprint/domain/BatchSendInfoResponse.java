package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.List;

public class BatchSendInfoResponse implements Serializable{

    private static final long serialVersionUID = -8924312841267162505L;
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<BatchSendResult> data;
    

    public List<BatchSendResult> getData() {
        return data;
    }

    public void setData(List<BatchSendResult> data) {
        this.data = data;
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
}
