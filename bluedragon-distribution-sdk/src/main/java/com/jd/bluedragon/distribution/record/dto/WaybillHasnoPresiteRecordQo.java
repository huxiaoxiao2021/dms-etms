package com.jd.bluedragon.distribution.record.dto;

import java.io.Serializable;
import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * Description: 无滑道包裹明细<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 */
public class WaybillHasnoPresiteRecordQo extends BasePagerCondition implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4247677878077785367L;

	/**
     * 运单号
     */
    private String waybillCode;

    /**
     * 区域
     */
    private Integer orgCode;

    /**
     * 分拣中心
     */
    private Integer siteCode;

    /**
     * 时间范围
     */
    private Integer hourRange;
    /**
     * 操作人
     */
    private String currentUserErp;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 异步导出标识
     */
    private Integer isAsyncExport;

    private Integer pageSize;
    
    private Date startTimeTs;
    
    private Date endTimeTs;
    
    private Long startId;
    
    private Date endCreateTime;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public Integer getHourRange() {
		return hourRange;
	}

	public void setHourRange(Integer hourRange) {
		this.hourRange = hourRange;
	}

	public String getCurrentUserErp() {
		return currentUserErp;
	}

	public void setCurrentUserErp(String currentUserErp) {
		this.currentUserErp = currentUserErp;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getIsAsyncExport() {
		return isAsyncExport;
	}

	public void setIsAsyncExport(Integer isAsyncExport) {
		this.isAsyncExport = isAsyncExport;
	}

	public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }

	public Date getStartTimeTs() {
		return startTimeTs;
	}

	public void setStartTimeTs(Date startTimeTs) {
		this.startTimeTs = startTimeTs;
	}

	public Date getEndTimeTs() {
		return endTimeTs;
	}

	public void setEndTimeTs(Date endTimeTs) {
		this.endTimeTs = endTimeTs;
	}

	public Long getStartId() {
		return startId;
	}

	public void setStartId(Long startId) {
		this.startId = startId;
	}

	public Date getEndCreateTime() {
		return endCreateTime;
	}

	public void setEndCreateTime(Date endCreateTime) {
		this.endCreateTime = endCreateTime;
	}

}