package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

//逆向收货接收对象
@XmlRootElement(name = "ReceiveRequest")
public class ReverseReceiveRequest extends JdRequest {


	private static final long serialVersionUID = 1L;

	//是否可收
    private Integer canReceive;

	/** 操作人编号 */
    private String operatorId;

	/** 操作人 */
    private String operatorName;

	/** 操作人 兼容 */
	private String operaterName;

	private String orderId;

	//机构ID
    private Integer orgId;

	/** 包裹编号 */
    private String packageCode;

	/** 取件单号 */
	private String pickWareCode;
    
    /** 收货时间 */
    private String receiveTime;
    
    //收货类型
    private Integer receiveType;
    
    //拒收编码
    private Integer rejectCode;
    
    //拒收原因
    private String rejectMessage;
    
    /** 发货批次 */
    private String sendCode;

	private String waybillCode;

	/** 商品明细 */
	private List<Eclp2BdReceiveDetail> detailList;

	public List<Eclp2BdReceiveDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<Eclp2BdReceiveDetail> detailList) {
		this.detailList = detailList;
	}

	public Integer getCanReceive() {
		return canReceive;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public String getOrderId() {
		return orderId;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public String getPickWareCode() {
		return pickWareCode;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public Integer getReceiveType() {
		return receiveType;
	}

	public Integer getRejectCode() {
		return rejectCode;
	}

	public String getRejectMessage() {
		return rejectMessage;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setCanReceive(Integer canReceive) {
		this.canReceive = canReceive;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public void setPickWareCode(String pickWareCode) {
		this.pickWareCode = pickWareCode;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	public void setReceiveType(Integer receiveType) {
		this.receiveType = receiveType;
	}

	public void setRejectCode(Integer rejectCode) {
		this.rejectCode = rejectCode;
	}

	public void setRejectMessage(String rejectMessage) {
		this.rejectMessage = rejectMessage;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getOperaterName() {
		return operaterName;
	}

	public void setOperaterName(String operaterName) {
		this.operaterName = operaterName;
	}

	@Override
	public String toString() {
		return "ReverseReceiveRequest [operatorName=" + operatorName
				+ ", operatorId=" + operatorId + ", receiveTime=" + receiveTime
				+ ", packageCode=" + packageCode + ", pickWareCode="
				+ pickWareCode + ", sendCode=" + sendCode + ", receiveType="
				+ receiveType + ", canReceive=" + canReceive + ", rejectCode="
				+ rejectCode + ", rejectMessage=" + rejectMessage + ", orgId="
				+ orgId + ", orderId=" + orderId + "]";
	}
    
    
}
