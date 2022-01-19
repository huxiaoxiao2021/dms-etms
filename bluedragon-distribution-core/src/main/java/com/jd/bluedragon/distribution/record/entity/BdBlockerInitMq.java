package com.jd.bluedragon.distribution.record.entity;

import java.io.Serializable;
import java.util.Date;

import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;

/**
 * 
 * @author wuyoude
 *
 */
public class BdBlockerInitMq implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private String waybillCode;

    private Integer featureType;
    /**
     * 业务类型
     */
    private String businessType;

    private Integer exceptionId = 1;

    /**
     * 拦截类型  1:取消订单拦截,2:拒收订单拦截,3:恶意订单拦截,4:分拣中心拦截,5:仓储异常拦截,6:白条强制拦截
     */
    private Integer interceptType;

    /**
     * 拦截方式 1:提示 2：强制
     */
    private Integer interceptMode;

    private String messageType;

    private String createTime;

    private String updateTime;

    private String remark;

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public Integer getFeatureType() {
		return featureType;
	}

	public void setFeatureType(Integer featureType) {
		this.featureType = featureType;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public Integer getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(Integer exceptionId) {
		this.exceptionId = exceptionId;
	}

	public Integer getInterceptType() {
		return interceptType;
	}

	public void setInterceptType(Integer interceptType) {
		this.interceptType = interceptType;
	}

	public Integer getInterceptMode() {
		return interceptMode;
	}

	public void setInterceptMode(Integer interceptMode) {
		this.interceptMode = interceptMode;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
