package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

/**
 * @author zhaohc 
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-3-15 下午08:26:17
 *
 * POP打印Request对象
 */
public class PopPrintRequest extends JdObject {
	private static final long serialVersionUID = 1L;

    /**
     * 龙门架驻厂：无打印
     */
    public static final Integer NOT_PRINT_PACK_TYPE = -1;

	/**
	 * 打印包裹
	 */
	public static final Integer PRINT_PACK_TYPE = 1;
	
	/**
	 * 打印发票
	 */
	public static final Integer PRINT_INVOICE_TYPE = 2;
	
	/**
	 * 接货类型-4-配送员接货
	 */
	public static final Integer POP_RECEIVE_TYPE_4 = 4;

	/**
	 * 接货类型-5
	 */
	public static final Integer POP_RECEIVE_TYPE_5 = 5;
	
	/**
	 * 业务类型-平台打印
	 */
	public static final Integer BUS_TYPE_PLATFORM_PRINT = 1;
	/**
	 * 业务类型-站点平台打印
	 */
	public static final Integer BUS_TYPE_SITE_PLATFORM_PRINT = 2;
	/**
	 * 业务类型-驻厂打印
	 */
	public static final Integer BUS_TYPE_IN_FACTORY_PRINT = 0;
	/**
	 * 运单号
	 */
	private String waybillCode;
	
	private String packageBarcode;
	
	/**
	 * 操作人编号
	 */
	private Integer operatorCode;
	
	/**
	 * 操作人名称
	 */
	private String operatorName;
	
	/**
	 * 操作人站点编号
	 */
	private Integer operateSiteCode;
	
	/**
	 * 操作人站点名称
	 */
	private String operateSiteName;
	
	/**
	 * 操作类型
	 */
	private Integer operateType;
	
	/**
	 * 操作时间
	 */
	private String operateTime;
	
	/**
     * POP商家ID
     */
    private Integer popSupId;
    
    /**
     * POP商家名称
     */
    private String popSupName;
    
    /**
     * 包裹数量
     */
    private Integer quantity;
    
    /**
     * 滑道号
     */
    private String crossCode;

    /** 运单类型(JYN) */
    private Integer waybillType;
    
    /**
     * POP收货类型：
     * 	商家直送：1
     * 	托寄送货：2
     *  配送员接货4
     */
    private Integer popReceiveType;
    
    /**
	 * 三方运单号
	 */
	private String thirdWaybillCode;
	
	/**
     * 排队号
     */
    private String queueNo;
    
    private String boxCode;
    
    private String driverCode;
    
    private String driverName;
    
    /**
	 * B商家ID
	 */
	private Integer busiId;
	
	/**
	 * B商家名称
	 */
	private String busiName;
	/**
	 * 业务类型-区分1-平台打印和2-站点平台打印
	 */
	private Integer businessType;
	/**
	 *打印入口类型
	 */
	public Integer interfaceType;

	public Integer getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(Integer interfaceType) {
		this.interfaceType = interfaceType;
	}


	/**
	 * 包裹托寄物名称
	 */
	private String categoryName;

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

	public Integer getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(Integer operatorCode) {
		this.operatorCode = operatorCode;
	}

	public Integer getOperateSiteCode() {
		return operateSiteCode;
	}

	public void setOperateSiteCode(Integer operateSiteCode) {
		this.operateSiteCode = operateSiteCode;
	}

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public String getOperateSiteName() {
		return operateSiteName;
	}

	public void setOperateSiteName(String operateSiteName) {
		this.operateSiteName = operateSiteName;
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

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

	public Integer getPopReceiveType() {
		return popReceiveType;
	}

	public void setPopReceiveType(Integer popReceiveType) {
		this.popReceiveType = popReceiveType;
	}

	public String getThirdWaybillCode() {
		return thirdWaybillCode;
	}

	public void setThirdWaybillCode(String thirdWaybillCode) {
		this.thirdWaybillCode = thirdWaybillCode;
	}

	public String getQueueNo() {
		return queueNo;
	}

	public void setQueueNo(String queueNo) {
		this.queueNo = queueNo;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public String getDriverCode() {
		return driverCode;
	}

	public void setDriverCode(String driverCode) {
		this.driverCode = driverCode;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
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

	/**
	 * @return the businessType
	 */
	public Integer getBusinessType() {
		return businessType;
	}

	/**
	 * @param businessType the businessType to set
	 */
	public void setBusinessType(Integer businessType) {
		this.businessType = businessType;
	}

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
