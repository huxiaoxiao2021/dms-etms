package com.jd.bluedragon.distribution.rollcontainer.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 周转箱（笼车）
 * @author lihuachang
 * @Date 2017-05-03 14:49:18
 */
public class RollContainer implements Serializable{
	
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
	 * 状态
	 */
	private Integer status;
 	
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
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}
	
	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
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
