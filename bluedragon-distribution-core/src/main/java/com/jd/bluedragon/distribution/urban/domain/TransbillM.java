package com.jd.bluedragon.distribution.urban.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 城配运单M表-实体类
 * 
 * @ClassName: TransbillM
 * @Description: TODO
 * @author wuyoude
 * @date 2017年04月28日 13:30:01
 *
 */
@Deprecated
public class TransbillM implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4043754561539583141L;

	/**
	 * 主键
	 */
	private Long mId;

	/**
	 * 运输单号
	 */
	private String transbillCode;

	/**
	 * 运单号
	 */
	private String waybillCode;

	/**
	 * 订单异常标示  drop
	 */
	private Integer orderFlag;

	/**
	 * 调度单号
	 */
	private String scheduleBillCode;

	/**
	 * 调度单中运输单数量  drop
	 */
	private Integer scheduleAmount;

	/**
	 * 卡位号(卡车的位置)
	 */
	private String truckSpot;

	/**
	 * 调度单配载顺序  drop
	 */
	private String allocateSequence;

	/**
	 * 妥投完成时间  drop
	 */
	private Date arriveTime;

	/**
	 * 再投时间  drop
	 */
	private Date redeliveryTime;

	/**
	 * 再投地址  drop
	 */
	private String redeliveryAddress;

	/**
	 * 操作时间
	 */
	private Date operateTime;

	/**
	 * 运输单状态
	 */
	private Integer transbillState;

	/**
	 * 当前运输单所属站点ID
	 */
	private Integer siteId;

	/**
	 * 当前运输单所属站点名称
	 */
	private String siteName;

	/**
	 * 当前运输单所属站点CODE
	 */
	private String siteCode;

	/**
	 * 生成方式，2,直接下发，1、补全生成
	 */
	private Integer generateType;

	/**
	 * 是否已推送预分拣 0:没推送 1:已推送
	 */
	private Integer pushPreFlag;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 创建人
	 */
	private String createUser;

	/**
	 * 更新人
	 */
	private String updateUser;

	/**
	 * 分区时间
	 */
	private Date partitionTime;

	/**
	 * 时间戳
	 */
	private Long tsM;

	/**
	 * 是否有效
	 */
	private Integer yn;
	/**
	 * 要求运输模式 1-分拣集货 2-仓库直发
	 */
	private Integer requireTransMode;

	/**
	 *
	 * @param mId
	 */
	public void setMId(Long mId) {
		this.mId = mId;
	}

	/**
	 *
	 * @return mId
	 */
	public Long getMId() {
		return this.mId;
	}

	/**
	 *
	 * @param transbillCode
	 */
	public void setTransbillCode(String transbillCode) {
		this.transbillCode = transbillCode;
	}

	/**
	 *
	 * @return transbillCode
	 */
	public String getTransbillCode() {
		return this.transbillCode;
	}

	/**
	 *
	 * @param waybillCode
	 */
	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	/**
	 *
	 * @return waybillCode
	 */
	public String getWaybillCode() {
		return this.waybillCode;
	}

	/**
	 *
	 * @param orderFlag
	 */
	public void setOrderFlag(Integer orderFlag) {
		this.orderFlag = orderFlag;
	}

	/**
	 *
	 * @return orderFlag
	 */
	public Integer getOrderFlag() {
		return this.orderFlag;
	}

	/**
	 *
	 * @param scheduleBillCode
	 */
	public void setScheduleBillCode(String scheduleBillCode) {
		this.scheduleBillCode = scheduleBillCode;
	}

	/**
	 *
	 * @return scheduleBillCode
	 */
	public String getScheduleBillCode() {
		return this.scheduleBillCode;
	}

	/**
	 *
	 * @param scheduleAmount
	 */
	public void setScheduleAmount(Integer scheduleAmount) {
		this.scheduleAmount = scheduleAmount;
	}

	/**
	 *
	 * @return scheduleAmount
	 */
	public Integer getScheduleAmount() {
		return this.scheduleAmount;
	}

	/**
	 *
	 * @param truckSpot
	 */
	public void setTruckSpot(String truckSpot) {
		this.truckSpot = truckSpot;
	}

	/**
	 *
	 * @return truckSpot
	 */
	public String getTruckSpot() {
		return this.truckSpot;
	}

	/**
	 *
	 * @param allocateSequence
	 */
	public void setAllocateSequence(String allocateSequence) {
		this.allocateSequence = allocateSequence;
	}

	/**
	 *
	 * @return allocateSequence
	 */
	public String getAllocateSequence() {
		return this.allocateSequence;
	}

	/**
	 *
	 * @param arriveTime
	 */
	public void setArriveTime(Date arriveTime) {
		this.arriveTime = arriveTime;
	}

	/**
	 *
	 * @return arriveTime
	 */
	public Date getArriveTime() {
		return this.arriveTime;
	}

	/**
	 *
	 * @param redeliveryTime
	 */
	public void setRedeliveryTime(Date redeliveryTime) {
		this.redeliveryTime = redeliveryTime;
	}

	/**
	 *
	 * @return redeliveryTime
	 */
	public Date getRedeliveryTime() {
		return this.redeliveryTime;
	}

	/**
	 *
	 * @param redeliveryAddress
	 */
	public void setRedeliveryAddress(String redeliveryAddress) {
		this.redeliveryAddress = redeliveryAddress;
	}

	/**
	 *
	 * @return redeliveryAddress
	 */
	public String getRedeliveryAddress() {
		return this.redeliveryAddress;
	}

	/**
	 *
	 * @param operateTime
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 *
	 * @return operateTime
	 */
	public Date getOperateTime() {
		return this.operateTime;
	}

	/**
	 *
	 * @param transbillState
	 */
	public void setTransbillState(Integer transbillState) {
		this.transbillState = transbillState;
	}

	/**
	 *
	 * @return transbillState
	 */
	public Integer getTransbillState() {
		return this.transbillState;
	}

	/**
	 *
	 * @param siteId
	 */
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	/**
	 *
	 * @return siteId
	 */
	public Integer getSiteId() {
		return this.siteId;
	}

	/**
	 *
	 * @param siteName
	 */
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	/**
	 *
	 * @return siteName
	 */
	public String getSiteName() {
		return this.siteName;
	}

	/**
	 *
	 * @param siteCode
	 */
	public void setSiteCode(String siteCode) {
		this.siteCode = siteCode;
	}

	/**
	 *
	 * @return siteCode
	 */
	public String getSiteCode() {
		return this.siteCode;
	}

	/**
	 *
	 * @param generateType
	 */
	public void setGenerateType(Integer generateType) {
		this.generateType = generateType;
	}

	/**
	 *
	 * @return generateType
	 */
	public Integer getGenerateType() {
		return this.generateType;
	}

	/**
	 *
	 * @param pushPreFlag
	 */
	public void setPushPreFlag(Integer pushPreFlag) {
		this.pushPreFlag = pushPreFlag;
	}

	/**
	 *
	 * @return pushPreFlag
	 */
	public Integer getPushPreFlag() {
		return this.pushPreFlag;
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
	 * @param partitionTime
	 */
	public void setPartitionTime(Date partitionTime) {
		this.partitionTime = partitionTime;
	}

	/**
	 *
	 * @return partitionTime
	 */
	public Date getPartitionTime() {
		return this.partitionTime;
	}

	/**
	 *
	 * @param tsM
	 */
	public void setTsM(Long tsM) {
		this.tsM = tsM;
	}

	/**
	 *
	 * @return tsM
	 */
	public Long getTsM() {
		return this.tsM;
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

	public Integer getRequireTransMode() {
		return requireTransMode;
	}

	public void setRequireTransMode(Integer requireTransMode) {
		this.requireTransMode = requireTransMode;
	}


}
