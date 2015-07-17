package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.List;

public class SummaryPrintResultResponse implements Serializable{

    private static final long serialVersionUID = -8300677209957145264L;
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<SummaryPrintResult> data;
    

    public List<SummaryPrintResult> getData() {
        return data;
    }

    public void setData(List<SummaryPrintResult> data) {
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
