package com.jd.bluedragon.distribution.station.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 删除请求
 * @author wuyoude
 *
 * @param <T> 删除实体类型
 */
public class DeleteRequest<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	/**
	 * 操作人站点
	 */
	private Integer operateSiteCode;
	/**
	 * 操作人编码
	 */
	private String operateUserCode;
	/**
	 * 操作人名称
	 */
	private String operateUserName;
	/**
	 * 操作时间
	 */
	private Date operateTime;
	/**
	 * 删除数据列表
	 */
	private List<T> dataList;
	
	public Integer getOperateSiteCode() {
		return operateSiteCode;
	}
	public void setOperateSiteCode(Integer operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}
	public String getOperateUserCode() {
		return operateUserCode;
	}
	public void setOperateUserCode(String operateUserCode) {
		this.operateUserCode = operateUserCode;
	}
	public String getOperateUserName() {
		return operateUserName;
	}
	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}
	public Date getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}
	public List<T> getDataList() {
		return dataList;
	}
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}
}
