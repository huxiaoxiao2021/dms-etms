package com.jd.bluedragon.distribution.record.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 无滑道包裹明细<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 */
public class WaybillHasnoPresiteRecord implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8782028922481196425L;
	/**
	 * 全局唯一ID
	 */
	private Long id;

	/**
	 * 运单号
	 */
	private String waybillCode;

	/**
	 * 场地编号
	 */
	private Integer siteCode;

	/**
	 * 场地名称
	 */
	private String siteName;

	/**
	 * 目的分拣编号
	 */
	private Integer endDmsId;

	/**
	 * 状态
	 */
	private Integer status;

	/**
	 * 外呼状态
	 */
	private Integer callStatus;

	/**
	 * 卡片受理人erp
	 */
	private String dealCardUserErp;

	/**
	 * 卡片受理人名称
	 */
	private String dealCardUserName;

	/**
	 * 验货时间
	 */
	private Date checkTime;

	/**
	 * 外呼时间
	 */
	private Date callTime;

	/**
	 * 卡片受理时间
	 */
	private Date dealCardTime;

	/**
	 * 换单/弃货时间
	 */
	private Date finishTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 删除标识
	 */
	private Integer isDelete;

	/**
	 * 数据库时间
	 */
	private Date ts;

	/**
	 *
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 *
	 * @return id
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 *
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 *
	 * @return waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 *
	 * @param siteCode
	 */
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 *
	 * @return siteCode
	 */
	public Integer getSiteCode() {
		return this.siteCode;
	}

	/**
	 *
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 *
	 * @return siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 *
	 * @param endDmsId
	 */
	public void setEndDmsId(Integer endDmsId) {
		this.endDmsId = endDmsId;
	}

	/**
	 *
	 * @return endDmsId
	 */
	public Integer getEndDmsId() {
		return this.endDmsId;
	}

	/**
	 *
	 * @param status
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 *
	 * @return status
	 */
	public Integer getStatus() {
		return this.status;
	}

	/**
	 *
	 * @param callStatus
	 */
	public void setCallStatus(Integer callStatus) {
		this.callStatus = callStatus;
	}

	/**
	 *
	 * @return callStatus
	 */
	public Integer getCallStatus() {
		return this.callStatus;
	}

	/**
	 *
	 * @param dealCardUserErp
	 */
	public void setDealCardUserErp(String dealCardUserErp) {
		this.dealCardUserErp = dealCardUserErp;
	}

	/**
	 *
	 * @return dealCardUserErp
	 */
	public String getDealCardUserErp() {
		return this.dealCardUserErp;
	}

	/**
	 *
	 * @param dealCardUserName
	 */
	public void setDealCardUserName(String dealCardUserName) {
		this.dealCardUserName = dealCardUserName;
	}

	/**
	 *
	 * @return dealCardUserName
	 */
	public String getDealCardUserName() {
		return this.dealCardUserName;
	}

	/**
	 *
	 * @param checkTime
	 */
	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	/**
	 *
	 * @return checkTime
	 */
	public Date getCheckTime() {
		return this.checkTime;
	}

	/**
	 *
	 * @param callTime
	 */
	public void setCallTime(Date callTime) {
		this.callTime = callTime;
	}

	/**
	 *
	 * @return callTime
	 */
	public Date getCallTime() {
		return this.callTime;
	}

	/**
	 *
	 * @param dealCardTime
	 */
	public void setDealCardTime(Date dealCardTime) {
		this.dealCardTime = dealCardTime;
	}

	/**
	 *
	 * @return dealCardTime
	 */
	public Date getDealCardTime() {
		return this.dealCardTime;
	}

	/**
	 *
	 * @param finishTime
	 */
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 *
	 * @return finishTime
	 */
	public Date getFinishTime() {
		return this.finishTime;
	}

	/**
	 *
	 * @param createTime
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 *
	 * @return createTime
	 */
	public Date getCreateTime() {
		return this.createTime;
	}

	/**
	 *
	 * @param updateTime
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 *
	 * @return updateTime
	 */
	public Date getUpdateTime() {
		return this.updateTime;
	}

	/**
	 *
	 * @param isDelete
	 */
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 *
	 * @return isDelete
	 */
	public Integer getIsDelete() {
		return this.isDelete;
	}

	/**
	 *
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 *
	 * @return ts
	 */
	public Date getTs() {
		return this.ts;
	}
}
