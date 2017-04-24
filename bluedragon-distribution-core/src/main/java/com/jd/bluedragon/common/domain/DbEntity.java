package com.jd.bluedragon.common.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @ClassName: BaseEntity
 * @Description: (基础类)
 * @author wuyoude
 * @date 2017年4月20日 上午10:25:48
 *
 */
public class DbEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4318733395607100562L;
	/**
	 * 主键ID
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
	 * yn-标识
	 */
	private Integer yn;
	/**
	 * 数据库时间戳
	 */
	private Date timeStamp;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getYn() {
		return yn;
	}
	public void setYn(Integer yn) {
		this.yn = yn;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
}
