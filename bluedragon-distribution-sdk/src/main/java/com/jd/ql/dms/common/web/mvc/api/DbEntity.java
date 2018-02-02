package com.jd.ql.dms.common.web.mvc.api;

import java.util.Date;
  
/**
 * 
 * @ClassName: DbEntity
 * @Description: 数据库实体类基类，包含数据库基础字段
 * @author wuyoude
 * @date 2017年4月19日 下午8:33:23
 *
 */
public class DbEntity implements Entity{
	private static final long serialVersionUID = 1L;
	/**
	 * id
	 */
	private Long id;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 是否有效
	 */
	private Integer isDelete;
	/**
	 * 数据库时间
	 */
	private Date ts;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * The set method for id.
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the isDelete
	 */
	public Integer getIsDelete() {
		return isDelete;
	}
	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Integer isDelete) {
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