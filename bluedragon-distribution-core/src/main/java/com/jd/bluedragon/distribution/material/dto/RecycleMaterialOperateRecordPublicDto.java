package com.jd.bluedragon.distribution.material.dto;

import java.io.Serializable;

/**
 * 操作记录对象，发出mq
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-02-23 15:32:44 周五
 */
public class RecycleMaterialOperateRecordPublicDto implements Serializable {
    private static final long serialVersionUID = -777457122964711741L;

    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 物资类型
     */
    private String materialType;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 订单号
     */
    private String orderCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 操作节点编码
     */
    private Integer operateNodeCode;

    /**
     * 操作节点名称
     */
    private String operateNodeName;

    /**
     * 目的场地ID
     */
    private String receiveSiteId;

    /**
     * 目的场地名称
     */
    private String receiveSiteName;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作场地ID
     */
    private String operateSiteId;

    /**
     * 操作场地名称
     */
    private String operateSiteName;

    /**
     * 操作时间，毫秒级时间戳
     */
    private Long operateTime;

    /**
     * 系统实际发消息时间
     */
    private Long sendTime;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOperateNodeCode() {
        return operateNodeCode;
    }

    public void setOperateNodeCode(Integer operateNodeCode) {
        this.operateNodeCode = operateNodeCode;
    }

    public String getOperateNodeName() {
        return operateNodeName;
    }

    public void setOperateNodeName(String operateNodeName) {
        this.operateNodeName = operateNodeName;
    }

    public String getReceiveSiteId() {
        return receiveSiteId;
    }

    public void setReceiveSiteId(String receiveSiteId) {
        this.receiveSiteId = receiveSiteId;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(String operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }
}
