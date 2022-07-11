package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TransWorkItemDto
 * @Description
 * @Author wyh
 * @Date 2022/6/3 17:36
 **/
public class TransWorkItemDto implements Serializable {

    private static final long serialVersionUID = 8922794886580267164L;

    /**
     * 派车单号
     */
    private String transWorkCode;

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
     * 运输类型
     */
    private Integer transType;

    /**
     * 运输方式
     */
    private Integer transWay;

    /**
     * 预计发车时间
     */
    private Date planDepartTime;

    /**
     * 操作类型  10、创建，20、作废
     */
    private Integer operateType;

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

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

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Integer getTransWay() {
        return transWay;
    }

    public void setTransWay(Integer transWay) {
        this.transWay = transWay;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }
}
