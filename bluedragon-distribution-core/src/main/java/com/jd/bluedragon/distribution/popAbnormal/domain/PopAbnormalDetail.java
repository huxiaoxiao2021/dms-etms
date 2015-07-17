package com.jd.bluedragon.distribution.popAbnormal.domain;

import java.util.Date;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-2-27 下午02:13:38
 *
 * POP差异订单明细对象
 */
public class PopAbnormalDetail {

	/**
	 *  自增主键 
	 */
	private Long abnormalDetailId;
	
	/**
	 *  差异列表对应ID
	 */
	private Long abnormalId;
	
	/**
	 * 操作人CODE
	 */
	private Integer operatorCode;
	
	/**
	 * 操作人名称
	 */
	private String operatorName;

	/**
	 * 操作时间 
	 */
	private Date operatorTime;
	
	/**
	 * 描述
	 */
	private String remark;

	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 防重标识
	 */
	private String fingerPrint;
	
	/**
	 * 是否有效
	 */
	private Integer yn;

	public Long getAbnormalDetailId() {
		return abnormalDetailId;
	}

	public void setAbnormalDetailId(Long abnormalDetailId) {
		this.abnormalDetailId = abnormalDetailId;
	}

	public Long getAbnormalId() {
		return abnormalId;
	}

	public void setAbnormalId(Long abnormalId) {
		this.abnormalId = abnormalId;
	}

	public Integer getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public Date getOperatorTime() {
		return operatorTime!=null?(Date)operatorTime.clone():null;
	}

	public void setOperatorTime(Date operatorTime) {
		this.operatorTime = operatorTime!=null?(Date)operatorTime.clone():null;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public String getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(String fingerPrint) {
		this.fingerPrint = fingerPrint;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}
}
