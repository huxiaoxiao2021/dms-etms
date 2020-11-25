package com.jd.bluedragon.distribution.funcSwitchConfig.domain;

import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;

import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/11/25 14:39
 */
public class FuncSwitchConfigResponse<T> {

    private static final long serialVersionUID = 1L;

    /** 响应状态码 */
    protected Integer code;

    /** 响应消息 */
    protected String message;

    /** 响应数据 */
    protected List<DmsFuncSwitchDto> data;

    public FuncSwitchConfigResponse() {
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

    public List<DmsFuncSwitchDto> getData() {
        return data;
    }

    public void setData(List<DmsFuncSwitchDto> data) {
        this.data = data;
    }
}
    
