package com.jd.bluedragon.common.dto.station;

import java.io.Serializable;

/**
 * 上岗码数据信息
 *
 * @author wuyoude
 * @date 2022/2/25 5:25 PM
 */
public class PositionData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

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
     * 作业区编码
     */
    private String areaCode;

    /**
     * 作业区名称
     */
    private String areaName;

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
     * 工序编码
     */
    private String workCode;

    /**
     * 工序名称
     */
    private String workName;
    /**
     * 默认功能码-PDA自动跳转相关的功能页面
     */
    private String defaultMenuCode;

	/**
	 * 岗位名称：卸车，到车等
	 */
	private String positionName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDefaultMenuCode() {
		return defaultMenuCode;
	}

	public void setDefaultMenuCode(String defaultMenuCode) {
		this.defaultMenuCode = defaultMenuCode;
	}

	public String getPositionName() {
		return positionName;
	}

	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
}
