package com.jd.bluedragon.distribution.work.domain;

import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 人员签到表-查询条件实体类
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class WorkGridManagerReportQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	/**
	 * 枢纽编码
	 */
	private String areaHubCode;
	/**
	 * 省区编码
	 */
	private String provinceAgencyCode;
	
	/**
	 * 机构编码
	 */
	private Integer orgCode;

	/**
	 * 机构名称
	 */
	private String orgName;

	/**
	 * 场地编码
	 */
	private Integer siteCode;

	/**
	 * 场地名称
	 */
	private String siteName;
	/**
	 * 状态
	 */
	private Integer status;
	/**
	 * 签到日期-开始
	 */
	private String processEndTimeStartStr;
	/**
	 * 签到日期-结束
	 */
	private String processEndTimeEndStr;
	/**
	 * 签到日期-开始
	 */
	private Date processEndTimeStart;
	/**
	 * 签到日期-结束
	 */
	private Date processEndTimeEnd;
	
	/**
	 * 分页-pageSize
	 */
	private Integer pageSize;
	public String getAreaHubCode() {
		return areaHubCode;
	}
	public void setAreaHubCode(String areaHubCode) {
		this.areaHubCode = areaHubCode;
	}
	public String getProvinceAgencyCode() {
		return provinceAgencyCode;
	}
	public void setProvinceAgencyCode(String provinceAgencyCode) {
		this.provinceAgencyCode = provinceAgencyCode;
	}
	public Integer getOrgCode() {
		return orgCode;
	}
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getProcessEndTimeStartStr() {
		return processEndTimeStartStr;
	}
	public void setProcessEndTimeStartStr(String processEndTimeStartStr) {
		this.processEndTimeStartStr = processEndTimeStartStr;
	}
	public String getProcessEndTimeEndStr() {
		return processEndTimeEndStr;
	}
	public void setProcessEndTimeEndStr(String processEndTimeEndStr) {
		this.processEndTimeEndStr = processEndTimeEndStr;
	}
	public Date getProcessEndTimeStart() {
		return processEndTimeStart;
	}
	public void setProcessEndTimeStart(Date processEndTimeStart) {
		this.processEndTimeStart = processEndTimeStart;
	}
	public Date getProcessEndTimeEnd() {
		return processEndTimeEnd;
	}
	public void setProcessEndTimeEnd(Date processEndTimeEnd) {
		this.processEndTimeEnd = processEndTimeEnd;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
