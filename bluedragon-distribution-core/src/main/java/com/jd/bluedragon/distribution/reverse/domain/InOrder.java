package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 对外（配送入）接口对象
 * @author qiYunFeng
 */
public class InOrder implements Serializable {

    private String fromPin;

    private String fromName;

    private String createReason;

    private Integer aimOrgId;

    private Integer sourceId;

    private Integer aimStoreId;
    
    private Integer orderType;
    
    private Long orderId;
    
    private List<OrderDetail> orderDetail;
    
    private static final long serialVersionUID = 1L;
    
    private Integer flashOrgId;
    
    private Integer flashStoreId;
    
    public Integer getFlashStoreId() {
		return flashStoreId;
	}

	public void setFlashStoreId(Integer flashStoreId) {
		this.flashStoreId = flashStoreId;
	}

	public Integer getFlashOrgId() {
		return flashOrgId;
	}

	public void setFlashOrgId(Integer flashOrgId) {
		this.flashOrgId = flashOrgId;
	}

	public void setFromPin(String fromPin) {
		this.fromPin = fromPin;
	}

	public String getFromPin() {
		return fromPin;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getFromName() {
		return fromName;
	}

	public void setCreateReason(String createReason) {
		this.createReason = createReason;
	}

	public String getCreateReason() {
		return createReason;
	}

	public void setAimOrgId(Integer aimOrgId) {
		this.aimOrgId = aimOrgId;
	}

	public Integer getAimOrgId() {
		return aimOrgId;
	}

	public void setAimStoreId(Integer aimStoreId) {
		this.aimStoreId = aimStoreId;
	}

	public Integer getAimStoreId() {
		return aimStoreId;
	}

	public void setOrderDetail(List<OrderDetail> orderDetail) {
		this.orderDetail = orderDetail;
	}

	public List<OrderDetail> getOrderDetail() {
		return orderDetail;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

}
