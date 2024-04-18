package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 换单打印时的包裹纬度打印消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2024-04-18 16:28:03 周四
 */
public class WaybillExchangePrintPackageDto implements Serializable {

    private static final long serialVersionUID = -174036521699740057L;
    /**
     * 原单号
     */
    private String waybillCodeOld;

    /**
     * 新单号
     */
    private String waybillCodeNew;

    /**
     * 老包裹号
     */
    private String packageCodeOld;

    /**
     * 新包裹号
     */
    private String packageCodeNew;

    /**
     * 员工ID
     */
    private Integer operateUserId;

    /**
     * 员工ERP
     */
    private String operateUserErp;

    /**
     * 员工真实姓名
     */
    private String operateUserName;


    /**
     * 操作人站点ID
     */
    private Integer siteId;

    /**
     * 操作人站点名称
     */
    private String siteName;

    /**
     * 业务操作类型
     */
    private Integer operateType;

    /**
     * 操作时间
     */
    private Long operateUnixTime;

    public String getWaybillCodeOld() {
        return waybillCodeOld;
    }

    public void setWaybillCodeOld(String waybillCodeOld) {
        this.waybillCodeOld = waybillCodeOld;
    }

    public String getWaybillCodeNew() {
        return waybillCodeNew;
    }

    public void setWaybillCodeNew(String waybillCodeNew) {
        this.waybillCodeNew = waybillCodeNew;
    }

    public String getPackageCodeOld() {
        return packageCodeOld;
    }

    public void setPackageCodeOld(String packageCodeOld) {
        this.packageCodeOld = packageCodeOld;
    }

    public String getPackageCodeNew() {
        return packageCodeNew;
    }

    public void setPackageCodeNew(String packageCodeNew) {
        this.packageCodeNew = packageCodeNew;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
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

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Long getOperateUnixTime() {
        return operateUnixTime;
    }

    public void setOperateUnixTime(Long operateUnixTime) {
        this.operateUnixTime = operateUnixTime;
    }
}
