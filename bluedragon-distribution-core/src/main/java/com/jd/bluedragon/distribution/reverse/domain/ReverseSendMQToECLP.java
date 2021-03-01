package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

/**
 * Created by guoyongzhi on 2015/2/4.
 * ECLP订单发送mq model
 */
public class ReverseSendMQToECLP {

    private String jdOrderCode;
    private String sendCode;
    private String sourceCode;
    private String waybillCode;
    private Integer rejType;
    private String rejRemark;
    /*拒收原因编码*/
    private Integer refuseReasonId;
    /*拒收原因描述*/
    private String refuseReasonName;
    /*操作时间*/
    private Long operateTime;
    /*操作人姓名*/
    private String operator;

    /*
    * 配送中心编码，天音项目增加
    * */
    private Integer distributeNo;

    /*
    * 库房编号，天音项目增加
    * */
    private Integer warehouseNo;
	/**
	 * 0：客退入，为1：病单入，为2：预售入
	 */
	private Integer guestBackType;

    //包裹号
	private List<String> packageCodeList;

	//暂存类型 ，其值见 StoreTypeEnum
    private Integer storeType;

    public String getJdOrderCode() {
        return jdOrderCode;
    }

    public void setJdOrderCode(String jdOrderCode) {
        this.jdOrderCode = jdOrderCode;
    }

    public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getRejType() {
        return rejType;
    }

    public void setRejType(Integer rejType) {
        this.rejType = rejType;
    }

    public String getRejRemark() {
        return rejRemark;
    }

    public void setRejRemark(String rejRemark) {
        this.rejRemark = rejRemark;
    }

    public Integer getRefuseReasonId() {
        return refuseReasonId;
    }

    public void setRefuseReasonId(Integer refuseReasonId) {
        this.refuseReasonId = refuseReasonId;
    }

    public String getRefuseReasonName() {
        return refuseReasonName;
    }

    public void setRefuseReasonName(String refuseReasonName) {
        this.refuseReasonName = refuseReasonName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getDistributeNo() {
        return distributeNo;
    }

    public void setDistributeNo(Integer distributeNo) {
        this.distributeNo = distributeNo;
    }

    public Integer getWarehouseNo() {
        return warehouseNo;
    }

    public void setWarehouseNo(Integer warehouseNo) {
        this.warehouseNo = warehouseNo;
    }

	public Integer getGuestBackType() {
		return guestBackType;
	}

	public void setGuestBackType(Integer guestBackType) {
		this.guestBackType = guestBackType;
	}

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }
}
