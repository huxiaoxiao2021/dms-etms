package com.jd.bluedragon.distribution.reverse.domain;

import com.jd.bluedragon.distribution.api.request.Eclp2BdReceiveDetail;

import java.util.Date;
import java.util.List;

public class ReverseReceive {
    
    public static final Integer RECEIVE_TYPE_SPWMS = 3;
    public static final Integer RECEIVE = 1;
    public static final Integer REJECT = 0;

    public static final Integer REVERSE_TYPE_C2C = 10;

    // 是否可收
    private Integer canReceive;
    
    // 创建时间
    private Date createTime;
    
    // 数据指纹
    private String fingerprint;
    
    // 自增主键
    private Long id;
    
    // 操作人编号
    private String operatorId;
    
    // 操作人
    private String operatorName;
    
    // 机构ID
    private Integer orgId;
    private Integer storeId;
    
    public Integer getStoreId() {
        return this.storeId;
    }
    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
    
    // 包裹编号
    private String packageCode;
    
    // 取件单号
    private String pickWareCode;
    
    // 收货时间
    private Date receiveTime;
    
    // 收货类型
    private Integer receiveType;
    
    // 拒收编码
    private Integer rejectCode;
    
    // 拒收原因
    private String rejectMessage;
    
    // 发货批次
    private String sendCode;
    
    // 最有一次修改时间
    private Date updateTime;
    
    // 是否删除
    private Integer yn;

    // 商品明细
    private List<Eclp2BdReceiveDetail> detailList;

    public List<Eclp2BdReceiveDetail> getDetailList() {
        return detailList;
    }

    public void setDetailList(List<Eclp2BdReceiveDetail> detailList) {
        this.detailList = detailList;
    }
    
    public Integer getCanReceive() {
        return this.canReceive;
    }
    
    public Date getCreateTime() {
    	return createTime!=null?(Date)createTime.clone():null;
    }
    
    public String getFingerprint() {
        return this.fingerprint;
    }
    
    public Long getId() {
        return this.id;
    }
    
    public String getOperatorId() {
        return this.operatorId;
    }
    
    public String getOperatorName() {
        return this.operatorName;
    }
    
    public Integer getOrgId() {
        return this.orgId;
    }
    
    public String getPackageCode() {
        return this.packageCode;
    }
    
    public String getPickWareCode() {
        return this.pickWareCode;
    }
    
    public Date getReceiveTime() {
    	return receiveTime!=null?(Date)receiveTime.clone():null;
    }
    
    public Integer getReceiveType() {
        return this.receiveType;
    }
    
    public Integer getRejectCode() {
        return this.rejectCode;
    }
    
    public String getRejectMessage() {
        return this.rejectMessage;
    }
    
    public String getSendCode() {
        return this.sendCode;
    }
    
    public Date getUpdateTime() {
    	return updateTime!=null?(Date)updateTime.clone():null;
    }
    
    public Integer getYn() {
        return this.yn;
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
    
    public void setReceiveTime(Date receiveTime) {
    	this.receiveTime = receiveTime!=null?(Date)receiveTime.clone():null;
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
    private String orderId;
    
    private String waybillCode;
    
    private Date businessDate;
    
    public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Date getBusinessDate() {
		return businessDate!=null?(Date)businessDate.clone():null;
	}

	public void setBusinessDate(Date businessDate) {
		this.businessDate = businessDate!=null?(Date)businessDate.clone():null;
	}

    public String getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(String orderId) {
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
