package com.jd.bluedragon.distribution.station.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: WorkStationGrid
 * @Description: 网格表-实体类
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class WorkStationGrid implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务主键：site_code、floor、grid_no、ref_station_key
	 */
	private String businessKey;

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
	 * 编制人数
	 */
	private Integer standardNum;

	/**
	 * 负责人erp
	 */
	private String ownerUserErp;

	/**
	 * 关联：work_station业务主键
	 */
	private String refStationKey;

	/**
	 * 创建人ERP
	 */
	private String createUser;

	/**
	 * 创建人姓名
	 */
	private String createUserName;

	/**
	 * 修改人ERP
	 */
	private String updateUser;

	/**
	 * 更新人姓名
	 */
	private String updateUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 逻辑删除标志,0-删除,1-正常
	 */
	private Integer yn;

	/**
	 * 数据库时间
	 */
	private Date ts;

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
	 * @param businessKey
	 */
	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	/**
	 *
	 * @return businessKey
	 */
	public String getBusinessKey() {
		return this.businessKey;
	}

	/**
	 *
	 * @param orgCode
	 */
	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	/**
	 *
	 * @return orgCode
	 */
	public Integer getOrgCode() {
		return this.orgCode;
	}

	/**
	 *
	 * @param orgName
	 */
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	/**
	 *
	 * @return orgName
	 */
	public String getOrgName() {
		return this.orgName;
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
	 * @param floor
	 */
	public void setFloor(Integer floor) {
		this.floor = floor;
	}

	/**
	 *
	 * @return floor
	 */
	public Integer getFloor() {
		return this.floor;
	}

	/**
	 *
	 * @param gridNo
	 */
	public void setGridNo(String gridNo) {
		this.gridNo = gridNo;
	}

	/**
	 *
	 * @return gridNo
	 */
	public String getGridNo() {
		return this.gridNo;
	}

	/**
	 *
	 * @param gridCode
	 */
	public void setGridCode(String gridCode) {
		this.gridCode = gridCode;
	}

	/**
	 *
	 * @return gridCode
	 */
	public String getGridCode() {
		return this.gridCode;
	}

	/**
	 *
	 * @param gridName
	 */
	public void setGridName(String gridName) {
		this.gridName = gridName;
	}

	/**
	 *
	 * @return gridName
	 */
	public String getGridName() {
		return this.gridName;
	}

	/**
	 *
	 * @param standardNum
	 */
	public void setStandardNum(Integer standardNum) {
		this.standardNum = standardNum;
	}

	/**
	 *
	 * @return standardNum
	 */
	public Integer getStandardNum() {
		return this.standardNum;
	}

	/**
	 *
	 * @param ownerUserErp
	 */
	public void setOwnerUserErp(String ownerUserErp) {
		this.ownerUserErp = ownerUserErp;
	}

	/**
	 *
	 * @return ownerUserErp
	 */
	public String getOwnerUserErp() {
		return this.ownerUserErp;
	}

	/**
	 *
	 * @param refStationKey
	 */
	public void setRefStationKey(String refStationKey) {
		this.refStationKey = refStationKey;
	}

	/**
	 *
	 * @return refStationKey
	 */
	public String getRefStationKey() {
		return this.refStationKey;
	}

	/**
	 *
	 * @param createUser
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 *
	 * @return createUser
	 */
	public String getCreateUser() {
		return this.createUser;
	}

	/**
	 *
	 * @param createUserName
	 */
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	/**
	 *
	 * @return createUserName
	 */
	public String getCreateUserName() {
		return this.createUserName;
	}

	/**
	 *
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 *
	 * @return updateUser
	 */
	public String getUpdateUser() {
		return this.updateUser;
	}

	/**
	 *
	 * @param updateUserName
	 */
	public void setUpdateUserName(String updateUserName) {
		this.updateUserName = updateUserName;
	}

	/**
	 *
	 * @return updateUserName
	 */
	public String getUpdateUserName() {
		return this.updateUserName;
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
	 * @param yn
	 */
	public void setYn(Integer yn) {
		this.yn = yn;
	}

	/**
	 *
	 * @return yn
	 */
	public Integer getYn() {
		return this.yn;
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

	/**
	 *
	 * @param areaCode
	 */
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	/**
	 *
	 * @return areaCode
	 */
	public String getAreaCode() {
		return this.areaCode;
	}

	/**
	 *
	 * @param areaName
	 */
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	/**
	 *
	 * @return areaName
	 */
	public String getAreaName() {
		return this.areaName;
	}

	/**
	 *
	 * @param workCode
	 */
	public void setWorkCode(String workCode) {
		this.workCode = workCode;
	}

	/**
	 *
	 * @return workCode
	 */
	public String getWorkCode() {
		return this.workCode;
	}

	/**
	 *
	 * @param workName
	 */
	public void setWorkName(String workName) {
		this.workName = workName;
	}

	/**
	 *
	 * @return workName
	 */
	public String getWorkName() {
		return this.workName;
	}


}
