package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 人员签到-咚咚通知实体类
 * 
 * @author wuyoude
 * @date 2022年3月20日 14:30:43
 *
 */
public class UserSignNoticeVo implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 签到日期
	 */
	private Date signDate;
	/**
	 * 机构名称
	 */
	private String orgName;

	/**
	 * 场地名称
	 */
	private String siteName;
	/**
	 * 编制人数总计
	 */
	private Integer standardNumSum;
	/**
	 * 网格数总计
	 */
	private Integer gridCount;
	/**
	 * 工序数总计
	 */
	private Integer stationCount;
	/**
	 * 作业区总计
	 */
	private Integer areaCount;
	/**
	 * 波次明细
	 */
	private List<UserSignNoticeWaveItemVo> waveItems;
	
	public Date getSignDate() {
		return signDate;
	}
	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public Integer getStandardNumSum() {
		return standardNumSum;
	}
	public void setStandardNumSum(Integer standardNumSum) {
		this.standardNumSum = standardNumSum;
	}
	public Integer getGridCount() {
		return gridCount;
	}
	public void setGridCount(Integer gridCount) {
		this.gridCount = gridCount;
	}
	public Integer getStationCount() {
		return stationCount;
	}
	public void setStationCount(Integer stationCount) {
		this.stationCount = stationCount;
	}
	public Integer getAreaCount() {
		return areaCount;
	}
	public void setAreaCount(Integer areaCount) {
		this.areaCount = areaCount;
	}
	public List<UserSignNoticeWaveItemVo> getWaveItems() {
		return waveItems;
	}
	public void setWaveItems(List<UserSignNoticeWaveItemVo> waveItems) {
		this.waveItems = waveItems;
	}

}
