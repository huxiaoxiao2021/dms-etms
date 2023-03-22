package com.jd.bluedragon.distribution.station.query;

import java.util.Date;
import java.util.List;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 人员签到表-查询条件实体类
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class UserSignRecordFlowQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	/**
	 * 关联签到表id
	 */
	private Long refRecordId;
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
	 * 员工ERP|拼音
	 */
	private String userCode;
	/**
	 * 流程类型
	 */
	private Integer flowType;
	
	/**
	 * 签到日期：根据签到时间，计算归属日期
	 */
	private Date signDate;

	/**
	 * 签到日期
	 */
	private String signDateStr;
	/**
	 * 签到日期-开始
	 */
	private String signDateStartStr;
	/**
	 * 签到日期-结束
	 */
	private String signDateEndStr;
	/**
	 * 签到日期-开始
	 */
	private Date signDateStart;
	/**
	 * 签到日期-结束
	 */
	private Date signDateEnd;
	/**
	 * 签到时间
	 */
	private Date signInTime;

	/**
	 * 签退时间
	 */
	private Date signOutTime;
	/**
	 * 流程状态列表
	 */
	private List<Integer> flowStatusList;
	/**
	 * 分页-pageSize
	 */
	private Integer pageSize;
	
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
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Integer getFlowType() {
		return flowType;
	}
	public void setFlowType(Integer flowType) {
		this.flowType = flowType;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public String getSignDateStr() {
		return signDateStr;
	}
	public void setSignDateStr(String signDateStr) {
		this.signDateStr = signDateStr;
	}
	public String getSignDateStartStr() {
		return signDateStartStr;
	}
	public void setSignDateStartStr(String signDateStartStr) {
		this.signDateStartStr = signDateStartStr;
	}
	public String getSignDateEndStr() {
		return signDateEndStr;
	}
	public void setSignDateEndStr(String signDateEndStr) {
		this.signDateEndStr = signDateEndStr;
	}
	public Date getSignDateStart() {
		return signDateStart;
	}
	public void setSignDateStart(Date signDateStart) {
		this.signDateStart = signDateStart;
	}
	public Date getSignDateEnd() {
		return signDateEnd;
	}
	public void setSignDateEnd(Date signDateEnd) {
		this.signDateEnd = signDateEnd;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Date getSignInTime() {
		return signInTime;
	}
	public void setSignInTime(Date signInTime) {
		this.signInTime = signInTime;
	}
	public Date getSignOutTime() {
		return signOutTime;
	}
	public void setSignOutTime(Date signOutTime) {
		this.signOutTime = signOutTime;
	}
	public List<Integer> getFlowStatusList() {
		return flowStatusList;
	}
	public void setFlowStatusList(List<Integer> flowStatusList) {
		this.flowStatusList = flowStatusList;
	}
	public Long getRefRecordId() {
		return refRecordId;
	}
	public void setRefRecordId(Long refRecordId) {
		this.refRecordId = refRecordId;
	}
}
