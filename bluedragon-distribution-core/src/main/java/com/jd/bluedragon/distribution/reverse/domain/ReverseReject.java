package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

public class ReverseReject {
    
    public static final Integer BUSINESS_TYPE_WMS = 1;
    public static final Integer BUSINESS_TYPE_AMS = 2;
    public static final Integer BUSINESS_TYPE_SPWMS = 3;
    
    /** 自增主键 */
    private Long id;
    
    /** 创建站点编号 */
    private Integer createSiteCode;
    
    /** 创建站点名称 */
    private String createSiteName;
    
    /** 机构ID */
    private Integer orgId;
    
    /** 配送中心标识 */
    private Integer cky2;
    
    /** 库房ID */
    private Integer storeId;
    
    /** 订单编号 */
    private String orderId;
    
    /** 取件单号 */
    private String pickwareCode;
    
    /** 包裹编号 */
    private String packageCode;
    
    /** 操作人编号 */
    private String operatorCode;
    
    /** 操作人 */
    private String operator;
    
    /** 操作时间 */
    private Date operateTime;
    
    /** 业务类型 */
    private Integer businessType;
    
    /** 验货人 */
    private String inspector;
    
    /** 验货人编号 */
    private String inspectorCode;
    
    /** 验货时间 */
    private Date inspectTime;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 最有一次修改时间 */
    private Date updateTime;
    
    /** 是否删除 */
    private Integer yn;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }
    
    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
    
    public String getCreateSiteName() {
        return this.createSiteName;
    }
    
    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }
    
    public Integer getOrgId() {
        return this.orgId;
    }
    
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
    
    public Integer getCky2() {
        return this.cky2;
    }
    
    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }
    
    public Integer getStoreId() {
        return this.storeId;
    }
    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getPickwareCode() {
        return this.pickwareCode;
    }
    
    public void setPickwareCode(String pickwareCode) {
        this.pickwareCode = pickwareCode;
    }
    
    public String getPackageCode() {
        return this.packageCode;
    }
    
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
    
    public String getOperatorCode() {
        return this.operatorCode;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public Date getOperateTime() {
    	return operateTime!=null?(Date)operateTime.clone():null;
    }
    
    public void setOperateTime(Date operateTime) {
    	this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
    }
    
    public Integer getBusinessType() {
        return this.businessType;
    }
    
    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }
    
    public String getInspector() {
        return this.inspector;
    }
    
    public void setInspector(String inspector) {
        this.inspector = inspector;
    }
    
    public String getInspectorCode() {
        return this.inspectorCode;
    }
    
    public void setInspectorCode(String inspectorCode) {
        this.inspectorCode = inspectorCode;
    }
    
    public Date getInspectTime() {
    	return inspectTime!=null?(Date)inspectTime.clone():null;
    }
    
    public void setInspectTime(Date inspectTime) {
    	this.inspectTime = inspectTime!=null?(Date)inspectTime.clone():null;
    }
    
    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }
    
    public void setCreateTime(Date createTime) {
    	this.createTime = createTime!=null?(Date)createTime.clone():null;
    }
    
    public Date getUpdateTime() {
    	return updateTime!=null?(Date)updateTime.clone():null;
    }
    
    public void setUpdateTime(Date updateTime) {
    	this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
    }
    
    public Integer getYn() {
        return this.yn;
    }
    
    public void setYn(Integer yn) {
        this.yn = yn;
    }
    
    public String toString() {
        return "ReverseReject [id=" + this.id + ", createSiteCode=" + this.createSiteCode
                + ", createSiteName=" + this.createSiteName + ", orgId=" + this.orgId + ", cky2="
                + this.cky2 + ", storeId=" + this.storeId + ", orderId=" + this.orderId
                + ", pickwareCode=" + this.pickwareCode + ", packageCode=" + this.packageCode
                + ", operatorCode=" + this.operatorCode + ", operator=" + this.operator
                + ", operateTime=" + this.operateTime + ", businessType=" + this.businessType
                + ", inspector=" + this.inspector + ", inspectorCode=" + this.inspectorCode
                + ", inspectTime=" + this.inspectTime + "]";
    }
    
}
