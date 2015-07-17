package com.jd.bluedragon.distribution.api.response;

import com.jd.etms.vos.dto.SendCarInfoDto;

import java.util.List;

/**
 * Created by yanghongqiang on 2015/1/12.
 */
public class CarryDeparturePrintResponse {


    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    private List<SendCarInfoDto> data;

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

    public List<SendCarInfoDto> getData() {
        return data;
    }

    public void setData(List<SendCarInfoDto> data) {
        this.data = data;
    }
}
