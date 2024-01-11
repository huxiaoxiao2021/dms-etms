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
	private String taskDateStartStr;
	/**
	 * 签到日期-结束
	 */
	private String taskDateEndStr;
	/**
	 * 签到日期-开始
	 */
	private Date taskDateStart;
	/**
	 * 签到日期-结束
	 */
	private Date taskDateEnd;

	/**
	 * 分页-pageSize
	 */
	private Integer pageSize;
	//任务业务类型：1-日常巡查 2-管理巡视 3-异常及时检查 4-指标周期改善 5-事件治理整改任务
	private Integer taskBizType;

	private Integer isMatch;

	private Integer transfer;

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

	public String getTaskDateStartStr() {
		return taskDateStartStr;
	}

	public void setTaskDateStartStr(String taskDateStartStr) {
		this.taskDateStartStr = taskDateStartStr;
	}

	public String getTaskDateEndStr() {
		return taskDateEndStr;
	}

	public void setTaskDateEndStr(String taskDateEndStr) {
		this.taskDateEndStr = taskDateEndStr;
	}

	public Date getTaskDateStart() {
		return taskDateStart;
	}

	public void setTaskDateStart(Date taskDateStart) {
		this.taskDateStart = taskDateStart;
	}

	public Date getTaskDateEnd() {
		return taskDateEnd;
	}

	public void setTaskDateEnd(Date taskDateEnd) {
		this.taskDateEnd = taskDateEnd;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getTaskBizType() {
		return taskBizType;
	}

	public void setTaskBizType(Integer taskBizType) {
		this.taskBizType = taskBizType;
	}

	public Integer getIsMatch() {
		return isMatch;
	}

	public void setIsMatch(Integer isMatch) {
		this.isMatch = isMatch;
	}

	public Integer getTransfer() {
		return transfer;
	}

	public void setTransfer(Integer transfer) {
		this.transfer = transfer;
	}
}
