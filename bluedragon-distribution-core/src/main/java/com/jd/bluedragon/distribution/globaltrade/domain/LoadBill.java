package com.jd.bluedragon.distribution.globaltrade.domain;

import java.io.Serializable;
import java.util.Date;

public class LoadBill implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int BEGINNING = 10;
	public static final int APPLIED = 20;
	public static final int GREENLIGHT = 30;
	public static final int REDLIGHT = 40;

	/** 全局ID */
	private Long id;

	/** 装载单ID(随机号码) */
	private String loadId;

	/** 仓库ID */
	private String warehouseId;

	/** 运单号 */
	private String waybillCode;

	/** 包裹号 */
	private String packageBarcode;

	/** 包裹数量 */
	private Integer packageAmount;

	/** 订单号 */
	private String orderId;

	private String boxCode;

	/** 分拣中心编号 */
	private Integer dmsCode;

	/** 分拣中心名称 */
	private String dmsName;

	/** 发货时间 */
	private Date sendTime;

	/** 发货批次号 */
	private String sendCode;

	/** 车牌号 */
	private String truckNo;

	/** 审批编号,10初始,20已申请,30已放行,40未放行 */
	private Integer approvalCode;

	/** 审批时间 */
	private Date approvalTime;

	/** 申报海关编码。默认：5165南沙旅检 */
	private String ctno;

	/** 申报国检编码。默认：000069申报地国检 */
	private String gjno;

	/** 物流企业编码。默认：京配编号 */
	private String tpl;

	/** 重量(精确小数点2位，单位kg) */
	private Double weight;

	/** 创建时间 */
	private Date createTime;

	/** 更新时间 */
	private Date updateTime;

	/** 装载单生成时间 */
	private Date genTime;

	/** 创建人编号 */
	private Integer createUserCode;

	/** 创建人 */
	private String createUser;

	/** 打包人编号 */
	private Integer packageUserCode;

	/** 打包人 */
	private String packageUser;

	/** 打包时间 */
	private Date packageTime;

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

	public String getLoadId() {
		return loadId;
	}

	public void setLoadId(String loadId) {
		this.loadId = loadId;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getCreateUserCode() {
		return createUserCode;
	}

	public void setCreateUserCode(Integer createUserCode) {
		this.createUserCode = createUserCode;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Integer getPackageAmount() {
		return packageAmount;
	}

	public void setPackageAmount(Integer packageAmount) {
		this.packageAmount = packageAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
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

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getTruckNo() {
		return truckNo;
	}

	public void setTruckNo(String truckNo) {
		this.truckNo = truckNo;
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

	public String getCtno() {
		return ctno;
	}

	public void setCtno(String ctno) {
		this.ctno = ctno;
	}

	public String getGjno() {
		return gjno;
	}

	public void setGjno(String gjno) {
		this.gjno = gjno;
	}

	public String getTpl() {
		return tpl;
	}

	public void setTpl(String tpl) {
		this.tpl = tpl;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
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

	public Date getGenTime() {
		return genTime;
	}

	public void setGenTime(Date genTime) {
		this.genTime = genTime;
	}

	public Integer getPackageUserCode() {
		return packageUserCode;
	}

	public void setPackageUserCode(Integer packageUserCode) {
		this.packageUserCode = packageUserCode;
	}

	public String getPackageUser() {
		return packageUser;
	}

	public void setPackageUser(String packageUser) {
		this.packageUser = packageUser;
	}

	public Date getPackageTime() {
		return packageTime;
	}

	public void setPackageTime(Date packageTime) {
		this.packageTime = packageTime;
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
