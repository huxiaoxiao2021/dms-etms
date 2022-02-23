package com.jd.bluedragon.distribution.station.query;

import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 人员签到表-查询条件实体类
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public class UserSignRecordQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
	/**
	 * 工序编码
	 */
	private String workCode;

	/**
	 * 机构编码
	 */
	private Integer orgCode;

	/**
	 * 场地编码
	 */
	private Integer siteCode;

	/**
	 * 网格编号
	 */
	private String gridNo;

	/**
	 * 班次:1-白班 2-中班 3-晚班
	 */
	private Integer waveCode;

	/**
	 * 员工ERP|拼音
	 */
	private String userCode;

	/**
	 * 工种:1-正式工 2-派遣工 3-外包工 4-临时工5-小时工
	 */
	private Integer jobCode;

	/**
	 * 签到日期：根据签到时间，计算归属日期
	 */
	private Date signDate;

	/**
	 * 签到时间，同一天内多次签到记录最早签到时间
	 */
	private Date signInTime;

	/**
	 * 签退时间，同一天内多次签到记录最晚签到时间
	 */
	private Date signOutTime;
	/**
	 * 签到日期
	 */
	private String signDateStr;
	/**
	 * 创建人
	 */
	private String createUser;
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
	 * 分页-pageSize
	 */
	private Integer pageSize;
	
	public String getWorkCode() {
		return workCode;
	}
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
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
	public String getGridNo() {
		return gridNo;
	}
	public void setGridNo(String gridNo) {
		this.gridNo = gridNo;
	}
	public Integer getWaveCode() {
		return waveCode;
	}
	public void setWaveCode(Integer waveCode) {
		this.waveCode = waveCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public Integer getJobCode() {
		return jobCode;
	}
	public void setJobCode(Integer jobCode) {
		this.jobCode = jobCode;
	}
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
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
	public String getSignDateStr() {
		return signDateStr;
	}
	public void setSignDateStr(String signDateStr) {
		this.signDateStr = signDateStr;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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
}
