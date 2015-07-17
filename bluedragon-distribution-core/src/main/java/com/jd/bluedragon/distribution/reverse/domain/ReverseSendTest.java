package com.jd.bluedragon.distribution.reverse.domain;

import java.util.Date;

public class ReverseSendTest {
    
	//是否可收
    private Integer canReceive;
	
	
    //创建时间 
    private Date createTime;

	//数据指纹
	private String fingerprint;

	//自增主键
	private Long id;

    //操作人编号
    private String operatorId;
    
    // 操作人
    private String operatorName;
    
    //机构ID
    private Integer orgId;
    
    //包裹编号
    private String packageCode;
    
    //取件单号
	private String pickWareCode;
    
    //收货时间
    private String receiveTime;
    
    //收货类型
    private Integer receiveType;

	//拒收编码
    private Integer rejectCode;

	//拒收原因
    private String rejectMessage;

	//发货批次
    private String sendCode;

	//最有一次修改时间
    private Date updateTime;

	//是否删除
    private Integer yn;

	public Integer getCanReceive() {
		return canReceive;
	}


	public Date getCreateTime() {
		return createTime!=null?(Date)createTime.clone():null;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public Long getId() {
		return id;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public String getOperatorName() {
		return operatorName;
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

	public Date getUpdateTime() {
		return updateTime!=null?(Date)updateTime.clone():null;
	}

	public Integer getYn() {
		return yn;
	}

	public void setCanReceive(Integer canReceive) {
		this.canReceive = canReceive;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime!=null?(Date)createTime.clone():null;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public void setId(Long id) {
		this.id = id;
	}
    
    public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
    
    public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
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
	 /** 订单编号 */
    private Long orderId;
	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime!=null?(Date)updateTime.clone():null;
	}

	public void setYn(Integer yn) {
		this.yn = yn;
	}

}
