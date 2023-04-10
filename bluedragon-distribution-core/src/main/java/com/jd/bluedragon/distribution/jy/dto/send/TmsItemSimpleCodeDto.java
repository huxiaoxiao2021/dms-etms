package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;


public class TmsItemSimpleCodeDto implements Serializable {


    private static final long serialVersionUID = -5595932300580906230L;

    /**
     * 派车明细号
     */
    private String transWorkItemCode;

    /**
     * 始发地编码
     */
    private String beginNodeCode;

    /**
     * 目的地编码
     */
    private String endNodeCode;

    /**
     * 任务简码
     */
    private String simpleCode;

    /**
     * 时间
     */
    private Date operateTime;

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getSimpleCode() {
        return simpleCode;
    }

    public void setSimpleCode(String simpleCode) {
        this.simpleCode = simpleCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
