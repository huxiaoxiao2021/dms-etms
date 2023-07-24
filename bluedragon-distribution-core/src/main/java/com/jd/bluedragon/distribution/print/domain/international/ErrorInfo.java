package com.jd.bluedragon.distribution.print.domain.international;

import java.io.Serializable;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 4:01 PM
 */
public class ErrorInfo implements Serializable {

    /**
     * 业务单号
     */
    private String orderNumber;

    /**
     * 错误码（与当前render接口一致即可）
     */
    private String code;

    /**
     * 错误原因
     */
    private String messag;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessag() {
        return messag;
    }

    public void setMessag(String messag) {
        this.messag = messag;
    }
}
