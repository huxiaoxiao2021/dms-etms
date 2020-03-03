package com.jd.bluedragon.distribution.cyclebox.domain;

import java.util.Date;
import java.util.List;

public class BoxMaterialRelationMQ {

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 与物资编码绑定的箱号
     */
    private String boxCode;

    /**
     * 运单号列表
     */
    private List<String> waybillCode;

    /**
     * 包裹号列表
     */
    private List<String> packageCode;

    /**
     * 业务标识
     * @see com.jd.bluedragon.distribution.cyclebox.domain.BoxMaterialRelationEnum
     *  1： 发货（对应 绑定）
     *  2： 取消发货（对应 解绑）
     *  3： 验货 (对应 解绑)
     *  4： 站点发货 (对应 绑定)
     */
    private int businessType;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作人编码
     */
    private int operatorCode;

    /**
     * 操作时间
     */
    private Date operatorTime;

    /**
     * 操作人所属站点编码
     */
    private String siteCode;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public List<String> getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(List<String> waybillCode) {
        this.waybillCode = waybillCode;
    }

    public List<String> getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(List<String> packageCode) {
        this.packageCode = packageCode;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public int getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(int operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }
}
