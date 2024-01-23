package com.jd.bluedragon.distribution.businessIntercept.dto;

import java.io.Serializable;

/**
 * 拦截日志es原始对象
 *
 * @author fanggang7
 * @time 2020-12-08 21:21:08 周二
 */
public class BusinessInterceptDisposeRecord implements Serializable {
    private static final long serialVersionUID = -703624389322325081L;

    private String id;

    /**
     * 业务主键ID  db_column: biz_id
     */
    private String bizId;

    /**
     * 业务数据来源  db_column: biz_source
     */
    private Long bizSource;

    /**
     * 区域ID  db_column: org_id
     */
    private Long orgId;

    /**
     * 区域名称  db_column: org_name
     */
    private String orgName;

    /**
     * 省区
     */
    private String provinceAgencyCode;

    /**
     * 省区名称
     */
    private String provinceAgencyName;

    /**
     * 枢纽
     */
    private String areaHubCode;

    /**
     * 枢纽名称
     */
    private String areaHubName;

    /**
     * 区域名称  db_column: site_code
     */
    private Integer siteCode;

    /**
     * 站点名称  db_column: site_name
     */
    private String siteName;

    /**
     * 单据号（包裹号、运单号、箱号等）  db_column: bar_code
     */
    private String barCode;

    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;

    /**
     * 运单号  db_column: waybill_code
     */
    private String waybillCode;

    /**
     * 设备类型  db_column: device_type
     */
    private Integer deviceType;

    /**
     * 设备编码  db_column: device_code
     */
    private String deviceCode;

    /**
     * 拦截后处理节点  db_column: dispose_node
     */
    private Integer disposeNode;

    /**
     * 拦截后处理节点名称  db_column: dispose_node_name
     */
    private String disposeNodeName;

    /**
     * 拦截后处理人  db_column: dispose_user
     */
    private String disposeUser;

    /**
     * 拦截后处理人  db_column: dispose_user_id
     */
    private Integer disposeUserId;

    /**
     * 拦截后处理人名称  db_column: dispose_user_name
     */
    private String disposeUserName;

    /**
     * 拦截后处理时间
     */
    private Long disposeTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getBizSource() {
        return bizSource;
    }

    public void setBizSource(Long bizSource) {
        this.bizSource = bizSource;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
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

    public Integer getDisposeNode() {
        return disposeNode;
    }

    public void setDisposeNode(Integer disposeNode) {
        this.disposeNode = disposeNode;
    }

    public String getDisposeNodeName() {
        return disposeNodeName;
    }

    public void setDisposeNodeName(String disposeNodeName) {
        this.disposeNodeName = disposeNodeName;
    }

    public String getDisposeUser() {
        return disposeUser;
    }

    public void setDisposeUser(String disposeUser) {
        this.disposeUser = disposeUser;
    }

    public Integer getDisposeUserId() {
        return disposeUserId;
    }

    public void setDisposeUserId(Integer disposeUserId) {
        this.disposeUserId = disposeUserId;
    }

    public String getDisposeUserName() {
        return disposeUserName;
    }

    public void setDisposeUserName(String disposeUserName) {
        this.disposeUserName = disposeUserName;
    }

    public Long getDisposeTime() {
        return disposeTime;
    }

    public void setDisposeTime(Long disposeTime) {
        this.disposeTime = disposeTime;
    }
}
