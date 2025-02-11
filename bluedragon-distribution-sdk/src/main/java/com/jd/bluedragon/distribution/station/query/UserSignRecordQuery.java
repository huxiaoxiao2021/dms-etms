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
public class UserSignRecordQuery extends BasePagerCondition {

	private static final long serialVersionUID = 1L;
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
	 * 楼层
	 */
	private Integer floor;

	/**
	 * 网格号:01~99
	 */
	private String gridNo;

	/**
	 * 网格ID
	 */
	private String gridCode;

	/**
	 * 网格名称
	 */
	private String gridName;
	/**
	 * 作业区编码
	 */
	private String areaCode;

	/**
	 * 作业区名称
	 */
	private String areaName;

	/**
	 * 工序编码
	 */
	private String workCode;

	/**
	 * 工序名称
	 */
	private String workName;

	/**
	 * 班次:1-白班 2-中班 3-晚班
	 */
	private Integer waveCode;

	/**
	 * 员工ERP|拼音
	 */
	private String userCode;

    /**
     * 员工ID
     */
    private Integer userId;

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

	/**
	 * 实时统计的日期-开始
	 */
	private Date nowDateStart;

	/**
	 * 实时统计的日期-结束
	 */
	private Date nowDateEnd;

	private String refGridKey;

	/**
	 * 签到人名称
	 */
	private String userErp;


	/**
	 * 省区编码
	 */
	private String provinceAgencyCode;
	/**
	 * 枢纽编码
	 */
	private String areaHubCode;

	private List<String> businessKeyList;

	private List<Integer> jobCodeList;

	/**
	 * 身份证号
	 */
	private String idCard;
	
	private List<String> refGridKeyList;

	private Date yesterdayStart;

	public Date getYesterdayStart() {
		return yesterdayStart;
	}

	public void setYesterdayStart(Date yesterdayStart) {
		this.yesterdayStart = yesterdayStart;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
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
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public String getGridNo() {
		return gridNo;
	}
	public void setGridNo(String gridNo) {
		this.gridNo = gridNo;
	}
	public String getGridCode() {
		return gridCode;
	}
	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}
	public String getGridName() {
		return gridName;
	}
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getWorkCode() {
		return workCode;
	}
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}
	public String getWorkName() {
		return workName;
	}
	public void setWorkName(String workName) {
		this.workName = workName;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
	public Date getNowDateStart() {
		return nowDateStart;
	}
	public void setNowDateStart(Date nowDateStart) {
		this.nowDateStart = nowDateStart;
	}
	public Date getNowDateEnd() {
		return nowDateEnd;
	}
	public void setNowDateEnd(Date nowDateEnd) {
		this.nowDateEnd = nowDateEnd;
	}
	public String getRefGridKey() {
		return refGridKey;
	}
	public void setRefGridKey(String refGridKey) {
		this.refGridKey = refGridKey;
	}

	public String getUserErp() {
		return userErp;
	}

	public void setUserErp(String userErp) {
		this.userErp = userErp;
	}

	public String getProvinceAgencyCode() {
		return provinceAgencyCode;
	}

	public void setProvinceAgencyCode(String provinceAgencyCode) {
		this.provinceAgencyCode = provinceAgencyCode;
	}

	public String getAreaHubCode() {
		return areaHubCode;
	}

	public void setAreaHubCode(String areaHubCode) {
		this.areaHubCode = areaHubCode;
	}

	public List<String> getBusinessKeyList() {
		return businessKeyList;
	}

	public void setBusinessKeyList(List<String> businessKeyList) {
		this.businessKeyList = businessKeyList;
	}

	public List<Integer> getJobCodeList() {
		return jobCodeList;
	}

	public void setJobCodeList(List<Integer> jobCodeList) {
		this.jobCodeList = jobCodeList;
	}

	public List<String> getRefGridKeyList() {
		return refGridKeyList;
	}

	public void setRefGridKeyList(List<String> refGridKeyList) {
		this.refGridKeyList = refGridKeyList;
	}
}
