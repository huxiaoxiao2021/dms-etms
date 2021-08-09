package com.jd.bluedragon.distribution.businessIntercept.dto;

import java.io.Serializable;

/**
 * 拦截消息数据给拦截报表
 *
 * @author fanggang7
 * @time 2020-12-10 12:50:56 周四
 */
public class SaveInterceptMsgDto implements Serializable {

    private static final long serialVersionUID = -5266602216259515248L;

    /**
     * 单据号
     */
    private String barCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 操作节点
     */
    private Integer operateNode;

    /**
     * 操作子节点
     */
    private Integer operateSubNode;

    /**
     * 场地ID
     */
    private Integer siteCode;

    /**
     * 场地名称
     */
    private String siteName;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 设备子类型
     */
    private Integer deviceSubType;

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 操作用户code
     */
    private Integer operateUserCode;

    /**
     * 操作用户ERP
     */
    private String operateUserErp;

    /**
     * 操作用户名称
     */
    private String operateUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 拦截编码
     */
    private Integer interceptCode;

    /**
     * 拦截提示语
     */
    private String interceptMessage;

    /**
     * 拦截生效时间
     */
    private Long interceptEffectTime;

    /**
     * 在线状态
     */
    private Integer onlineStatus;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

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

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public Integer getOperateSubNode() {
        return operateSubNode;
    }

    public SaveInterceptMsgDto setOperateSubNode(Integer operateSubNode) {
        this.operateSubNode = operateSubNode;
        return this;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getInterceptCode() {
        return interceptCode;
    }

    public void setInterceptCode(Integer interceptCode) {
        this.interceptCode = interceptCode;
    }

    public String getInterceptMessage() {
        return interceptMessage;
    }

    public void setInterceptMessage(String interceptMessage) {
        this.interceptMessage = interceptMessage;
    }

    public Integer getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(Integer deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public Long getInterceptEffectTime() {
        return interceptEffectTime;
    }

    public void setInterceptEffectTime(Long interceptEffectTime) {
        this.interceptEffectTime = interceptEffectTime;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    @Override
    public String toString() {
        return "SaveInterceptMsgDto{" +
                "barCode='" + barCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                ", operateNode=" + operateNode +
                ", operateSubNode=" + operateSubNode +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", deviceType=" + deviceType +
                ", deviceSubType=" + deviceSubType +
                ", deviceCode='" + deviceCode + '\'' +
                ", operateUserCode=" + operateUserCode +
                ", operateUserErp='" + operateUserErp + '\'' +
                ", operateUserName='" + operateUserName + '\'' +
                ", operateTime=" + operateTime +
                ", interceptCode=" + interceptCode +
                ", interceptMessage='" + interceptMessage + '\'' +
                ", interceptEffectTime=" + interceptEffectTime +
                ", onlineStatus=" + onlineStatus +
                '}';
    }
}
