package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;
import java.util.Date;

public class GlobalTrade implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 全局ID */
	private Long id;

	/** 运单号 */
	private String waybillCode;

	/** 包裹号 */
	private String packageBarcode;

	/** 订单号 */
	private String orderCode;

	/** 分拣中心编号 */
	private Integer dmsCode;

	/** 分拣中心名称 */
	private String dmsName;

	/** 发货时间 */
	private Date sendTime;

	/** 发货批次号 */
	private String sendCode;

	/** 车牌号 */
	private String carCode;

	/** 审批编号,10初始,20已申请,30已放行,40未放行 */
	private Integer approvalCode;

	/** 审批时间 */
	private Date approvalTime;

	/** 创建时间 */
	private Date createTime;

	/** 更新时间 */
	private Date updateTime;

	/** 备注 */
	private String remark;

	/** 有效标识 */
	private Integer yn;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public Integer getDmsCode() {
		return dmsCode;
	}

	public void setDmsCode(Integer dmsCode) {
		this.dmsCode = dmsCode;
	}

	public String getDmsName() {
		return dmsName;
	}

	public void setDmsName(String dmsName) {
		this.dmsName = dmsName;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getCarCode() {
		return carCode;
	}

	public void setCarCode(String carCode) {
		this.carCode = carCode;
	}

	public Integer getApprovalCode() {
		return approvalCode;
	}

	public void setApprovalCode(Integer approvalCode) {
		this.approvalCode = approvalCode;
	}

	public Date getApprovalTime() {
		return approvalTime;
	}

	public void setApprovalTime(Date approvalTime) {
		this.approvalTime = approvalTime;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getYn() {
		return yn;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
