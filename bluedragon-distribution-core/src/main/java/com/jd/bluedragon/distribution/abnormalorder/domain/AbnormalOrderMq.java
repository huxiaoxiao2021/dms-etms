package com.jd.bluedragon.distribution.abnormalorder.domain;

import java.util.Date;

public class AbnormalOrderMq {
	
	/**
	 * 二级选项未选择时状态
	 */
	private final Integer NO_SELECE = -1;
	
	/**
	 * 唯一码
	 */
	String fingerPrintCode;
	/**
	 * 订单号
	 */
	String orderId;
	/**
	 * 原因描述
	 * eg:一级原因:1001，订单地址超区。二级原因:10011，COD超区 
	 */
	String content;
	/**
	 * PDA-ERP
	 */
	String erpCode;
	/**
	 * 申请时间
	 * eg: 2013-02-25 00:15:33 
	 */
	Date operateTime;
	/**
	 * 分拣中心编号
	 */
	Integer createSiteCode;
	/**
	 * 分拣中心名称
	 */
	String createSiteName;
	/**
	 * 是否生产：1 (1：已生产，0：未生产。分拣中心默认总是1) 
	 */
	Integer isProduce = 1;
	
	String memo;
	
	Integer isCancel;
		
	/**
	 * 系统标识，默认为DMS 
	 */
	String systemFlag = "DMS";
	
	/**
	 *  一级类型编码
	 */
	Integer biztypeFlag;
	
	public String getSystemFlag() {
		return systemFlag;
	}
	public void setSystemFlag(String systemFlag) {
		this.systemFlag = systemFlag;
	}
	public Integer getBiztypeFlag() {
		return biztypeFlag;
	}
	public void setBiztypeFlag(Integer biztypeFlag) {
		this.biztypeFlag = biztypeFlag;
	}
	public Integer getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(Integer isCancel) {
		this.isCancel = isCancel;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getFingerPrintCode() {
		return fingerPrintCode;
	}
	public void setFingerPrintCode(String fingerPrintCode) {
		this.fingerPrintCode = fingerPrintCode;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getErpCode() {
		return erpCode;
	}
	public void setErpCode(String erpCode) {
		this.erpCode = erpCode;
	}
	public Date getOperateTime() {
		return operateTime!=null?(Date)operateTime.clone():null;
	}
	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime!=null?(Date)operateTime.clone():null;
	}
	public Integer getCreateSiteCode() {
		return createSiteCode;
	}
	public void setCreateSiteCode(Integer createSiteCode) {
		this.createSiteCode = createSiteCode;
	}
	public String getCreateSiteName() {
		return createSiteName;
	}
	public void setCreateSiteName(String createSiteName) {
		this.createSiteName = createSiteName;
	}
	public Integer getIsProduce() {
		return isProduce;
	}
	public void setIsProduce(Integer isProduce) {
		this.isProduce = isProduce;
	}
	
	public AbnormalOrderMq(){
	}
	
	public AbnormalOrderMq(AbnormalOrder abnormalOrder){
		this.orderId = abnormalOrder.getOrderId();
		this.operateTime = abnormalOrder.getOperateTime();
		this.createSiteCode = abnormalOrder.getCreateSiteCode();
		this.createSiteName = abnormalOrder.getCreateSiteName();
		this.erpCode = abnormalOrder.getCreateUserErp();
		this.fingerPrintCode = abnormalOrder.getFingerprint();
		this.systemFlag = "DMS";
		this.biztypeFlag = abnormalOrder.getAbnormalCode1();
		
		StringBuffer sb = new StringBuffer();
		sb.append(abnormalOrder.getAbnormalCode1());
		sb.append(",");
		sb.append(abnormalOrder.getAbnormalReason1());
		sb.append(". ");
		
		/*如果二级分类类型为 -1 表示未选择，这不传递此信息*/
		if(abnormalOrder.getAbnormalCode2()!=null && !abnormalOrder.getAbnormalCode2().equals(NO_SELECE)){
			sb.append(abnormalOrder.getAbnormalCode2());
			sb.append(",");
			sb.append(abnormalOrder.getAbnormalReason2());
		}
		this.content = sb.toString();
	}
}
