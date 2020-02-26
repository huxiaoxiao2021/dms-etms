package com.jd.bluedragon.distribution.inspection;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lijie
 * @date 2020/2/19 16:36
 */
public class InsepctionCheckDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 包裹号
     * */
    private String packageCode;

    /**
     * 运单号
     * */
    private String waybillCode;

    /**
     * 包裹总数
     * */
    private Integer packageNum;

    /**
     * 已验包裹数
     * */
    private Integer inspectionedPackNum;

    /**
     * 是否集齐
     * */
    private boolean isGather;

    /**
     * 操作人姓名
     * */
    private String operator;

    /**
     * 操作时间
     * */
    private Date operateTime;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }

    public Integer getInspectionedPackNum() {
        return inspectionedPackNum;
    }

    public void setInspectionedPackNum(Integer inspectionedPackNum) {
        this.inspectionedPackNum = inspectionedPackNum;
    }

    public boolean isGather() {
        return isGather;
    }

    public void setGather(boolean gather) {
        isGather = gather;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
