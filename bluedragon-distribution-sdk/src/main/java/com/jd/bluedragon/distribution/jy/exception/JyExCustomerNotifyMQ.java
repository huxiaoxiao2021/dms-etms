package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * 拣运异常-客服回传MQ实体
 *
 * @author hujiping
 * @date 2023/3/16 10:36 AM
 */
public class JyExCustomerNotifyMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务唯一标识
     */
    private String businessId;

    /**
     * 异常类型
     */
    private Integer exceptionType;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 客户回传状态
     */
    private Integer notifyStatus;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Integer getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(Integer exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(Integer notifyStatus) {
        this.notifyStatus = notifyStatus;
    }
}
