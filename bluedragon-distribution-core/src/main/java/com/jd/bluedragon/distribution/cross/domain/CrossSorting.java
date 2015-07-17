package com.jd.bluedragon.distribution.cross.domain;

import java.util.Date;

public class CrossSorting implements java.io.Serializable {

	private static final long serialVersionUID = -6246079578423950980L;

	/** 主键 */
	private Long id;

	/** 机构ID */
	private Integer orgId;

	/** 建包/发货分拣中心编码 */
	private Integer createDmsCode;

	/** 建包/发货分拣中心 */
	private String createDmsName;

	/** 目的分拣中心编码 */
	private Integer destinationDmsCode;

	/** 目的分拣中心 */
	private String destinationDmsName;

	/** 可混装分拣中心ID */
	private Integer mixDmsCode;

	/** 可混装分拣中心 */
	private String mixDmsName;

	/** 规则类型 */
	private Integer type;

	/** 创建人编码 */
	private Integer createUserCode;

	/** 创建人 */
	private String createUserName;

	/** 删除人编码 */
	private Integer deleteUserCode;

	/** 删除人 */
	private String deleteUserName;

	/** 创建时间 */
	private Date createTime;

	/** 删除时间 */
	private Date deleteTime;

	/** 有效标识 */
	private Integer yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Integer getCreateDmsCode() {
		return createDmsCode;
	}

	public void setCreateDmsCode(Integer createDmsCode) {
		this.createDmsCode = createDmsCode;
	}

	public String getCreateDmsName() {
		return createDmsName;
	}

	public void setCreateDmsName(String createDmsName) {
		this.createDmsName = createDmsName;
	}

	public Integer getDestinationDmsCode() {
		return destinationDmsCode;
	}

	public void setDestinationDmsCode(Integer destinationDmsCode) {
		this.destinationDmsCode = destinationDmsCode;
	}

	public String getDestinationDmsName() {
		return destinationDmsName;
	}

	public void setDestinationDmsName(String destinationDmsName) {
		this.destinationDmsName = destinationDmsName;
	}

	public Integer getMixDmsCode() {
		return mixDmsCode;
	}

	public void setMixDmsCode(Integer mixDmsCode) {
		this.mixDmsCode = mixDmsCode;
	}

	public String getMixDmsName() {
		return mixDmsName;
	}

	public void setMixDmsName(String mixDmsName) {
		this.mixDmsName = mixDmsName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public Integer getDeleteUserCode() {
		return deleteUserCode;
	}

	public void setDeleteUserCode(Integer deleteUserCode) {
		this.deleteUserCode = deleteUserCode;
	}

	public String getDeleteUserName() {
		return deleteUserName;
	}

	public void setDeleteUserName(String deleteUserName) {
		this.deleteUserName = deleteUserName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
