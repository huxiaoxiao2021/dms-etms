package com.jd.bluedragon.distribution.businessIntercept.dto;

import java.io.Serializable;

/**
 * 拦截日志报表数据
 *
 * @author fanggang7
 * @time 2020-12-08 21:21:08 周二
 */
public class BusinessInterceptReport implements Serializable {

    private static final long serialVersionUID = -7452994628724154981L;

    private String id;

    /**
     * 业务主键ID  db_column: biz_id
     */
    private String bizId;

    /**
     * 业务数据来源  db_column: biz_source
     */
    private String bizSource;

    /**
     * 区域ID  db_column: org_id
     */
    private Integer orgId;

    /**
     * 区域名称  db_column: org_name
     */
    private String orgName;

    /**
     * 省区域ID  db_column: province_agency_code
     */
    private String provinceAgencyCode;

    /**
     * 省名称  db_column: province_agency_name
     */
    private String provinceAgencyName;

    /**
     * 枢纽ID  db_column: area_hub_code
     */
    private String areaHubCode;

    /**
     * 枢纽名称  db_column: area_hub_name
     */
    private String areaHubName;

    /**
     * 场地ID  db_column: site_code
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
     * 运单总包裹数
     */
    private Integer packageTotal;

    /**
     * 包裹号  db_column: package_code
     */
    private String packageCode;

    /**
     * 运单号  db_column: waybill_code
     */
    private String waybillCode;

    /**
     * 箱号  db_column: box_code
     */
    private String boxCode;

    /**
     * 发货道口号  db_column: cross_code
     */
    private String crossCode;

    /**
     * 操作节点  db_column: operate_node
     */
    private Integer operateNode;

    /**
     * 操作节点名称  db_column: operate_node_name
     */
    private String operateNodeName;

    /**
     * 操作子节点  db_column: operate_sub_node
     */
    private Integer operateSubNode;

    /**
     * 操作子节点名称  db_column: operate_sub_node_name
     */
    private String operateSubNodeName;

    /**
     * 应拦截生效时间  db_column: intercept_effect_time
     */
    private Long interceptEffectTime;

    /**
     * 拦截类型  db_column: intercept_type
     */
    private Integer interceptType;

    /**
     * 拦截类型  db_column: intercept_type_name
     */
    private String interceptTypeName;

    /**
     * 设备类型  db_column: device_type
     */
    private Integer deviceType;

    /**
     * 设备类型  db_column: device_type_name
     */
    private String deviceTypeName;

    /**
     * 设备子类型  db_column: device_sub_type
     */
    private Integer deviceSubType;

    /**
     * 设备子类型名称  db_column: device_sub_type_name
     */
    private String deviceSubTypeName;

    /**
     * 设备编码  db_column: device_code
     */
    private String deviceCode;

    /**
     * 在线状态  db_column: online_status
     */
    private Integer onlineStatus;

    /**
     * 拦截返回码  db_column: intercept_code
     */
    private Integer interceptCode;

    /**
     * 拦截消息  db_column: intercept_message
     */
    private String interceptMessage;

    /**
     * 操作时间  db_column: operate_time
     */
    private Long operateTime;

    /**
     * 操作人  db_column: operate_user
     */
    private String operateUser;

    /**
     * 操作人名称  db_column: operate_user_name
     */
    private String operateUserName;

    /**
     * 拦截后处理节点  db_column: dispose_node
     */
    private Integer disposeNode;

    /**
     * 拦截后处理节点  db_column: dispose_node_name
     */
    private String disposeNodeName;

    /**
     * 拦截后处理人  db_column: dispose_user
     */
    private String disposeUser;

    /**
     * 拦截后处理人名称  db_column: dispose_user_name
     */
    private String disposeUserName;

    /**
     * 拦截后处理时间  db_column: disposeTime
     */
    private Long disposeTime;

    /**
     * 创建人  db_column: create_user
     */
    private String createUser;

    /**
     * 创建人  db_column: create_user_id
     */
    private String createUserId;

    /**
     * 创建人名称  db_column: create_user_name
     */
    private String createUserName;

