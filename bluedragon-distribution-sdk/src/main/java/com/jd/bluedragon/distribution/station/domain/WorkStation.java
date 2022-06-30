package com.jd.bluedragon.distribution.station.domain;

import java.util.Date;
import java.io.Serializable;

/**
 * @ClassName: WorkStation
 * @Description: 网格工序信息表-实体类
 * @author wuyoude
 * @date 2022年02月23日 11:01:53
 *
 */
public class WorkStation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 主键ID
	 */
	private Long id;

	/**
	 * 业务主键：area_code、work_code
	 */
	private String businessKey;

	/**
	 * 业务条线编码
	 */
	private String businessLineCode;

	/**
	 * 业务条线名称
	 */
	private String businessLineName;

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

	public String getBusinessLineCode() {
		return businessLineCode;
	}

	public void setBusinessLineCode(String businessLineCode) {
		this.businessLineCode = businessLineCode;
	}

	public String getBusinessLineName() {
		return businessLineName;
	}

	public void setBusinessLineName(String businessLineName) {
		this.businessLineName = businessLineName;
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


}
