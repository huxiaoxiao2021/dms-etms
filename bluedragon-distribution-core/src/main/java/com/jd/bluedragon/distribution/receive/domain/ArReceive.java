package com.jd.bluedragon.distribution.receive.domain;

import java.util.Date;

import com.jd.ql.dms.common.web.mvc.api.Entity;

/**
 * @ClassName: ArReceive
 * @Description: 空铁提货-可以认为是收货的子类型
 * @author: wuyoude
 * @date: 2018年1月4日 下午11:41:51
 *
 */
public class ArReceive extends Receive implements Entity{
	private static final long serialVersionUID = 1L;
	 /** 执行次数 */
	private Integer taskExeCount;
	 /** 0未处理  1已处理 */
	private Integer receiveStatus;
	 /** 操作时间 */
	private Date operateTime;
	/**
	 * 发货登记的批次号
	 */
	private Long sendRegisterId;
	/**
	 * 发货登记的批次号
	 */
	private String sendCode;
    /**
     * 摆渡车型
     */
    private Integer shuttleBusType;

    /**
     * 摆渡车牌号
     */
    private String shuttleBusNum;
    
	 /** 数据库时间戳*/
	private Date ts;
    
    /**
     * 备注信息
     */
    private String remark;
	/**
	 * @return the taskExeCount
	 */
	public Integer getTaskExeCount() {
		return taskExeCount;
	}

	/**
	 * @param taskExeCount the taskExeCount to set
	 */
	public void setTaskExeCount(Integer taskExeCount) {
		this.taskExeCount = taskExeCount;
	}

	/**
	 * @return the sendRegisterId
	 */
	public Long getSendRegisterId() {
		return sendRegisterId;
	}

	/**
	 * @param sendRegisterId the sendRegisterId to set
	 */
	public void setSendRegisterId(Long sendRegisterId) {
		this.sendRegisterId = sendRegisterId;
	}

	/**
	 * @return the sendCode
	 */
	public String getSendCode() {
		return sendCode;
	}

	/**
	 * @param sendCode the sendCode to set
	 */
	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	/**
	 * @return the shuttleBusType
	 */
	public Integer getShuttleBusType() {
		return shuttleBusType;
	}

	/**
	 * @param shuttleBusType the shuttleBusType to set
	 */
	public void setShuttleBusType(Integer shuttleBusType) {
		this.shuttleBusType = shuttleBusType;
	}

	/**
	 * @return the shuttleBusNum
	 */
	public String getShuttleBusNum() {
		return shuttleBusNum;
	}

	/**
	 * @param shuttleBusNum the shuttleBusNum to set
	 */
	public void setShuttleBusNum(String shuttleBusNum) {
		this.shuttleBusNum = shuttleBusNum;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public void setId(Long id) {
		super.setReceiveId(id);
	}

	@Override
	public Long getId() {
		return super.getReceiveId();
	}

	/**
	 * @return the receiveStatus
	 */
	public Integer getReceiveStatus() {
		return receiveStatus;
	}

	/**
	 * @param receiveStatus the receiveStatus to set
	 */
	public void setReceiveStatus(Integer receiveStatus) {
		this.receiveStatus = receiveStatus;
	}

	/**
	 * @return the operateTime
	 */
	public Date getOperateTime() {
		return operateTime;
	}

	/**
	 * @param operateTime the operateTime to set
	 */
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
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