    /**
     * 修改人  db_column: update_user
     */
    private String updateUser;

    /**
     * 修改人用户ID  db_column: update_user_id
     */
    private String updateUserId;

    /**
     * 修改人名称  db_column: update_user_name
     */
    private String updateUserName;

    /**
     * 创建时间  db_column: create_time
     */
    private Long createTime;

    /**
     * 修改时间  db_column: update_time
     */
    private Long updateTime;

    /**
     * 有效标志  db_column: update_time
     */
    private Integer yn;

    /**
     * 数据库时间  db_column: ts
     */
    private Long ts;

    /**
     * 运单类型  db_column: waybill_type
     */
    private Integer waybillType;

    /**
     * 运单类型  db_column: waybill_type_name
     */
    private String waybillTypeName;

    /**
     * 商户编码  db_column: customer_code
     */
    private String customerCode;

    /**
     * 商户名称  db_column: customer_code_name
     */
    private String customerName;

    /**
     * 验货时间  db_column: inspection_time
     */
    private Long inspectionTime;

    /**
     * 分拣时间  db_column: sorting_time
     */
    private Long sortingTime;

    /**
     * 发货时间  db_column: send_time
     */
    private Long sendTime;

    /**
     * 实际揽收区域Id  db_column: real_pickup_org_id
     */
    private Long realPickupOrgId;

    /**
     * 实际揽收区域名称  db_column: real_pickup_org_name
     */
    private String realPickupOrgName;

    /**
     * 实际揽收站点ID  db_column: real_pickup_site_id
     */
    private Long realPickupSiteId;

    /**
     * 实际揽收站点名称  db_column: real_pickup_site_name
     */
    private String realPickupSiteName;

    /**
     * 实际揽收人erp  db_column: pickup_user_erp
     */
    private String pickupUserErp;

    /**
     * 实际揽收人姓名  db_column: pickup_user_name
     */
    private String pickupUserName;

    /**
     * 预分拣揽收站点ID  db_column: pickup_site_id
     */
    private Long pickupSiteId;

    /**
     * 预分拣揽收站点名称  db_column: pickup_site_name
     */
    private String pickupSiteName;

    /**
     * 配送区域ID  db_column: delivery_org_id
     */
    private Long deliveryOrgId;

    /**
     * 配送区域名称  db_column: delivery_org_name
     */
    private String deliveryOrgName;

    /**
     * 配送站点ID  db_column: delivery_site_id
     */
    private Long deliverySiteId;

    /**
     * 配送站点名称  db_column: delivery_site_name
     */
    private String deliverySiteName;

    /**
     * 分拣中心一级类型
     */
    private Integer sortType;

    /**
     * 分拣中心二级类型
     */
    private Integer sortSubType;

    /**
     * 分拣中心三级类型
     */
    private Integer sortThirdType;

    /**
     * 操作人所在网格码
     */
    private String operatePositionCode;

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

    public String getBizSource() {
        return bizSource;
    }

