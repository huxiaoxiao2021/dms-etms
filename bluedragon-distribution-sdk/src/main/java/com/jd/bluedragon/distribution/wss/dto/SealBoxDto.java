package com.jd.bluedragon.distribution.wss.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaohengchong
 * 
 *         封箱对象
 */
public class SealBoxDto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 全局唯一ID */
	private Long id;
	
	/** 箱号 */
	private String boxCode;

	/** 封箱编号 */
	private String code;

	/** 创建站点编号 */
	private Integer createSiteCode;

	/** 接收站点编号 */
	private Integer receiveSiteCode;

	/** 创建人编号 */
	private Integer createUserCode;

	/** 创建人 */
	private String createUser;

	/** 创建时间 */
	private Date createTime;

	/** 修改人编号 */
	private Integer updateUserCode;

	/** 修改人 */
	private String updateUser;

	/** 修改时间 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Integer getUpdateUserCode() {
		return updateUserCode;
	}

	public void setUpdateUserCode(Integer updateUserCode) {
		this.updateUserCode = updateUserCode;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}
}
