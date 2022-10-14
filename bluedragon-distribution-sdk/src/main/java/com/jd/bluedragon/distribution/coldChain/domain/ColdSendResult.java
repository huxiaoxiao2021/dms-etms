package com.jd.bluedragon.distribution.coldChain.domain;

import java.io.Serializable;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.coldChain.domain
 * @Description:
 * @date Date : 2022年09月29日 10:51
 */
public class ColdSendResult implements Serializable {
    private static final long serialVersionUID = 1L;

    public ColdSendResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 提示语
     */
    private String msg;

    //=====================================

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
