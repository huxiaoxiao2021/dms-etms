package com.jd.bluedragon.distribution.coldChain.enums;

/**
 * @author : caozhixing3
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.distribution.coldChain.enums
 * @Description:
 * @date Date : 2022年09月29日 11:05
 */
public enum  ColdSendResultCodeNum {

    SUCCESS(1),
    /**
     * 拦截
     */
    INTERCEPT(2),
    /**
     * 提示
     */
    PROMPT(3),
    /**
     * 确认
     */
    CONFIRM(4);

    ColdSendResultCodeNum(Integer code) {
        this.code = code;
    }

    /**
     * 状态码
     */
    private Integer code;

    public Integer getCode() {
        return code;
    }
}