    public void setBizSource(String bizSource) {
        this.bizSource = bizSource;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
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

    public Integer getPackageTotal() {
        return packageTotal;
    }

    public void setPackageTotal(Integer packageTotal) {
        this.packageTotal = packageTotal;
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

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public Integer getOperateNode() {
        return operateNode;
    }

    public void setOperateNode(Integer operateNode) {
        this.operateNode = operateNode;
    }

    public String getOperateNodeName() {
        return operateNodeName;
    }

    public void setOperateNodeName(String operateNodeName) {
        this.operateNodeName = operateNodeName;
    }

    public Integer getOperateSubNode() {
        return operateSubNode;
    }

    public void setOperateSubNode(Integer operateSubNode) {
        this.operateSubNode = operateSubNode;
    }

    public String getOperateSubNodeName() {
        return operateSubNodeName;
    }

    public void setOperateSubNodeName(String operateSubNodeName) {
        this.operateSubNodeName = operateSubNodeName;
    }

    public Long getInterceptEffectTime() {
        return interceptEffectTime;
    }

    public void setInterceptEffectTime(Long interceptEffectTime) {
        this.interceptEffectTime = interceptEffectTime;
    }

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

    public String getInterceptTypeName() {
        return interceptTypeName;
    }

    public void setInterceptTypeName(String interceptTypeName) {
        this.interceptTypeName = interceptTypeName;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    public Integer getDeviceSubType() {
        return deviceSubType;
    }

    public void setDeviceSubType(Integer deviceSubType) {
        this.deviceSubType = deviceSubType;
    }

    public String getDeviceSubTypeName() {
        return deviceSubTypeName;
    }

    public void setDeviceSubTypeName(String deviceSubTypeName) {
        this.deviceSubTypeName = deviceSubTypeName;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public Integer getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(Integer onlineStatus) {
        this.onlineStatus = onlineStatus;
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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
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

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
    }

    public String getWaybillTypeName() {
        return waybillTypeName;
    }

    public void setWaybillTypeName(String waybillTypeName) {
        this.waybillTypeName = waybillTypeName;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(Long inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public Long getSortingTime() {
        return sortingTime;
    }

    public void setSortingTime(Long sortingTime) {
        this.sortingTime = sortingTime;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Long getRealPickupOrgId() {
        return realPickupOrgId;
    }

    public void setRealPickupOrgId(Long realPickupOrgId) {
        this.realPickupOrgId = realPickupOrgId;
    }

    public String getRealPickupOrgName() {
        return realPickupOrgName;
    }

    public void setRealPickupOrgName(String realPickupOrgName) {
        this.realPickupOrgName = realPickupOrgName;
    }

    public Long getRealPickupSiteId() {
        return realPickupSiteId;
    }

    public void setRealPickupSiteId(Long realPickupSiteId) {
        this.realPickupSiteId = realPickupSiteId;
    }

    public String getRealPickupSiteName() {
        return realPickupSiteName;
    }

    public void setRealPickupSiteName(String realPickupSiteName) {
        this.realPickupSiteName = realPickupSiteName;
    }

    public String getPickupUserErp() {
        return pickupUserErp;
    }

    public void setPickupUserErp(String pickupUserErp) {
        this.pickupUserErp = pickupUserErp;
    }

    public String getPickupUserName() {
        return pickupUserName;
    }

    public void setPickupUserName(String pickupUserName) {
        this.pickupUserName = pickupUserName;
    }

    public Long getPickupSiteId() {
        return pickupSiteId;
    }

    public void setPickupSiteId(Long pickupSiteId) {
        this.pickupSiteId = pickupSiteId;
    }

    public String getPickupSiteName() {
        return pickupSiteName;
    }

    public void setPickupSiteName(String pickupSiteName) {
        this.pickupSiteName = pickupSiteName;
    }

    public Long getDeliveryOrgId() {
        return deliveryOrgId;
    }

    public void setDeliveryOrgId(Long deliveryOrgId) {
        this.deliveryOrgId = deliveryOrgId;
    }

    public String getDeliveryOrgName() {
        return deliveryOrgName;
    }

    public void setDeliveryOrgName(String deliveryOrgName) {
        this.deliveryOrgName = deliveryOrgName;
    }

    public Long getDeliverySiteId() {
        return deliverySiteId;
    }

    public void setDeliverySiteId(Long deliverySiteId) {
        this.deliverySiteId = deliverySiteId;
    }

    public String getDeliverySiteName() {
        return deliverySiteName;
    }

    public void setDeliverySiteName(String deliverySiteName) {
        this.deliverySiteName = deliverySiteName;
    }

    public Integer getSortType() {
        return sortType;
    }

    public void setSortType(Integer sortType) {
        this.sortType = sortType;
    }

    public Integer getSortSubType() {
        return sortSubType;
    }

    public void setSortSubType(Integer sortSubType) {
        this.sortSubType = sortSubType;
    }

    public Integer getSortThirdType() {
        return sortThirdType;
    }

    public void setSortThirdType(Integer sortThirdType) {
        this.sortThirdType = sortThirdType;
    }

    public String getOperatePositionCode() {
        return operatePositionCode;
    }

    public void setOperatePositionCode(String operatePositionCode) {
        this.operatePositionCode = operatePositionCode;
    }
}
