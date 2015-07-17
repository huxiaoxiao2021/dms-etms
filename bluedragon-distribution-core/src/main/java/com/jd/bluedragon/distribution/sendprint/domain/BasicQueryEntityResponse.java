package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;
import java.util.List;

public class BasicQueryEntityResponse implements Serializable{

    private static final long serialVersionUID = 6782494991811332161L;
    
    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;
    
    private List<BasicQueryEntity> data;

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

    public List<BasicQueryEntity> getData() {
        return data;
    }

    public void setData(List<BasicQueryEntity> data) {
        this.data = data;
    }
}
