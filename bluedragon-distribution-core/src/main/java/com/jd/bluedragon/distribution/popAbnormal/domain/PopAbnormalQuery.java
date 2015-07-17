package com.jd.bluedragon.distribution.popAbnormal.domain;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.Pager;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2013-3-1 下午03:39:42
 *
 * POP收货差异列表查询条件对象
 */
public class PopAbnormalQuery {
	/**
	 * 创建机构CODE
	 */
	private Integer orgCode;
	
	/**
	 * 分拣中心编号
	 */
	private Integer createSiteCode;
	
	/**
	 * 一级差异类型ID
	 */
	private Integer mainType;
	
	/**
	 * 二级差异类型ID
	 */
	private Integer subType;
	
	/**
	 * 商家ID
	 */
	private String popSupNo;
	
	/**
	 * 商家名称
	 */
	private String popSupName;

	/**
	 * 运单号
	 */
	private String waybillCode;
	
	private String orderCode;

	/**
	 * 操作人Code
	 */
	private Integer operatorCode;
	
	/**
	 * 操作人Name
	 */
	private String operatorName;
	
	/**
	 * 时间类型：
	 * 	1，发起时间；
	 * 	2，回复时间；
	 */
	private Integer dateType;
	
	/**
	 * 开始时间
	 */
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	private Date endTime;

	/**
	 * 处理状态
	 */
	private Integer abnormalStatus;
	
	/**
	 * 排序规则
	 */
	private String orderByString;
	
	private Pager<List<PopReceiveAbnormal>> pager;

	public Integer getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(Integer orgCode) {
		this.orgCode = orgCode;
	}

	public Integer getCreateSiteCode() {
		return createSiteCode;
	}

	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}

	public Integer getMainType() {
		return mainType;
	}

	public void setMainType(Integer mainType) {
		this.mainType = mainType;
	}

	public Integer getSubType() {
		return subType;
	}

	public void setSubType(Integer subType) {
		this.subType = subType;
	}

	public String getPopSupNo() {
		return popSupNo;
	}

	public void setPopSupNo(String popSupNo) {
		this.popSupNo = popSupNo;
	}

	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
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

	public Integer getDateType() {
		return dateType;
	}

	public void setDateType(Integer dateType) {
		this.dateType = dateType;
	}

	public Date getStartTime() {
		return startTime!=null?(Date)startTime.clone():null;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime!=null?(Date)startTime.clone():null;
	}

	public Date getEndTime() {
		return endTime!=null?(Date)endTime.clone():null;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime!=null?(Date)endTime.clone():null;
	}

	public Integer getAbnormalStatus() {
		return abnormalStatus;
	}

	public void setAbnormalStatus(Integer abnormalStatus) {
		this.abnormalStatus = abnormalStatus;
	}

	public String getOrderByString() {
		return orderByString;
	}

	public void setOrderByString(String orderByString) {
		this.orderByString = orderByString;
	}

	public Pager<List<PopReceiveAbnormal>> getPager() {
		return pager;
	}

	public void setPager(Pager<List<PopReceiveAbnormal>> pager) {
		this.pager = pager;
	}
}
