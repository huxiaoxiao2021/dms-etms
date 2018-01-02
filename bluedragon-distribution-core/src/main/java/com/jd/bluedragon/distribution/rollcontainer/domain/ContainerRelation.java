package com.jd.bluedragon.distribution.rollcontainer.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 周转箱关系表
 * @author lihuachang
 * @Date 2017-05-03 14:49:18
 */
public class ContainerRelation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 主键
	 */
	private String id;
 	
	/**
	 * 编码
	 */
	private String containerCode;
 	
	/**
	 * 箱号
	 */
	private String boxCode;
 	
	/**
	 * 储位对应的站点
	 */
	private String siteCode;
 	
	/**
	 * 包裹个数
	 */
	private String packageCount;
	/**
	 * 分拣中心编码
	 */
	private Integer dmsId;
	/**
	 * 发货状态
	 */
	private Integer sendStatus;

	/**
	 * 创建时间
	 */
	private Date createTime;
 	
	/**
	 * 更新时间
	 */
	private Date updateTime;
 	
	/**
	 * 创建用户
	 */
	private String createUser;
 	
	/**
	 * 更新用户
	 */
	private String updateUser;
 	
	/**
	 * 是否有效
	 */
	private int isDelete;
 	
	/**
	 * 默认时间
	 */
	private Date ts;
 	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	
	
	
	/**
	 * @return the containerCode
	 */
	public String getContainerCode() {
		return containerCode;
	}
	
	/**
	 * @param containerCode the containerCode to set
	 */
	public void setContainerCode(String containerCode) {
		this.containerCode = containerCode;
	}
	
	
	
	
	/**
	 * @return the boxCode
	 */
	public String getBoxCode() {
		return boxCode;
	}
	
	/**
	 * @param boxCode the boxCode to set
	 */
	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}
	
	
	
	
	/**
	 * @return the siteCode
	 */
	public String getSiteCode() {
		return siteCode;
	}
	
	/**
	 * @param siteCode the siteCode to set
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}
	
	
	
	
	/**
	 * @return the packageCount
	 */
	public String getPackageCount() {
		return packageCount;
	}
	
	/**
	 * @param packageCount the packageCount to set
	 */
	public void setPackageCount(String packageCount) {
		this.packageCount = packageCount;
	}


	public Integer getDmsId() {
		return dmsId;
	}

	public void setDmsId(Integer dmsId) {
		this.dmsId = dmsId;
	}

	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}
	
	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}
	
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	
	
	
	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	
	
	
	/**
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}



	/**
	 * @param createUser the createUser to set

	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
	
	
	
	/**
	 * @return the updateUser
	 */
	public String getUpdateUser() {
		return updateUser;
	}
	
	/**
	 * @param updateUser the updateUser to set
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
	
	
	/**
	 * @return the isDelete
	 */
	public int getIsDelete() {
		return isDelete;
	}
	
	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}
	
	
	
	
	/**
	 * @return the ts
	 */
	public Date getTs() {
		return ts;
	}
	
	/**
	 * @param ts the ts to set
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}
	
	
}
