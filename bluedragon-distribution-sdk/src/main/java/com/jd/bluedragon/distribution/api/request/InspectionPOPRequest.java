package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-9 上午09:52:29
 *
 * POP PDA收货
 */
public class InspectionPOPRequest extends JdRequest {
	private static final long serialVersionUID = 1L;

	/**
	 * POP 第三方运单
	 */
	private String boxCode;
	
	/**
	 * POP（京东）运单
	 */
	private String boxCodeNew;
	
	private String packageBarcode;
	
	/**
     * POP商家ID
     */
    private Integer popSupId;
    
    /**
     * POP商家名称
     */
    private String popSupName;
    
    /**
     * 数量
     */
    private Integer quantity;
    
    /**
     * 滑道号
     */
    private String crossCode;
    
    /** 运单类型(JYN) */
    private Integer type;
    
    /**
     * 排队号
     */
    private String queueNo;
    
    /**
     * 排队类型：
     * 	1，POP商家直送
     *  2，托送
     */
    private Integer queueType;
    
    /**
     * 托寄公司ID
     */
    private String expressCode;
    
    /**
     * 托寄公司名称
     */
    private String expressName;
    
    /**
	 * B商家ID
	 */
	private Integer busiId;
	
	/**
	 * B商家名称
	 */
	private String busiName;

    /**
     * 是否取消鸡毛信服务；
     * 根据waybillsign确认是鸡毛信运单 才有用
     * true 取消，false 不取消
     */
	private Boolean cancelFeatherLetter;

	private String featherLetterDeviceNo;
    
	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getBoxCodeNew() {
		return boxCodeNew;
	}

	public void setBoxCodeNew(String boxCodeNew) {
		this.boxCodeNew = boxCodeNew;
	}

	public String getPackageBarcode() {
		return packageBarcode;
	}

	public void setPackageBarcode(String packageBarcode) {
		this.packageBarcode = packageBarcode;
	}

	public Integer getPopSupId() {
		return popSupId;
	}

	public void setPopSupId(Integer popSupId) {
		this.popSupId = popSupId;
	}

	public String getPopSupName() {
		return popSupName;
	}

	public void setPopSupName(String popSupName) {
		this.popSupName = popSupName;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCrossCode() {
		return crossCode;
	}

	public void setCrossCode(String crossCode) {
		this.crossCode = crossCode;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public Integer getQueueType() {
		return queueType;
	}

	public void setQueueType(Integer queueType) {
		this.queueType = queueType;
	}

	public String getExpressCode() {
		return expressCode;
	}

	public void setExpressCode(String expressCode) {
		this.expressCode = expressCode;
	}

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String expressName) {
		this.expressName = expressName;
	}

	public Integer getBusiId() {
		return busiId;
	}

	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

    public Boolean getCancelFeatherLetter() {
        return cancelFeatherLetter;
    }

    public void setCancelFeatherLetter(Boolean cancelFeatherLetter) {
        this.cancelFeatherLetter = cancelFeatherLetter;
    }

    public String getFeatherLetterDeviceNo() {
        return featherLetterDeviceNo;
    }

    public void setFeatherLetterDeviceNo(String featherLetterDeviceNo) {
        this.featherLetterDeviceNo = featherLetterDeviceNo;
    }
}
